package com.deloitte.bdh.data.collation.enums;

import com.deloitte.bdh.common.exception.BizException;

/**
 * 计算公式
 * 多个示例使用";"进行分割，以便接口返回list
 * @author chenghzhang
 * @date 2020/12/25
 */
public enum CalculateOperatorEnum {
    ABS("abs", "abs(number)", "返回给定数字的绝对值", "abs(-7)=7"),
    CEILING("ceiling", "ceiling(number)", "将数字向上取整，舍入为等于或大于值的最接近整数", "ceiling(3.1415)=4"),
    MAX("max", "max(number1, number2)", "返回两个字段值的较大值", "max(100,120)=120"),
    MIN("min", "min(number1, number2)", "返回两个字段值的较小值", "min(100,120)=100"),
    ADD("+", "加号 +", "数值内容相加", "2+3=5"),
    SUBTRACT("-", "减号 -", "数值内容相减", "5-3=2"),
    MULTIPLY("*", "乘号 *", "数值内容相乘", "2*3=6"),
    DIVIDE("/", "除号 /", "数值内容相除", "6/3=2"),
    PARENTHESES("()", "括号 ()", "计算优先级", "5*(3-2)=5"),
    IF("if", "if [expr] then [v1] elseif [expr2] then [v2] … else [v3] end", "如果表达式expr成立,则返回结果v1,如果表达式expr2成立,则返回结果v2,否则返回v3", "if [${利润}>0] then ['盈利'] elseif ${利润=0} then ['平衡'] else ['亏损'] end;if ([${折扣}<0] or [${折扣}=0]) then ['无折扣'] elseif ([${折扣}<0.5] and [${折扣}>0]) then ['低折扣'] else ['高折扣'] end;"),
    CASE("case", "case [expr] when [v1] then [r1] … else [r2] end", "如果表达式expr等于v1,则返回结果r1,否则返回结果r2", "case [${销售人员}] when ['王晓义'] then ['华东经理'] when ['黄成橵'] then ['华南经理'] else ['普通销售'] end"),
    AND("and", "IF [expr1] and [expr3]", "对两个表达式进行逻辑或操作", "([${name}='张三'] and [${age}>25])"),
    OR("or", "IF [expr1] or [expr2] ", "对两个表达式进行逻辑合取操作", "([${name}='张三'] or [${age}>25])"),
    END("end", "end", "结束一个if或者case", "if [${利润}>0] then ['盈利'] elseif ${利润=0} then ['平衡'] else ['亏损'] end"),
    WHEN("when", "when ", "计算中的表达式连接", "case [${销售人员}] when ['王晓义'] then ['华东经理'] when ['黄成橵'] then ['华南经理'] else ['普通销售'] end"),
    IF_NULL("ifnull", "ifnull(expr1,expr2)", "如果[expr1]不为null，则返回他，否则返回[expr2]", "ifnull(${利润},0)"),
    ELSE("else", "else", "计算中的表达式连接", "if [${利润}<0] then ['盈利'] elseif [${利润}=0] then ['平衡'] else ['亏损'] end"),
    THEN("then", "then", "计算中的表达式连接", "if [${利润}<0] then ['盈利'] elseif [${利润}=0] then ['平衡'] else ['亏损'] end"),
    ELSEIF("elseif", "elseif", "计算中的表达式连接", "if[${利润}>0] then ['盈利'] elseif [${利润}=1] then ['平衡'] else ['亏损'] end"),
    DATE_DIFF("datediff", "datediff(expr1,expr2)", "两个日期之间的天数", "datediff('2020-01-01','2020-01-05')=4"),
    TODAY("today", "today()", "返回当前日期", "today()='2020-12-21'"),
    NOW("now", "now()", "返回当前日期和时间", "now()='2020-12-21 14:35:21'"),
    ADD_DATE("adddate", "adddate(expr1,expr2)", "日期相加", "adddate('2020-01-01',4)='2020-01-05'"),
    DAY("day", "day()", "以整数返回给定日期的天", "day('2020-12-21')=21"),
    MONTH("month", "month()", "以整数返回给定日期的月", "month('2020-12-21')=12"),
    YEAR("year", "year()", "以整数返回给定日期的年", "year('2020-12-21')=2020"),
    LEFT("left", "left(string,num)", "返回给定字符串右起指定字符数", "left('deloitte',4)=delo"),
    RIGHT("right", "right(string,num)", "返回给定字符串左起指定字符数", "right('deloitte',5)=oitte"),
    MID("mid", "mid(string,start,leght)", "返回给定字符串起始位置和长度的截取。字符串中的第一个字符位置为1；超过长度，则返回字符串结束的说有字符", "mid('deloitte',1,20)='deloitte'"),
    ;

    /**
     * 公式
     **/
    private String operator;
    /**
     * 公式名称
     **/
    private String name;
    /**
     * 公式描述
     **/
    private String desc;
    /**
     * 公式示例
     **/
    private String example;

    CalculateOperatorEnum(String operator, String name, String desc, String example) {
        this.operator = operator;
        this.name = name;
        this.desc = desc;
        this.example = example;
    }

    /**
     * 根据类型获取枚举
     *
     * @param operator
     * @return
     */
    public static CalculateOperatorEnum get(String operator) {
        CalculateOperatorEnum[] enums = CalculateOperatorEnum.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getOperator().equals(operator)) {
                return enums[i];
            }
        }
        throw new BizException("暂不支持的公式类型！");
    }

    public String getOperator() {
        return operator;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getExample() {
        return example;
    }
}
