package com.deloitte.bdh.data.collation.model;

import com.deloitte.bdh.common.util.SpringUtil;
import com.deloitte.bdh.data.analyse.enums.WildcardEnum;
import com.deloitte.bdh.data.analyse.sql.utils.RelaBaseBuildUtil;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import com.deloitte.bdh.data.collation.database.DbSelector;
import com.deloitte.bdh.data.collation.database.dto.DbContext;
import com.deloitte.bdh.data.collation.enums.SyncTypeEnum;
import com.deloitte.bdh.data.collation.model.request.ConditionDto;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.ToString;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class RunPlan implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1458053651084390691L;
	private String groupCode;
    // 任务名称
    private String planName;
    //0数据同步、1数据整理
    private String planType;
    private String first;
    private String modelCode;
    private String cronExpression;
    private BiEtlMappingConfig config;
    private String count;

    private String tableName;
    //数据源为mappingCode ,etl为processorsCode
    private String refCode;

    public static RunPlan builder() {
        return new RunPlan();
    }

    public RunPlan groupCode(String groupCode) {
        this.groupCode = groupCode;
        return this;
    }

    public RunPlan planName(String planName) {
        this.planName = planName;
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

    public RunPlan cronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
        return this;
    }

    public RunPlan mappingConfigCode(BiEtlMappingConfig config) {
        this.config = config;
        this.refCode = config.getCode();
        return this;
    }

    public RunPlan synCount(List<ConditionDto> conditionDtos) throws Exception {
        String condition = null;

        List<String> list = Lists.newArrayList();
        list.add(" 1=1 ");
        if (config.getType().equals(SyncTypeEnum.INCREMENT.getValue())) {
            if (StringUtils.isNotBlank(config.getOffsetValue())) {
                list.add("'" + config.getOffsetField() + "' > =" + "'" + config.getOffsetValue() + "'");
            }
        }
        if (CollectionUtils.isNotEmpty(conditionDtos)) {
            for (ConditionDto conditionDto : conditionDtos) {
                WildcardEnum wildcardEnum = WildcardEnum.get(conditionDto.getSymbol());
                String value = wildcardEnum.expression(conditionDto.getValues());
                String symbol = wildcardEnum.getCode();
                String express = RelaBaseBuildUtil.condition(conditionDto.getField(), symbol, value);
                list.add(express);
            }
        }
        if (list.size() > 1) {
            condition = AnalyseUtil.join(" AND ", list.toArray(new String[0]));
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

//    public RunPlan etlCount(String tableName) throws Exception {
//        DbHandler handler = SpringUtil.getBean("dbHandler", DbHandler.class);
//        long nowCount = handler.getCount(tableName, null);
//        this.count = String.valueOf(nowCount);
//        this.tableName = tableName;
//        return this;
//    }


}
