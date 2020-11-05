package com.deloitte.bdh.data.collation.model;

import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class RunPlan {
    private String groupCode;
    //0数据同步、1数据整理
    private String planType;
    private String first;
    private String modelCode;
    private BiEtlMappingConfig config;
    private String count;

    private String tableName;
    private String refCode;

    public static RunPlan builder() {
        return new RunPlan();
    }

    public RunPlan groupCode(String groupCode) {
        this.groupCode = groupCode;
        return this;
    }

    public RunPlan planType(String planType) {
        this.planType = planType;
        return this;
    }

    public RunPlan first(String first) {
        this.first = first;
        return this;
    }

    public RunPlan modelCode(String modelCode) {
        this.modelCode = modelCode;
        return this;
    }

    public RunPlan mappingConfigCode(BiEtlMappingConfig config) {
        this.config = config;
        this.refCode = config.getCode();
        return this;
    }

    public RunPlan synCount() throws Exception {
        String condition = null;
        if (config.getType().equals(SyncTypeEnum.INCREMENT.getValue())) {
            if (StringUtils.isNotBlank(config.getOffsetValue())) {
                condition = "'" + config.getOffsetField() + "' > =" + "'" + config.getOffsetValue() + "'";
            }
        }

        DbContext context = new DbContext();
        context.setDbId(config.getRefSourceId());
        context.setTableName(config.getFromTableName());
        context.setCondition(condition);
        DbSelector selector = SpringUtil.getBean("dbSelector", DbSelector.class);
        this.count = String.valueOf(selector.getTableCount(context));
        this.tableName = config.getToTableName();
        return this;
    }

    public RunPlan refCode(String refCode) {
        this.refCode = refCode;
        return this;
    }

    public RunPlan etlCount(String tableName) throws Exception {
        DbHandler handler = SpringUtil.getBean("dbHandler", DbHandler.class);
        long nowCount = handler.getCount(tableName, null);
        this.count = String.valueOf(nowCount);
        this.tableName = tableName;
        return this;
    }


}
