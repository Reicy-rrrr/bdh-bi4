package com.deloitte.bdh.data.collation.component;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 组件接口
 *
 * @author chenghzhang
 * @date 2020/11/09
 */
public interface Component {
    /**
     * 别名前缀
     */
    String alias_prefix = "BI";

    String sql_key_select = "SELECT ";
    String sql_key_create = "CREATE TABLE IF NOT EXISTS ";
    String sql_key_insert = "INSERT INTO ";
    String sql_key_from = "FROM ";
    String sql_key_inner_join = "INNER JOIN ";
    String sql_key_left_join = "LEFT JOIN ";
    String sql_key_full_join = "FULL JOIN ";
    String sql_key_where = "WHERE ";
    String sql_key_on = "ON ";
    String sql_key_as = "AS ";
    String sql_key_and = "AND ";
    String sql_key_group_by = "GROUP BY ";
    String sql_key_order_by = "ORDER BY ";
    String sql_key_having = "HAVING ";

    String sql_key_count = "COUNT";
    String sql_key_max = "MAX";
    String sql_key_min = "MIN";
    String sql_key_sum = "SUM";
    String sql_key_avg = "AVG";

    String sql_key_blank = " ";
    String sql_key_comma = ",";
    String sql_key_separator = ".";
    String sql_key_equal = "=";
    String sql_key_bracket_left = "(";
    String sql_key_bracket_right = ") ";
    /**
     * 换行符
     */
    String line_separator = System.getProperty("line.separator");

    /**
     * 获取字段别名（对原字段名进行MD5加密并转大写，然后截取前16位后加上固定前缀）
     *
     * @param columnName 表名 + "." + 字段名 （example: tb_user.name）
     * @return
     */
    default String getColumnAlias(String columnName) {
        return alias_prefix + DigestUtils.md5Hex(columnName).toUpperCase().substring(0, 16);
    }
}
