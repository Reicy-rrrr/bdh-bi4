package com.deloitte.bdh.data.collation.component.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 连接模型
 *
 * @author chenghzhang
 * @date 2020/10/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinModel {
    /** 表名 */
    private String tableName;

    /** 关联层级 */
    private int level;

    /** 关联类型：left，inner，full */
    private String joinType;

    /** 左边表名 */
    private String leftTableName;

    /** 左边表字段 */
    private String leftField;

    /** 右边表字段 */
    private String rightField;

    /** 右边的表 */
    @JsonIgnore
    private List<JoinModel> right;
}
