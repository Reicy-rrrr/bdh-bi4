package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.enums.FormatTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.deloitte.bdh.data.analyse.utils.AnalyseUtil;
import com.deloitte.bdh.data.analyse.utils.BuildSqlUtil;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("quotaCoreDataImpl")
public class QuotaCoreDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) throws Exception {
        DataModel dataModel = request.getDataConfig().getDataModel();
        String sql = buildSql(dataModel);
        return execute(sql, list -> {
            //未开启直接返回
            if (!isOpen(dataModel)) {
                return decoration(setDefalut(dataModel, list));
            }

            String sourceSql = doSourceSql(sql, dataModel);
            List<Map<String, Object>> sourceSqlList = setDefalut(dataModel, super.biUiDemoMapper.selectDemoList(sourceSql));

            List<Map<String, Object>> chainSqlList = Lists.newArrayList();
            if (isOpenChain(dataModel)) {
                String chainSql = chainSql(sql, dataModel);
                chainSqlList = setDefalut(dataModel, super.biUiDemoMapper.selectDemoList(chainSql));
            }
            List<Map<String, Object>> yoySqlList = Lists.newArrayList();
            if (isOpenYoy(dataModel)) {
                String yoySql = yoySql(sql, dataModel);
                yoySqlList = setDefalut(dataModel, super.biUiDemoMapper.selectDemoList(yoySql));
            }

            for (String field : getFields(dataModel)) {
                for (Map<String, Object> sourceMap : sourceSqlList) {
                    if (!sourceMap.containsKey(field)) {
                        continue;
                    }
                    //当期
                    BigDecimal current = (null == sourceMap.get(field) ? BigDecimal.ZERO
                            : new BigDecimal(String.valueOf(sourceMap.get(field))));

                    //环比
                    if (isOpenChain(dataModel)) {
                        BigDecimal chain = BigDecimal.ZERO;
                        if (CollectionUtils.isNotEmpty(chainSqlList)) {
                            for (Map<String, Object> map : chainSqlList) {
                                if (map.containsKey(field)) {
                                    chain = (null == map.get(field) ? BigDecimal.ZERO : new BigDecimal(String.valueOf(map.get(field))));
                                }
                            }
                        }
                        //环比增长率=（本期数-上期数）/上期数×100%。
                        BigDecimal chainGrowthRate = new BigDecimal("100");
                        if (BigDecimal.ZERO.compareTo(chain) != 0) {
                            chainGrowthRate = (current.subtract(chain)).divide(chain, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                        }
                        sourceMap.put("chain", chainGrowthRate);
                    }
                    //同比
                    if (isOpenYoy(dataModel)) {
                        BigDecimal previous = BigDecimal.ZERO;
                        if (CollectionUtils.isNotEmpty(yoySqlList)) {
                            for (Map<String, Object> map : yoySqlList) {
                                if (map.containsKey(field)) {
                                    previous = (null == map.get(field) ? BigDecimal.ZERO : new BigDecimal(String.valueOf(map.get(field))));
                                }
                            }
                        }
                        //同比增长率=（本期数-同期数）/|同期数|×100%。本年度与上年度
                        BigDecimal yoyGrowthRate = new BigDecimal("100");
                        if (BigDecimal.ZERO.compareTo(previous) != 0) {
                            yoyGrowthRate = (current.subtract(previous)).divide(previous.abs(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                        }
                        sourceMap.put("yoy", yoyGrowthRate);
                    }
                }
            }
            return decoration(sourceSqlList);
        });
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new RuntimeException("字段列表不能为空");
        }

        //对度量和维度数量有校验
        List<DataModelField> dlFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.DL.getCode()))
                .collect(Collectors.toList());
        List<DataModelField> wdFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.WD.getCode()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(wdFields)) {
            throw new RuntimeException("核心指标图只能设置度量");
        }
        if (CollectionUtils.isEmpty(dlFields)) {
            throw new RuntimeException("核心指标图设置度量不能为空");
        }

        //校验同比环比
        if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
            if (isOpen(dataModel) && StringUtils.isAnyBlank(getCoreDateKey(dataModel), getCoreDateValue(dataModel), getCoreDateType(dataModel))) {
                throw new RuntimeException("核心指标图开启同比或环比后，请选择日期");
            }
        }
        dataModel.setPage(null);
    }

    private String doSourceSql(String sql, DataModel dataModel) {
        String str = null;
        if (FormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATE_FORMAT('#1','%Y')=DATE_FORMAT(#2,'%Y') ";
        }
        if (FormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATE_FORMAT('#1','%Y-%m')=DATE_FORMAT(#2,'%Y-%m') ";
        }
        if (FormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATE_FORMAT('#1','%Y-%m-%d')=DATE_FORMAT(#2,'%Y-%m-%d') ";
        }
        if (FormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            str = " QUARTER('#1')=QUARTER(#2) AND DATE_FORMAT('#1','%Y')=DATE_FORMAT(#2,'%Y')";
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return BuildSqlUtil.append(sql, appendField, 2);
    }

    private String chainSql(String sql, DataModel dataModel) {
        String str = null;
        if (FormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " YEAR(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 year))=DATE_FORMAT(#2,'%Y') ";
        }
        if (FormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " LEFT(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 month),7)=DATE_FORMAT(#2,'%Y-%m') ";
        }
        if (FormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 day)=DATE_FORMAT(#2,'%Y-%m-%d') ";
        }
        if (FormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            if (Integer.parseInt(getCoreDateValue(dataModel).split("-")[1]) > 4) {
                str = " QUARTER(DATE_SUB('#1',interval 1 QUARTER))=QUARTER(#2) AND DATE_FORMAT('#1','%Y')=DATE_FORMAT(#2,'%Y') ";
            } else {
                str = " QUARTER(DATE_SUB('#1',interval 1 QUARTER))=QUARTER(#2) AND YEAR(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 year))=DATE_FORMAT(#2,'%Y') ";
            }
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return BuildSqlUtil.append(sql, appendField, 2);
    }

    private String yoySql(String sql, DataModel dataModel) {
        //同比增长率=（本期数-同期数）/|同期数|×100%。本年度与上年度
        String str = null;
        if (FormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " YEAR(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 year))=DATE_FORMAT(#2,'%Y') ";
        }
        if (FormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " LEFT(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-12 month),7)=DATE_FORMAT(#2,'%Y-%m') ";
        }
        if (FormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATE_ADD('#1',interval -1 year)=DATE_FORMAT(#2,'%Y-%m-%d') ";
        }
        if (FormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            str = " QUARTER('#1')=QUARTER(#2) AND YEAR(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 year)) = DATE_FORMAT(#2,'%Y') ";
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return BuildSqlUtil.append(sql, appendField, 2);
    }

    private boolean isOpen(DataModel dataModel) {
        boolean isOpen = false;
        if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
            Boolean chain = isOpenChain(dataModel);
            Boolean yoy = isOpenYoy(dataModel);
            if (chain || yoy) {
                isOpen = true;
            }
        }
        return isOpen;
    }

    private boolean isOpenChain(DataModel dataModel) {
        Boolean bo = MapUtils.getBoolean(dataModel.getCustomParams(), CustomParamsConstants.CORE_CHAIN);
        return null != bo && bo;
    }

    private boolean isOpenYoy(DataModel dataModel) {
        Boolean bo = MapUtils.getBoolean(dataModel.getCustomParams(), CustomParamsConstants.CORE_YOY);
        return null != bo && bo;
    }

    private String getCoreDateKey(DataModel dataModel) {
        return MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.CORE_DATE_KEY);
    }

    private String getCoreDateValue(DataModel dataModel) {
        return MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.CORE_DATE_VALUE);
    }

    private String getCoreDateType(DataModel dataModel) {
        return MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.CORE_DATE_TYPE);
    }

    private List<Map<String, Object>> setDefalut(DataModel dataModel, List<Map<String, Object>> args) {
        List<Map<String, Object>> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(args) && MapUtils.isNotEmpty(args.get(0))) {
            for (Map.Entry<String, Object> entry : args.get(0).entrySet()) {
                Map<String, Object> var = Maps.newHashMap();
                var.put(entry.getKey(), entry.getValue());
                list.add(var);
            }
        } else {
            for (String var : getFields(dataModel)) {
                Map<String, Object> map = Maps.newHashMap();
                map.put(var, BigDecimal.ZERO);
                list.add(map);
            }
        }
        return list;
    }

    private List<Map<String, Object>> decoration(List<Map<String, Object>> args) {
        List<Map<String, Object>> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(args)) {
            for (Map<String, Object> args0 : args) {
                Map<String, Object> newMap = Maps.newHashMap();
                for (Map.Entry<String, Object> var : args0.entrySet()) {
                    if ("yoy".equals(var.getKey()) || "chain".equals(var.getKey())) {
                        newMap.put(var.getKey(), var.getValue());
                    } else {
                        newMap.put("name", var.getKey());
                        newMap.put("value", var.getValue());
                    }
                }
                list.add(newMap);
            }
        }
        return list;
    }

    private List<String> getFields(DataModel dataModel) {
        List<String> fields = Lists.newArrayList();
        for (DataModelField field : dataModel.getX()) {
            fields.add(StringUtils.isNotBlank(field.getAlias()) ? field.getAlias() : field.getId());
        }
        return fields;
    }

    @Override
    protected String buildSelect(DataModel dataModel) {
        List<String> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dataModel.getX())) {
            for (DataModelField s : dataModel.getX()) {
                String express = BuildSqlUtil.select(dataModel.getTableName(), s.getId(), s.getQuota(), s.getAggregateType(), s.getFormatType(), s.getAlias(), "0");
                if (org.apache.commons.lang.StringUtils.isNotBlank(express)) {
                    list.add(express);
                }
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return "SELECT " + AnalyseUtil.join(",", list.toArray(new String[0]));
    }

}
