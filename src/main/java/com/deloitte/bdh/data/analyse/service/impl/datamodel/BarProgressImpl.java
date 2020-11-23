package com.deloitte.bdh.data.analyse.service.impl.datamodel;

import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.data.analyse.constants.CustomParamsConstants;
import com.deloitte.bdh.data.analyse.enums.DataModelTypeEnum;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModel;
import com.deloitte.bdh.data.analyse.model.datamodel.DataModelField;
import com.deloitte.bdh.data.analyse.model.datamodel.request.BaseComponentDataRequest;
import com.deloitte.bdh.data.analyse.model.datamodel.response.BaseComponentDataResponse;
import com.deloitte.bdh.data.analyse.service.AnalyseDataService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 基础格式返回实现类
 */
@Service("barProgressImpl")
public class BarProgressImpl extends AbstractDataService implements AnalyseDataService {

    @Override
    public BaseComponentDataResponse handle(BaseComponentDataRequest request) {

        BaseComponentDataResponse response = execute(buildSql(request.getDataConfig().getDataModel()));
        buildRows(request, response);
        return response;
    }

    private void buildRows(BaseComponentDataRequest request, BaseComponentDataResponse response) {
        DataModel dataModel = request.getDataConfig().getDataModel();
        if (MapUtils.isNotEmpty(dataModel.getCustomParams())) {
            Map<String, Object> data = response.getRows().get(0);
            String colName = dataModel.getX().get(0).getId();
            if (StringUtils.isNotBlank(dataModel.getX().get(0).getAlias())) {
                colName = dataModel.getX().get(0).getAlias();
            }
            BigDecimal value = NumberUtils.createBigDecimal(String.valueOf(data.get(colName)));
            String progressTotal = MapUtils.getString(dataModel.getCustomParams(), CustomParamsConstants.PROGRESS_TOTAL);
            if (StringUtils.isNotBlank(progressTotal) && NumberUtils.isDigits(progressTotal)) {
                BigDecimal total = NumberUtils.createBigDecimal(progressTotal);
                data.put("total", total);
                data.put("percent", value.divide(total, 2, BigDecimal.ROUND_HALF_UP));
            }
        }

    }


    @Override
    protected void validate(DataModel dataModel) {
        if (CollectionUtils.isEmpty(dataModel.getX())) {
            throw new BizException("度量不能为空");
        }
        if (dataModel.getX().size() > 1) {
            throw new BizException("最多可放入一个度量");
        }
        if (!StringUtils.equals(DataModelTypeEnum.DL.getCode(), dataModel.getX().get(0).getQuota())) {
            throw new BizException("只可放入度量");
        }
    }
}
