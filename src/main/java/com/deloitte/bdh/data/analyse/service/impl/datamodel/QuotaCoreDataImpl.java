package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.beust.jcommander.internal.Lists;
import com.deloitte.bdh.common.date.DateUtils;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.*;
import com.deloitte.bdh.data.analyse.model.datamodel.DataCondition;
import com.deloitte.bdh.data.analyse.service.impl.LocaleMessageService;
import com.deloitte.bdh.data.analyse.sql.DataSourceSelection;
import com.deloitte.bdh.data.analyse.sql.enums.MysqlFormatTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.ComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("quotaCoreDataImpl")
public class QuotaCoreDataImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(ComponentDataRequest request) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        String sql = buildSql(dataModel);
        return execute(dataModel, sql, list -> {
            if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
                String viewDetail = MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.VIEW_DETAIL);
                if (StringUtils.equals(viewDetail, "true")) {
                    return list;
                }
            }
            //未开启直接返回
            if (!isOpen(dataModel)) {
                return decoration(dataModel, setDefalut(dataModel, list));
            }

            List<Map<String, Object>> sourceSqlList = setDefalut(dataModel, sourceSelection.expandExecute(dataModel, new DataSourceSelection.Type() {
                @Override
                public String local(DataModel model, String tableName) {
                    return sourceMysql(sql, dataModel);
                }

                @Override
                public String mysql(DataModel model, String tableName) {
                    return sourceMysql(sql, dataModel);
                }

                @Override
                public String oracle(DataModel model, String tableName) {
                    return sourceOracle(sql, dataModel);
                }

                @Override
                public String sqlServer(DataModel model, String tableName) {
                    return sourceSqlserver(sql, dataModel);
                }

                @Override
                public String hana(DataModel model, String tableName) {
                    return sourceHana(sql, dataModel);
                }
            }));

            List<Map<String, Object>> chainSqlList = Lists.newArrayList();
            if (isOpenChain(dataModel)) {
                String newSql = rebuild(dataModel);
                chainSqlList = setDefalut(dataModel, sourceSelection.expandExecute(dataModel, new DataSourceSelection.Type() {
                    @Override
                    public String local(DataModel model, String tableName) {
                        return chainMysql(newSql, dataModel);
                    }

                    @Override
                    public String mysql(DataModel model, String tableName) {
                        return chainMysql(newSql, dataModel);
                    }

                    @Override
                    public String oracle(DataModel model, String tableName) {
                        return chainOracle(newSql, dataModel);
                    }

                    @Override
                    public String sqlServer(DataModel model, String tableName) {
                        return chainSqlserver(newSql, dataModel);
                    }

                    @Override
                    public String hana(DataModel model, String tableName) {
                        return chainHana(newSql, dataModel);
                    }
                }));
            }
            List<Map<String, Object>> yoySqlList = Lists.newArrayList();
            if (isOpenYoy(dataModel)) {
                String newSql = rebuild(dataModel);
                yoySqlList = setDefalut(dataModel, sourceSelection.expandExecute(dataModel, new DataSourceSelection.Type() {
                    //移除param 里面的 条件
                    @Override
                    public String local(DataModel model, String tableName) {
                        return yoyMysql(newSql, dataModel);
                    }

                    @Override
                    public String mysql(DataModel model, String tableName) {
                        return yoyMysql(newSql, dataModel);
                    }

                    @Override
                    public String oracle(DataModel model, String tableName) {
                        return yoyOracle(newSql, dataModel);
                    }

                    @Override
                    public String sqlServer(DataModel model, String tableName) {
                        return yoySqlserver(newSql, dataModel);
                    }

                    @Override
                    public String hana(DataModel model, String tableName) {
                        return yoyHana(newSql, dataModel);
                    }
                }));
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
                        BigDecimal chainGrowthRate = current.compareTo(BigDecimal.ZERO) == 0 ? current : new BigDecimal("100");
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
                        BigDecimal yoyGrowthRate = current.compareTo(BigDecimal.ZERO) == 0 ? current : new BigDecimal("100");
                        if (BigDecimal.ZERO.compareTo(previous) != 0) {
                            yoyGrowthRate = (current.subtract(previous)).divide(previous.abs(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                        }
                        sourceMap.put("yoy", yoyGrowthRate);
                    }
                }
            }
            return decoration(dataModel, sourceSqlList);
        });
    }

    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException(ResourceMessageEnum.X_NOT_NULL.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.X_NOT_NULL.getMessage(), ThreadLocalHolder.getLang()));
        }

        //对度量和维度数量有校验
        List<DataModelField> dlFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.DL.getCode()))
                .collect(Collectors.toList());
        List<DataModelField> wdFields = dataModel.getX().stream().filter(s -> s.getQuota().equals(DataModelTypeEnum.WD.getCode()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(wdFields)) {
            throw new BizException(ResourceMessageEnum.DL_ONLY.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DL_ONLY.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (CollectionUtils.isEmpty(dlFields)) {
            throw new BizException(ResourceMessageEnum.DL_NOT_NULL.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.DL_NOT_NULL.getMessage(), ThreadLocalHolder.getLang()));
        }

        //校验同比环比
        if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
            if (isOpen(dataModel) && StringUtils.isAnyBlank(getCoreDateKey(dataModel), getCoreDateValue(dataModel), getCoreDateType(dataModel))) {
                throw new BizException(ResourceMessageEnum.QUOTA_DATE_NO_CHOSE.getCode(),
                        localeMessageService.getMessage(ResourceMessageEnum.QUOTA_DATE_NO_CHOSE.getMessage(), ThreadLocalHolder.getLang()));
            }
        }
        dataModel.setPage(null);
    }

    @Override
    public void before(DataModel dataModel) {
        super.before(dataModel);
        if (CollectionUtils.isNotEmpty(dataModel.getX())) {
            for (DataModelField s : dataModel.getX()) {
                s.setDefaultValue("0");
            }
        }
        if (isOpen(dataModel)) {
            //判断是否内部删选
            String coreDateKey = (String) dataModel.getCustomParams().get(CustomParamsConstants.CORE_DATE_KEY);
            String coreDateType = (String) dataModel.getCustomParams().get(CustomParamsConstants.CORE_DATE_TYPE);
            String coreDateValue = (String) dataModel.getCustomParams().get(CustomParamsConstants.CORE_DATE_VALUE);

            if (CollectionUtils.isNotEmpty(dataModel.getConditions())) {
                DataCondition ltrCondition = null;
                DataCondition eqCondition = null;
                String minDate = null;

                for (DataCondition condition : dataModel.getConditions()) {
                    if (condition.getId().get(0).equals(coreDateKey) && condition.getFormatType().equals(coreDateType)) {
                        //右值判断
                        if (condition.getSymbol().equals(WildcardEnum.LTE.getKey())) {
                            if (null != ltrCondition) {
                                //有多个ltr时，选择较小位
                                if (omparisoncOfDate(parseDate(ltrCondition.getFormatType(), ltrCondition.getValue().get(0)), parseDate(condition.getFormatType(), condition.getValue().get(0)))) {
                                    ltrCondition = condition;
                                }
                            } else {
                                ltrCondition = condition;
                            }
                        }

                        //eq
                        if (condition.getSymbol().equals(WildcardEnum.EQ.getKey())) {
                            eqCondition = condition;
                        }

                        //in
                        if (condition.getSymbol().equals(WildcardEnum.IN.getKey())) {
                            minDate = getMinDateStr(condition.getValue(), condition.getFormatType());
                        }

                    }
                }

                if (null != ltrCondition && omparisoncOfDate(coreDateValue,
                        parseDate(ltrCondition.getFormatType(), ltrCondition.getValue().get(0)))) {
                    coreDateValue = parseDate(ltrCondition.getFormatType(), ltrCondition.getValue().get(0));
                }
                if (null != minDate && omparisoncOfDate(coreDateValue, minDate)) {
                    coreDateValue = minDate;
                }
                if (null != eqCondition) {
                    coreDateValue = parseDate(eqCondition.getFormatType(), eqCondition.getValue().get(0));
                }
                if (null != coreDateValue) {
                    dataModel.getCustomParams().put(CustomParamsConstants.CORE_DATE_VALUE, coreDateValue);
                }
            }
        }

    }

    private String sourceMysql(String sql, DataModel dataModel) {
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATE_FORMAT('#1','%Y')=DATE_FORMAT(#2,'%Y') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATE_FORMAT('#1','%Y-%m')=DATE_FORMAT(#2,'%Y-%m') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATE_FORMAT('#1','%Y-%m-%d')=DATE_FORMAT(#2,'%Y-%m-%d') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            str = " QUARTER('#1')=QUARTER(#2) AND DATE_FORMAT('#1','%Y')=DATE_FORMAT(#2,'%Y')";
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String chainMysql(String sql, DataModel dataModel) {
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " YEAR(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 year))=DATE_FORMAT(#2,'%Y') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " LEFT(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 month),7)=DATE_FORMAT(#2,'%Y-%m') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 day)=DATE_FORMAT(#2,'%Y-%m-%d') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            if (Integer.parseInt(getCoreDateValue(dataModel).split("-")[1]) > 4) {
                str = " QUARTER(DATE_SUB('#1',interval 1 QUARTER))=QUARTER(#2) AND DATE_FORMAT('#1','%Y')=DATE_FORMAT(#2,'%Y') ";
            } else {
                str = " QUARTER(#2) = 4 AND YEAR(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 year))=DATE_FORMAT(#2,'%Y') ";
            }
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String yoyMysql(String sql, DataModel dataModel) {
        //同比增长率=（本期数-同期数）/|同期数|×100%。本年度与上年度
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " YEAR(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 year))=DATE_FORMAT(#2,'%Y') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " LEFT(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-12 month),7)=DATE_FORMAT(#2,'%Y-%m') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATE_ADD('#1',interval -1 year)=DATE_FORMAT(#2,'%Y-%m-%d') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            str = " QUARTER('#1')=QUARTER(#2) AND YEAR(DATE_ADD(STR_TO_DATE('#1', '%Y-%m-%d'),interval-1 year)) = DATE_FORMAT(#2,'%Y') ";
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String sourceOracle(String sql, DataModel dataModel) {
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char( to_date('#1','yyyy-mm-dd hh24:mi:ss'),'yyyy')=to_char(#2,'yyyy') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char( to_date('#1','yyyy-mm-dd hh24:mi:ss'),'yyyy-mm')=to_char(#2,'yyyy-mm') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char( to_date('#1','yyyy-mm-dd hh24:mi:ss'),'yyyy-mm-dd')=to_char(#2,'yyyy-mm-dd') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            str = "CONCAT(to_char( to_date('#1','yyyy-mm-dd hh24:mi:ss') ,'yyyy') ,to_char( to_date('#1','yyyy-mm-dd hh24:mi:ss') ,'Q'))=CONCAT(to_char(#2,'yyyy') ,to_char(#2 ,'Q')) ";
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String chainOracle(String sql, DataModel dataModel) {
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char(to_date('#1','yyyy-mm-dd hh24:mi:ss') + numtoyminterval(-1,'year'),'yyyy')=to_char(#2,'yyyy') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char(to_date('#1','yyyy-mm-dd hh24:mi:ss') + numtoyminterval(-1,'month'),'yyyyMM')=to_char(#2,'yyyyMM') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char(to_date('#1','yyyy-mm-dd hh24:mi:ss') -1,'yyyy-mm-dd')=to_char(#2,'yyyy-mm-dd') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            if (Integer.parseInt(getCoreDateValue(dataModel).split("-")[1]) > 4) {
                str = " to_char(to_date('#1','yyyy-mm-dd hh24:mi:ss') + numtoyminterval(-4,'month'),'Q')=to_char(#2 ,'Q') AND to_char( to_date('#1','yyyy-mm-dd hh24:mi:ss'),'yyyy')=to_char(#2,'yyyy') ";
            } else {
                str = " to_char(#2 ,'Q') = 4 AND to_char( to_date('#1','yyyy-mm-dd hh24:mi:ss')+ numtoyminterval(-1,'year'),'yyyy')=to_char(#2,'yyyy') ";
            }
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String yoyOracle(String sql, DataModel dataModel) {
        //同比增长率=（本期数-同期数）/|同期数|×100%。本年度与上年度
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char(to_date('#1','yyyy-mm-dd hh24:mi:ss') + numtoyminterval(-1,'year'),'yyyy')=to_char(#2,'yyyy') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char(to_date('#1','yyyy-mm-dd hh24:mi:ss') + numtoyminterval(-1,'year'),'yyyyMM')=to_char(#2,'yyyyMM') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char(to_date('#1','yyyy-mm-dd hh24:mi:ss') + numtoyminterval(-1,'year'),'yyyy-mm-dd')=to_char(#2,'yyyy-mm-dd') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            str = "  to_char(to_date('#1','yyyy-mm-dd hh24:mi:ss'),'Q')=to_char(#2 ,'Q')  AND to_char( to_date('#1','yyyy-mm-dd hh24:mi:ss')+ numtoyminterval(-1,'year'),'yyyy')=to_char(#2,'yyyy') ";
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String sourceSqlserver(String sql, DataModel dataModel) {
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " YEAR('#1')=YEAR(#2) ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " format(convert(datetime,'#1', 20),'yyyy-MM') =format(#2,'yyyy-MM') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " format(convert(datetime,'#1', 20),'yyyy-MM-dd') =format(#2,'yyyy-MM-dd') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            str = " concat(format(convert(datetime,'#1', 20),'yyyy'),DATEPART(Q , convert(datetime,'#1', 20)))=concat(format(#2,'yyyy'),DATEPART(Q , #2)) ";
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String chainSqlserver(String sql, DataModel dataModel) {
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " format(convert(datetime,'#1', 20),'yyyy')-1=YEAR(#2) ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " format(DATEADD(MM,-1,'#1'),'yyyy-MM') =format(#2,'yyyy-MM') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " format(DATEADD(dd,-1,'#1'),'yyyy-MM-dd') =format(#2,'yyyy-MM-dd') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            if (Integer.parseInt(getCoreDateValue(dataModel).split("-")[1]) > 4) {
                str = " DATEPART(Q,DATEADD(Q,-1,convert(datetime,'#1', 20)))=DATEPART(Q , #2) AND YEAR('#1')=YEAR(#2)  ";
            } else {
                str = " DATEPART(Q , #2) = 4 AND format(convert(datetime,'#1', 20),'yyyy')-1=YEAR(#2) ";
            }
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String yoySqlserver(String sql, DataModel dataModel) {
        //同比增长率=（本期数-同期数）/|同期数|×100%。本年度与上年度
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " YEAR('#1')-1=YEAR(#2) ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " format(DATEADD(yy,-1,'#1'),'yyyy-MM') =format(#2,'yyyy-MM') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " format(DATEADD(yy,-1,'#1'),'yyyy-MM-dd') =format(#2,'yyyy-MM-dd') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            str = " DATEPART(Q , '#1')=DATEPART(Q , #2) AND format(convert(datetime,'#1', 20),'yyyy')-1=YEAR(#2) ";
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String sourceHana(String sql, DataModel dataModel) {
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " YEAR('#1')=YEAR(#2) ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char('#1','yyyy-MM') =to_char(#2,'yyyy-MM') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " to_char('#1','yyyy-MM-dd') =to_char(#2,'yyyy-MM-dd') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            str = " QUARTER('#1') =QUARTER(#2) ";
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String chainHana(String sql, DataModel dataModel) {
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " YEAR('#1')-1=YEAR(#2) ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " TO_CHAR(ADD_MONTHS('#1',-1),'yyyy-MM') = TO_CHAR(#2,'yyyy-MM') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " TO_CHAR(ADD_DAYS('#1',-1),'yyyy-MM-dd')=TO_CHAR(#2,'yyyy-MM-dd') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            if (Integer.parseInt(getCoreDateValue(dataModel).split("-")[1]) > 4) {
                str = " RIGHT(QUARTER('#1'),1)-1 = RIGHT(QUARTER(#2),1) AND YEAR('#1') = YEAR(#2) ";
            } else {
                str = " RIGHT(QUARTER(#2),1) = 4  AND YEAR('#1')-1 = YEAR(#2) ";
            }
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
    }

    private String yoyHana(String sql, DataModel dataModel) {
        //同比增长率=（本期数-同期数）/|同期数|×100%。本年度与上年度
        String str = null;
        if (MysqlFormatTypeEnum.YEAR.getKey().equals(getCoreDateType(dataModel))) {
            str = " YEAR('#1')-1=YEAR(#2) ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH.getKey().equals(getCoreDateType(dataModel))) {
            str = " TO_CHAR(ADD_YEARS('#1',-1),'yyyy-MM') = TO_CHAR(#2,'yyyy-MM') ";
        }
        if (MysqlFormatTypeEnum.YEAR_MONTH_DAY.getKey().equals(getCoreDateType(dataModel))) {
            str = " TO_CHAR(ADD_YEARS('#1',-1),'yyyy-MM-dd') = TO_CHAR(#2,'yyyy-MM-dd') ";
        }
        if (MysqlFormatTypeEnum.YEAR_QUARTERLY.getKey().equals(getCoreDateType(dataModel))) {
            str = " RIGHT(QUARTER('#1'),1) = RIGHT(QUARTER(#2),1) AND YEAR('#1') -1 = YEAR(#2) ";
        }
        String appendField = str.replace("#1", getCoreDateValue(dataModel)).replace("#2", getCoreDateKey(dataModel));
        return append(sql, appendField, 2);
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

    private List<Map<String, Object>> decoration(DataModel dataModel, List<Map<String, Object>> args) {
        List<Map<String, Object>> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(args)) {
            Map<String, String> precisionMap = Maps.newHashMap();
            Map<String, String> dataUnitMap = Maps.newHashMap();
            for (DataModelField x : dataModel.getX()) {
                String colName = x.getId();
                if (StringUtils.isNotBlank(x.getAlias())) {
                    colName = x.getAlias();
                }
                if (null != x.getPrecision()) {
                    precisionMap.put(colName, x.getPrecision().toString());
                }
                if (StringUtils.isNotBlank(x.getDataUnit())) {
                    dataUnitMap.put(colName, x.getDataUnit());
                }
            }
            for (Map<String, Object> args0 : args) {
                Map<String, Object> newMap = Maps.newHashMap();
                for (Map.Entry<String, Object> var : args0.entrySet()) {
                    if ("yoy".equals(var.getKey()) || "chain".equals(var.getKey())) {
                        newMap.put(var.getKey(), var.getValue());
                    } else {
                        newMap.put("name", var.getKey());
                        newMap.put("value", var.getValue());
                    }
                    //设置精度和数据单位
                    if (null != MapUtils.getObject(precisionMap, var.getKey())) {
                        newMap.put("precision", MapUtils.getObject(precisionMap, var.getKey()));
                    }
                    if (null != MapUtils.getObject(dataUnitMap, var.getKey())) {
                        newMap.put("dataUnit", DataUnitEnum.getDesc(MapUtils.getObject(dataUnitMap, var.getKey())));
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

    private String append(String sql, String insertField, int type) {
        StringBuilder sb = new StringBuilder(sql);
        switch (type) {
            case 1:
                //select
                sb.insert(sb.indexOf("FROM"), "," + insertField + " ");
                break;
            case 2:
                //where
                sb.insert(sb.indexOf("1=1") + 3, " AND " + insertField + " ");
                break;
            default:
                return sql;
        }
        return sb.toString();
    }

    private String parseDate(String forMatType, String value) {
        FormatTypeEnum typeEnum = FormatTypeEnum.get(forMatType);
        switch (typeEnum) {
            case YEAR:
                value += "-01-01";
                break;
            case YEAR_MONTH:
                value += "-01";
                break;
            case YEAR_QUARTERLY:
                String[] values = value.split("-");
                switch (values[1]) {
                    case "1":
                        value = values[0] + "-01-01";
                        break;
                    case "2":
                        value = values[0] + "-04-01";
                        break;
                    case "3":
                        value = values[0] + "-07-01";

                        break;
                    case "4":
                        value = values[0] + "-10-01";
                        break;
                }
                break;
            case YEAR_MONTH_DAY:
                break;
            default:
                value = DateUtils.formatStandardDate(new Date());
        }
        return value;
    }

    /**
     * 比较时间大小
     */
    public boolean omparisoncOfDate(String before, String after) {
        try {
            return DateUtils.parseStandardDate(before).getTime() > DateUtils.parseStandardDate(after).getTime();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回最小的时间
     */
    public String getMinDateStr(List<String> list, String forMatType) {
        try {
            Long temp = null;
            String minDate = null;
            for (String str : list) {
                long date = DateUtils.parseStandardDate(parseDate(forMatType, str)).getTime();
                if (null == temp || date < temp) {
                    temp = date;
                    minDate = parseDate(forMatType, str);
                }
            }
            return minDate;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String rebuild(DataModel dataModel) {
        String coreDateKey = MapUtils.getString(dataModel.getCustomParams(), "coreDateKey");
        if (null != coreDateKey) {
            if (CollectionUtils.isNotEmpty(dataModel.getConditions())) {
                Iterator iterator = dataModel.getConditions().iterator();
                while (iterator.hasNext()) {
                    DataCondition data = (DataCondition) iterator.next();
                    if (coreDateKey.equals(data.getId().get(0))) {
                        iterator.remove();
                    }
                }
            }
        }
        return buildSql(dataModel);
    }

}
