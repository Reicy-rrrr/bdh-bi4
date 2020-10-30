package com.deloitte.bdh.data.collation.component;

import com.deloitte.bdh.data.collation.component.model.ComponentModel;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 组件接口
 *
 * @author chenghzhang
 * @date 2020/10/26
 */
public interface ComponentHandler {

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
     * 创建sql语句
     *
     * @param component 组件模型对象
     * @return String
     */
    void handle(ComponentModel component);

    /**
     * 重命名字段
     *
     * @param columnName
     * @return
     */
    default String renameColumn(String columnName) {
        return "BI" + DigestUtils.md5Hex(columnName).toUpperCase().substring(0, 16);
    }
}
