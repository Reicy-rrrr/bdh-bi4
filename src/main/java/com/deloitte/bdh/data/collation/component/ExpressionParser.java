package com.deloitte.bdh.data.collation.component;

import com.deloitte.bdh.common.exception.BizException;
import com.google.common.collect.Sets;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 组件公式解析器
 *
 * @author chenghzhang
 * @date 2020/12/15
 */
public class ExpressionParser {
    /**
     * 参数格式正则
     **/
    private static final String param_regex = "\\$\\{(.+?)\\}";
    /**
     * 数字格式正则
     **/
    private static final String number_regex = "^(\\-|\\+)?\\d+(\\.\\d+)?$";
    /**
     * 脚本引擎
     **/
    private static final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("javascript");

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
     * 特殊符号：左括号
     **/
    private static final String operator_left_bracket = "(";
    /**
     * 特殊符号：右括号
     **/
    private static final String operator_right_bracket = ")";
    /**
     * 自定义方法
     */
    private static final Set<String> functionList = Sets.newHashSet();

    /**
     * 验证表达式是否为有效的公式
     *
     * @param expression 计算表达式
     * @return boolean
     */
    public static boolean isFormula(String expression) {
        String result;
        try {
            result = scriptEngine.eval(expression.replace(" ", "")).toString();
        } catch (Exception e) {
            return false;
        }
        Matcher matcher = Pattern.compile(number_regex).matcher(result);
        return matcher.matches();
    }

    /**
     * 验证表达式是否为有效的公式（带参数的）
     *
     * @param expression 计算表达式
     * @return boolean
     */
    public static boolean isParamFormula(String expression) {
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
            return false;
        }
        Matcher matcher = Pattern.compile(number_regex).matcher(result);
        return matcher.matches();
    }

    /**
     * 获取公式中的变量
     *
     * @param expression 计算公式
     * @return
     */
    public static List<String> getParams(String expression) {
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
    public static List<String> getUniqueParams(String expression) {
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
    public static String formatParam(String expression, Map<String, String> data) {
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
     * @param formula      方程式
     * @return String
     */
    public static String formatFormula(String formula) {
        Queue postQueue = reverseToPost(formula);
        String resultExpression = execute(postQueue);
        return resultExpression;
    }

    /**
     * 中缀转后缀表达式
     *
     * @param formula     方程式
     * @return Queue
     */
    private static Queue reverseToPost(String formula) {
        // 存放后序表达式
        Queue resultQueue = new ArrayDeque();
        // 去除表达式中的空格
        formula = formula.replace(" ", "");
        // 定义临时存储运算符的栈
        Stack stack = new Stack();
        StringBuilder tempStringBuilder = new StringBuilder();
        for (int i = 0; i < formula.length(); i++) {
            tempStringBuilder = tempStringBuilder.append(formula.charAt(i));
            if (i + 2 <= formula.length() && !isOperator(String.valueOf(formula.charAt(i + 1))) && !isOperator(tempStringBuilder.toString())) {
                continue;
            }
            String temp = tempStringBuilder.toString();
            // StringBuilder
            tempStringBuilder = new StringBuilder();
            // 如果“(”直接入栈
            if (functionList.contains(temp)) {
                stack.push(temp);
                resultQueue.add(temp);
            } else if (operator_left_bracket.equals(temp)) {
                stack.push(temp);
                // 如果“）”直接入栈，运算符出栈进入队列，直到遇到“（”，并去除“（”
            } else if (operator_right_bracket.equals(temp)) {
                while (true) {
                    if (operator_left_bracket.equals(stack.peek())) {
                        stack.pop();
                        if (!stack.empty() && functionList.contains(stack.peek())) {
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
            } else if (isOperator(temp)) {
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
     * @param postQueue     四则运算的后序队列
     * @return String（格式化后的运算表达式）
     */
    private static String execute(Queue postQueue) {
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
            if (functionList != null && functionList.contains(content)) {
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
                    stack.add(executeFunction(functionName, functionParamStack, ExpressionParser.class));
                    functionParamStack = new Stack();
                } else {
                    // 没有配对方法，直接入栈
                    stack.push(content);
                    functionStack.push(content);
                }
            } else {
                // 如果是运算符进行运算
                if (isOperator(content)) {
                    String rightParam = String.valueOf(stack.pop());
                    String leftParam = String.valueOf(stack.pop());
                    stack.push(operation(leftParam, rightParam, content));
                    // 如果不是运算符直接放入到运算结果中等待运算
                } else {
                    stack.push(content);
                }
            }

        }
        // 显示计算结果
        return stack.pop().toString();
    }

    /**
     * 两个数进行运算
     *
     * @param leftParam  左边计算参数
     * @param rightParam 右边计算参数
     * @param operator   运算符
     * @return String（格式化后的运算表达式）
     */
    private static String operation(String leftParam, String rightParam, String operator) {
        // 除法运算需要特殊处理除数为0的情况
        if (operator_divide.equals(operator)) {
            StringBuilder divisor = new StringBuilder();
            divisor.append("(CASE WHEN ").append(rightParam).append("=0 THEN NULL ELSE ").append(rightParam).append(" END)");
            rightParam = divisor.toString();
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
    private static String executeFunction(String functionName, Stack functionParamStack, Class functionClazz) {
        String result = null;
        try {
            // 获取对应方法
            Method get = functionClazz.getMethod(functionName, Stack.class);
            // 获取方法返回值
            result = (String) get.invoke(null, functionParamStack);
        } catch (Exception e) {
            throw new BizException("方法[" + functionName + "]格式化错误，请检查参数正确性！");
        }
        return result;
    }


    /**
     * 获取运算符优先级
     *
     * @param content 计算内容
     * @return int
     */
    private static int getPriority(String content) {
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
     * 判断是不是运算符
     *
     * @param content 计算内容
     * @return boolean
     */
    private static boolean isOperator(String content) {
        if (operator_add.equals(content) || operator_subtract.equals(content)
                || operator_multiply.equals(content) || operator_divide.equals(content)
                || operator_separator.equals(content) || operator_left_bracket.equals(content)
                || operator_right_bracket.equals(content)) {
            return true;
        }
        return false;
    }
}
