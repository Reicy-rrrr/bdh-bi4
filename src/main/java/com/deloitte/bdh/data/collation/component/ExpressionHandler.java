package com.deloitte.bdh.data.collation.component;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import com.deloitte.bdh.data.collation.enums.CalculateTypeEnum;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 组件公式处理器
 *
 * @author chenghzhang
 * @date 2020/12/15
 */
@Component
public class ExpressionHandler {

    @Resource
    private LocaleMessageService localeMessageService;
    /**
     * 参数格式正则
     **/
    private static final String param_regex = "\\$\\{(.+?)\\}";
    /**
     * 数字格式正则
     **/
    private static final String number_regex = "^(\\-|\\+)?\\d+(\\.\\d+)?$";
    /**
     * 运算符正则：+ - * / ( , )
     **/
    private static final String operator_regex = "^[\\+\\-\\*\\/\\(\\,\\)]$";
    /**
     * sql中字符串值正则：'zhangsan'
     **/
    private static final String sql_string_value_regex = "^\\'(.+?)\\'$";
    /**
     * 脚本引擎
     **/
    public static final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("javascript");

    /**
     * 运算符：加
     **/
    private static final String operator_add = "+";
    /**
     * 运算符：减
     **/
    private static final String operator_subtract = "-";
    /**
     * 运算符：乘
     **/
    private static final String operator_multiply = "*";
    /**
     * 运算符：除
     **/
    private static final String operator_divide = "/";
    /**
     * 特殊符号：分隔
     **/
    private static final String operator_separator = ",";
    /**
     * 特殊符号：小括号
     **/
    private static final Pair<String, String> parentheses = new ImmutablePair("(", ")");
    /**
     * 特殊符号：中括号
     **/
    private static final Pair<String, String> square_brackets = new ImmutablePair("[", "]");
    /**
     * 自定义方法
     */
    private static final Map<String, String> functions = new HashMap();

    /**
     * 逻辑筛选
     */
    private static final Map<String, String> logics = new HashMap();

    private static final Set<String> arithmetic_operators = Sets.newHashSet("+", "-", "*", "/");

    private static final Set<String> relational_operators = Sets.newHashSet(">", "=", "<", ">=", "<=", "!=", "<>");

    private static final Set<String> logical_operators = Sets.newHashSet("and", "or");

    private static final Set<String> special_symbols = Sets.newHashSet("+", "-", "*", "/", "(", ",", ")", "[", "]");

    private ArrangerSelector arrangerSelector;

    static {
        // 求绝对值
        functions.put("abs", "abs");
        // 向上取整
        functions.put("ceiling", "ceil");
        // 最大值
        functions.put("max", "max");
        // 最小值
        functions.put("min", "min");

        logics.put("if", "if_");
        logics.put("elseif", "elseif_");
        logics.put("else", "else_");
        logics.put("case", "case_");
        logics.put("when", "when_");
        logics.put("then", "then_");
        logics.put("end", "end_");
        logics.put("ifnull", "ifnull_");
    }

    /**
     * 验证表达式是否为有效的公式
     *
     * @param expression 计算表达式
     * @return boolean
     */
    public Pair<Boolean, String> isArithmeticFormula(String expression) {
        String result;
        try {
            result = scriptEngine.eval(expression.replace(" ", "")).toString();
        } catch (Exception e) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_1.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (!isNumeric(result)) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_1.getMessage(), ThreadLocalHolder.getLang()));
        }
        return new ImmutablePair(Boolean.TRUE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_2.getMessage(), ThreadLocalHolder.getLang()));
    }

    /**
     * 验证表达式是否为有效的公式（带参数的）
     *
     * @param expression 计算表达式
     * @return boolean
     */
    public Pair<Boolean, String> isParamArithmeticFormula(String expression) {
        List<String> params = getUniqueParams(expression);
        Map<String, String> data = new HashMap(params.size());
        // 将所有参数用数字替换掉进行计算
        for (int i = 0; i < params.size(); i++) {
            data.put(params.get(i), String.valueOf((i + 1) * 10));
        }
        String finalExpression = formatParam(expression, data);
        String result;
        try {
            result = scriptEngine.eval(finalExpression.replace(" ", "")).toString();
        } catch (Exception e) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_1.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (!isNumeric(result)) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_1.getMessage(), ThreadLocalHolder.getLang()));
        }
        return new ImmutablePair(Boolean.TRUE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_3.getMessage(), ThreadLocalHolder.getLang()));
    }

    /**
     * 验证表达式是否为有效的公式（带参数的）
     *
     * @param expression 计算表达式
     * @return boolean
     */
    public Pair<Boolean, String> isParamFunctionFormula(String expression) {
        Queue queue = null;
        try {
            queue = reverseToPost(expression);
        } catch (EmptyStackException e) {
            // 出现异常（括号不匹配），错误的表达式
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_4.getMessage(), ThreadLocalHolder.getLang()));
        } catch (Exception e) {
            // 出现异常，错误的表达式
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_5.getMessage(), ThreadLocalHolder.getLang()));
        }
        // 后续队列为空，错误的表达式
        if (queue.isEmpty()) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_5.getMessage(), ThreadLocalHolder.getLang()));
        }
        Pattern operatorPa = Pattern.compile(operator_regex);
        Pattern paramPa = Pattern.compile(param_regex);
        boolean flag = true;
        while (!queue.isEmpty()) {
            // 获取当前操作的内容
            String content = (String) queue.poll();
            if (paramPa.matcher(content).matches()) {
                continue;
            }
            if (operatorPa.matcher(content).matches()) {
                continue;
            }
            if (isNumeric(content)) {
                continue;
            }
            if (functions.containsKey(content)) {
                continue;
            }
            flag = false;
            break;
        }

        if (!flag) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_6.getMessage(), ThreadLocalHolder.getLang()));
        }
        return new ImmutablePair(Boolean.TRUE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_3.getMessage(), ThreadLocalHolder.getLang()));
    }

    /**
     * 验证表达式是否为有效（主要校验关键字是否匹配）
     *
     * @param expression 计算表达式
     * @return boolean
     */
    public Pair<Boolean, String> isFormula(String expression) {
        // 检查小括号是否匹配
        if (getSubStringCount(expression, parentheses.getLeft()) != getSubStringCount(expression, parentheses.getRight())) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_4.getMessage(), ThreadLocalHolder.getLang()));
        }
        // 检查中括号是否匹配
        if (getSubStringCount(expression, square_brackets.getLeft()) != getSubStringCount(expression, square_brackets.getRight())) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_7.getMessage(), ThreadLocalHolder.getLang()));
        }
        int ifCount = getSubStringCount(expression, "if");
        int ifNullCount = getSubStringCount(expression, "ifnull");
        int elseIfCount = getSubStringCount(expression, "elseif");
        // 校验if时需要排除ifnull和elseIf的情况
        ifCount = ifCount - ifNullCount - elseIfCount;
        int caseCount = getSubStringCount(expression, "case");
        int whenCount = getSubStringCount(expression, "when");
        int thenCount = getSubStringCount(expression, "then");
        int elseCount = getSubStringCount(expression, "else");
        // 校验else时需要排除elseif的情况
        elseCount = elseCount - elseIfCount;
        int endCount = getSubStringCount(expression, "end");
        // 检查 if - else / case else  || if - end / case - end
        if (ifCount + caseCount != elseCount) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_8.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (ifCount + caseCount != endCount) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_9.getMessage(), ThreadLocalHolder.getLang()));
        }

        // 检查 if - elseIf
        if (ifCount == 0 && elseIfCount > 0) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_10.getMessage(), ThreadLocalHolder.getLang()));
        }
        // 检查 if - then / elseif - then / when - then
        if (ifCount + elseIfCount + whenCount != thenCount) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_11.getMessage(), ThreadLocalHolder.getLang()));
        }
        //
        if (caseCount == 0 && whenCount > 0 || caseCount > whenCount) {
            return new ImmutablePair(Boolean.FALSE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_12.getMessage(), ThreadLocalHolder.getLang()));
        }
        return new ImmutablePair(Boolean.TRUE, localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_3.getMessage(), ThreadLocalHolder.getLang()));
    }

    /**
     * 判断字符串是否为数字格式
     *
     * @param value 字符串值
     * @return boolean
     */
    public boolean isNumeric(String value) {
        Matcher matcher = Pattern.compile(number_regex).matcher(value);
        return matcher.matches();
    }

    /**
     * 判断字符串是否为sql中的字符串格式
     *
     * @param value 字符串值
     * @return boolean
     */
    public boolean isSqlStringValue(String value) {
        Matcher matcher = Pattern.compile(sql_string_value_regex).matcher(value);
        return matcher.matches();
    }

    /**
     * 获取表达式的计算类型
     *
     * @param expression 计算表达式
     * @return CalculateTypeEnum
     */
    public CalculateTypeEnum getCalculateType(String expression) {
        if (StringUtils.isBlank(expression)) {
            return CalculateTypeEnum.ORDINARY;
        }

        for (String s : logics.keySet()) {
            if (expression.contains(s)) {
                return CalculateTypeEnum.LOGICAL;
            }
        }

        for (String s : logical_operators) {
            if (expression.contains(s)) {
                return CalculateTypeEnum.LOGICAL;
            }
        }

        for (String s : functions.keySet()) {
            if (expression.contains(s)) {
                return CalculateTypeEnum.FUNCTION;
            }
        }
        return CalculateTypeEnum.ORDINARY;
    }

    /**
     * 获取字符串中子字符串的个数
     *
     * @param fullString 全字符串
     * @param subString  子字符串
     * @return 子字符串的个数
     */
    public int getSubStringCount(String fullString, String subString) {
        if (StringUtils.isEmpty(fullString) || StringUtils.isEmpty(subString)) {
            return 0;
        }

        String temp = fullString.replace(subString, "");
        int replacedLength = fullString.length() - temp.length();
        if (replacedLength == 0) {
            return 0;
        }
        return replacedLength / subString.length();
    }

    /**
     * 获取公式中的变量
     *
     * @param expression 计算公式
     * @return
     */
    public List<String> getParams(String expression) {
        Matcher matcher = Pattern.compile(param_regex).matcher(expression);
        List<String> list = new ArrayList();
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    /**
     * 获取公式中的变量（去重）
     *
     * @param expression 计算公式
     * @return
     */
    public List<String> getUniqueParams(String expression) {
        Matcher matcher = Pattern.compile(param_regex).matcher(expression);
        Set<String> params = new LinkedHashSet();
        while (matcher.find()) {
            params.add(matcher.group(1));
        }
        return new ArrayList(params);
    }

    /**
     * 格式化公式（设置变量值）
     *
     * @param expression 计算公式
     * @param data       变量值
     * @return
     */
    public String formatParam(String expression, Map<String, String> data) {
        Matcher m = Pattern.compile(param_regex).matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String key = m.group(1);
            String value = data.get(key);
            m.appendReplacement(sb, value == null ? "" : value);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 格式化方程式（对方程式进行特殊处理）
     *
     * @param formula 方程式
     * @return String
     */
    public String formatFormula(String formula) {
        Queue postQueue = reverseToPost(formula);
        String resultExpression = execute(postQueue);
        return resultExpression;
    }

    /**
     * 格式化方程式（对方程式进行特殊处理）
     *
     * @param formula 方程式
     * @return String
     */
    public String formatFormula(String formula, ArrangerSelector arranger) {
        this.arrangerSelector = arranger;
        Queue postQueue = reverseToPost(formula);
        String resultExpression = execute(postQueue);
        return resultExpression;
    }

    /**
     * 中缀转后缀表达式
     *
     * @param formula 方程式
     * @return Queue
     */
    private Queue reverseToPost(String formula) {
        // 存放后序表达式
        Queue resultQueue = new ArrayDeque();
        // 去除表达式中的空格
        formula = formula.replace(" ", "").replace("\n", "");
        // 定义临时存储运算符的栈
        Stack stack = new Stack();
        StringBuilder tempStringBuilder = new StringBuilder();
        for (int i = 0; i < formula.length(); i++) {
            // 当前位置字符
            char currChar = formula.charAt(i);
            // 下个位置字符
            char nextChar = ' ';
            if (i + 2 <= formula.length()) {
                nextChar = formula.charAt(i + 1);
            }
            tempStringBuilder = tempStringBuilder.append(currChar);
            // 当前字符和下个字符不是特殊符号时，往下进行
            if (!isSpecialSymbol(String.valueOf(currChar)) && !Character.isSpaceChar(nextChar)
                    && !isSpecialSymbol(String.valueOf(nextChar))) {
                continue;
            }

            String temp = tempStringBuilder.toString();
            // StringBuilder
            tempStringBuilder = new StringBuilder();
            if (functions.containsKey(temp) || logics.containsKey(temp)) {
                stack.push(temp);
                resultQueue.add(temp);
            } else if (square_brackets.getLeft().equals(temp)) {
                stack.push(temp);
                // 如果“]”直接入栈，运算符出栈进入队列，直到遇到“[”，并去除“]”
            } else if (square_brackets.getRight().equals(temp)) {
                while (true) {
                    // 如果最终都未找到“[”，说明表达式有问题
                    if (stack.isEmpty()) {
                        throw new EmptyStackException();
                    }
                    if (square_brackets.getLeft().equals(stack.peek())) {
                        stack.pop();
                        if (!stack.isEmpty() && (functions.containsKey(stack.peek()) || logics.containsKey(stack.peek()))) {
                            resultQueue.add(stack.pop());
                        }
                        break;
                    }
                    // ","只用来判断，不加入到后缀表达式中
                    String operatorString = stack.pop().toString();
                    if (!operator_separator.equals(operatorString)) {
                        resultQueue.add(operatorString);
                    }
                }
                // 如果“(”直接入栈
            } else if (parentheses.getLeft().equals(temp)) {
                stack.push(temp);
                // 如果“）”直接入栈，运算符出栈进入队列，直到遇到“（”，并去除“（”
            } else if (parentheses.getRight().equals(temp)) {
                while (true) {
                    // 如果最终都未找到“（”，说明表达式有问题
                    if (stack.isEmpty()) {
                        throw new EmptyStackException();
                    }
                    if (parentheses.getLeft().equals(stack.peek())) {
                        stack.pop();
                        if (!stack.isEmpty() && (functions.containsKey(stack.peek()) || logics.containsKey(stack.peek()))) {
                            resultQueue.add(stack.pop());
                        }
                        break;
                    }
                    // ","只用来判断，不加入到后缀表达式中
                    String operatorString = stack.pop().toString();
                    if (!operator_separator.equals(operatorString)) {
                        resultQueue.add(operatorString);
                    }
                }
            } else if (isArithmeticOperator(temp)) {
                // 如果是普通运算符，将运算优先级大于等于他的从栈中取出加入到队列中，最后将当前运算符入栈
                while (true) {
                    if (!stack.isEmpty() && getPriority(temp) <= getPriority((String) stack.peek())) {
                        // ","只用来判断，不加入到后缀表达式中
                        String operatorString = stack.pop().toString();
                        if (!operator_separator.equals(operatorString)) {
                            resultQueue.add(operatorString);
                        }
                    } else {
                        break;
                    }
                }
                // 兼容-1
                if (resultQueue.isEmpty() && operator_subtract.equals(temp)) {
                    resultQueue.add("0");
                }
                stack.push(temp);
            } else if (isLogicalOperator(temp)) {
                // 逻辑运算表达式
                if (resultQueue.isEmpty()) {
                    throw new BizException(ResourceMessageEnum.EXPRESS_13.getCode(),
                            localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_13.getMessage(), ThreadLocalHolder.getLang()), temp);
                }
                stack.push(temp);
            } else if (operator_separator.equals(temp)) {
                // ","
                stack.push(temp);
            } else {
                // 如果是数字直接进入队列
                resultQueue.add(temp);
            }
        }
        // 将剩余的运算符加入到队列中
        if (!stack.isEmpty()) {
            while (!stack.isEmpty()) {
                resultQueue.add(stack.pop());
            }
        }
        return resultQueue;
    }

    /**
     * 转换表达式
     *
     * @param postQueue 四则运算的后序队列
     * @return String（格式化后的运算表达式）
     */
    private String execute(Queue postQueue) {
        if (postQueue.isEmpty()) {
            return null;
        }
        // 存放计算结果
        Stack stack = new Stack();
        // 存放方法函数
        Stack functionStack = new Stack();
        // 存放方法函数
        Stack functionParamStack = new Stack();
        while (!postQueue.isEmpty()) {
            // 获取当前操作的内容
            String content = (String) postQueue.poll();
            // 是运算方法特殊处理
            if (functions.containsKey(content) || logics.containsKey(content)) {
                // 方法配对成功，获取入参进行运算
                if (!functionStack.isEmpty() && content.equals(functionStack.peek())) {
                    // 方法名
                    String functionName = (String) functionStack.pop();
                    while (true) {
                        // 获取方法名和方法入参
                        String functionParam = (String) stack.pop();
                        if (functionParam != functionName) {
                            functionParamStack.push(functionParam);
                        } else {
                            break;
                        }
                    }
                    // 对方法函数进行运算
                    if (stack.isEmpty()) {
                        stack.add(executeFunction(functionName, functionParamStack, ExpressionHandler.class));
                    } else {
                        // 如果结果中已经有记录，前面函数执行的结果，需要拼接到一起
                        String ahead = stack.pop().toString();
                        String behind = executeFunction(functionName, functionParamStack, ExpressionHandler.class);
                        stack.add(ahead + " " + behind);
                    }
                    functionParamStack = new Stack();
                } else {
                    // 没有配对方法，直接入栈
                    stack.push(content);
                    functionStack.push(content);
                }
            } else {
                // 如果是 计算运算符 或者 逻辑运算符 进行运算
                if (isArithmeticOperator(content) || isLogicalOperator(content)) {
                    String rightParam = String.valueOf(stack.pop());
                    String leftParam = String.valueOf(stack.pop());
                    stack.push(operation(leftParam, rightParam, content));
                    // 如果不是运算符直接放入到运算结果中等待运算
                } else {
                    stack.push(content);
                }
            }
        }

        if (stack.size() == 1) {
            return stack.pop().toString();
        }

        StringBuilder resultBuilder = new StringBuilder();
        while (true) {
            if (stack.isEmpty()) {
                break;
            }
            resultBuilder.append(stack.pop().toString());
        }
        // 显示计算结果
        return resultBuilder.toString();
    }

    /**
     * 两个数进行运算
     *
     * @param leftParam  左边计算参数
     * @param rightParam 右边计算参数
     * @param operator   运算符
     * @return String（格式化后的运算表达式）
     */
    private String operation(String leftParam, String rightParam, String operator) {
        // 除法运算需要特殊处理除数为0的情况
        if (operator_divide.equals(operator)) {
            StringBuilder divisor = new StringBuilder();
            divisor.append("(CASE WHEN ").append(rightParam).append("=0 THEN NULL ELSE ").append(rightParam).append(" END)");
            rightParam = divisor.toString();
        }

        if ("and".equals(operator) || "or".equals(operator)) {
            operator = " " + operator + " ";
        }
        return new StringBuilder("(").append(leftParam).append(operator).append(rightParam).append(")").toString();
    }

    /**
     * 获取自定义方法结果
     *
     * @param functionName       方法名称
     * @param functionParamStack 方法参数栈
     * @param functionClazz      方法所属类型
     * @return String（自定义方法格式化后的运算表达式）
     */
    private String executeFunction(String functionName, Stack functionParamStack, Class functionClazz) {
        String result = null;
        try {
            // 获取方法映射的名称
            String methodName = functions.get(functionName);
            if (StringUtils.isBlank(methodName)) {
                methodName = logics.get(functionName);
            }
            if (StringUtils.isBlank(methodName)) {
                throw new BizException(ResourceMessageEnum.EXPRESS_14.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_14.getMessage(), ThreadLocalHolder.getLang()), functionName);
            }
            // 获取对应方法
            Method method = functionClazz.getDeclaredMethod(methodName, Stack.class);
            // 在访问私有方法前设置访问操作(不设置直接调用会报错)
            method.setAccessible(true);
            // 通过当前对象获取方法返回值
            result = (String) method.invoke(this, functionParamStack);
        } catch (Exception e) {
            throw new BizException(ResourceMessageEnum.EXPRESS_15.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_15.getMessage(), ThreadLocalHolder.getLang()), functionName);
        }
        return result;
    }

    /**
     * 获取运算符优先级
     *
     * @param content 计算内容
     * @return int
     */
    private int getPriority(String content) {
        if (operator_add.equals(content) || operator_subtract.equals(content)) {
            return 1;
        } else if (operator_multiply.equals(content) || operator_divide.equals(content)) {
            return 2;
        } else if (operator_separator.equals(content)) {
            return 0;
        } else {
            return -2;
        }
    }

    /**
     * 判断是不是算术运算符
     *
     * @param content 计算内容
     * @return boolean
     */
    private boolean isArithmeticOperator(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }
        return arithmetic_operators.contains(content);
    }

    /**
     * 判断是不是逻辑运算符
     *
     * @param content 计算内容
     * @return boolean
     */
    private boolean isLogicalOperator(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }
        return logical_operators.contains(content);
    }

    /**
     * 判断是不是关系运算符
     *
     * @param content 计算内容
     * @return boolean
     */
    private boolean isRelationalOperator(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }
        return relational_operators.contains(content);
    }

    /**
     * 判断是不是特殊符号
     *
     * @param content 内容
     * @return boolean
     */
    private boolean isSpecialSymbol(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }
        return special_symbols.contains(content);
    }

    /**
     * 判断是不是函数
     *
     * @param content 计算内容
     * @return boolean
     */
    private boolean isFunction(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }
        return functions.containsKey(content);
    }

    /**
     * 取字段绝对值
     *
     * @param stack 去绝对值字段栈
     * @return 运算表达式
     */
    private String abs(Stack stack) {
        if (stack.isEmpty()) {
            throw new BizException(ResourceMessageEnum.EXPRESS_16.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_16.getMessage(), ThreadLocalHolder.getLang()));
        }
        String absField = stack.pop().toString();
        if (StringUtils.isBlank(absField)) {
            throw new BizException(ResourceMessageEnum.EXPRESS_16.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_16.getMessage(), ThreadLocalHolder.getLang()));
        }
        return "ABS(" + absField + ")";
    }

    /**
     * 字段向上取整
     *
     * @param stack 取整字段栈
     * @return 运算表达式
     */
    private String ceil(Stack stack) {
        if (stack.isEmpty()) {
            throw new BizException(ResourceMessageEnum.EXPRESS_17.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_17.getMessage(), ThreadLocalHolder.getLang()));
        }
        String ceilField = stack.pop().toString();
        if (StringUtils.isBlank(ceilField)) {
            throw new BizException(ResourceMessageEnum.EXPRESS_17.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_17.getMessage(), ThreadLocalHolder.getLang()));
        }

        if (arrangerSelector == null) {
            throw new BizException(ResourceMessageEnum.EXPRESS_18.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_18.getMessage(), ThreadLocalHolder.getLang()));
        }
        return arrangerSelector.calculateCeil(ceilField);
    }

    /**
     * 获取多个字段中最大值
     *
     * @param stack 参与计算字段栈
     * @return 运算表达式
     */
    private String max(Stack stack) {
        if (stack.isEmpty() || stack.size() < 2) {
            throw new BizException(ResourceMessageEnum.EXPRESS_19.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_19.getMessage(), ThreadLocalHolder.getLang()));
        }

        String maxField = stack.pop().toString();
        StringBuilder tempBuilder = new StringBuilder();
        while (!stack.isEmpty()) {
            String rightField = stack.pop().toString();
            tempBuilder.append("(CASE WHEN ").append(maxField).append(" > ").append(rightField)
                    .append(" THEN ").append(maxField).append(" ELSE ").append(rightField).append(" END)");
            maxField = tempBuilder.toString();
            tempBuilder.setLength(0);
        }
        return maxField;
    }

    /**
     * 获取多个字段中最小值
     *
     * @param stack 参与计算字段栈
     * @return 运算表达式
     */
    private String min(Stack stack) {
        if (stack.isEmpty() || stack.size() < 2) {
            throw new BizException("Component calculate error: 取最小值错误，字段个数不能小于2！");
        }
        String minField = stack.pop().toString();
        StringBuilder tempBuilder = new StringBuilder();
        while (!stack.isEmpty()) {
            String rightField = stack.pop().toString();
            tempBuilder.append("(CASE WHEN ").append(minField).append(" < ").append(rightField)
                    .append(" THEN ").append(minField).append(" ELSE ").append(rightField).append(" END)");
            minField = tempBuilder.toString();
            tempBuilder.setLength(0);
        }
        return minField;
    }

    private String if_(Stack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new BizException(ResourceMessageEnum.EXPRESS_20.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_20.getMessage(), ThreadLocalHolder.getLang()));
        }
        String condition = stack.pop().toString();
        return new StringBuilder().append(" CASE WHEN ").append(condition).append(" ").toString();
    }

    private String elseif_(Stack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new BizException(ResourceMessageEnum.EXPRESS_21.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_21.getMessage(), ThreadLocalHolder.getLang()));
        }
        String condition = stack.pop().toString();
        return new StringBuilder().append(" WHEN ").append(condition).append(" ").toString();
    }

    private String case_(Stack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new BizException(ResourceMessageEnum.EXPRESS_22.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_22.getMessage(), ThreadLocalHolder.getLang()));
        }
        String field = stack.pop().toString();
        return new StringBuilder().append(" CASE ").append(field).append(" ").toString();
    }

    private String when_(Stack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new BizException(ResourceMessageEnum.EXPRESS_23.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_23.getMessage(), ThreadLocalHolder.getLang()));
        }
        String condition = stack.pop().toString();
        if (!isNumeric(condition) && !isSqlStringValue(condition)) {
            // sql中单引号转义使用两个连续单引号
            condition = "'" + condition.replace("'", "''") + "'";
        }
        return new StringBuilder().append(" WHEN ").append(condition).append(" ").toString();
    }

    private String then_(Stack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new BizException(ResourceMessageEnum.EXPRESS_24.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_24.getMessage(), ThreadLocalHolder.getLang()));
        }
        String result = stack.pop().toString();
        if (!isNumeric(result) && !isSqlStringValue(result)) {
            // sql中单引号转义使用两个连续单引号
            result = "'" + result.replace("'", "''") + "'";
        }
        return new StringBuilder().append(" THEN ").append(result).append(" ").toString();
    }

    private String else_(Stack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new BizException(ResourceMessageEnum.EXPRESS_25.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_25.getMessage(), ThreadLocalHolder.getLang()));
        }

        String result = stack.pop().toString();
        if (!isNumeric(result) && !isSqlStringValue(result)) {
            // sql中单引号转义使用两个连续单引号
            result = "'" + result.replace("'", "''") + "'";
        }
        return new StringBuilder().append(" ELSE ").append(result).append(" ").toString();
    }

    private String end_(Stack stack) {
        return " END ";
    }

    private String and_(Stack stack) {
        if (stack == null || stack.size() < 2) {
            throw new BizException(ResourceMessageEnum.EXPRESS_26.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_26.getMessage(), ThreadLocalHolder.getLang()));
        }
        String condition1 = stack.pop().toString();
        String condition2 = stack.pop().toString();
        return new StringBuilder(" ").append(condition1).append(" AND ").append(condition2).append(" ").toString();
    }

    private String or_(Stack stack) {
        if (stack == null || stack.size() < 2) {
            throw new BizException(ResourceMessageEnum.EXPRESS_27.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_27.getMessage(), ThreadLocalHolder.getLang()));
        }
        String condition1 = stack.pop().toString();
        String condition2 = stack.pop().toString();
        return new StringBuilder(" ").append(condition1).append(" OR ").append(condition2).append(" ").toString();
    }

    private String ifnull_(Stack stack) {
        if (stack == null || stack.size() < 2) {
            throw new BizException(ResourceMessageEnum.EXPRESS_28.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.EXPRESS_28.getMessage(), ThreadLocalHolder.getLang()));
        }
        String field = stack.pop().toString();
        String value = stack.pop().toString();
        if (!isNumeric(value) && !isSqlStringValue(value)) {
            // sql中单引号转义使用两个连续单引号
            value = "'" + value.replace("'", "''") + "'";
        }
        return arrangerSelector.calculateIfNull(field, value);
    }
}
