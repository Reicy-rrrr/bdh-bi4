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

    /** 关联类型：left，inner，full */
    private String joinType;

    /** 左边表名 */
    private String leftTableName;

    /** 关联的字段 */
    private List<JoinFieldModel> joinFields;

    /** 右边的表 */
    @JsonIgnore
    private List<JoinModel> right;
}
