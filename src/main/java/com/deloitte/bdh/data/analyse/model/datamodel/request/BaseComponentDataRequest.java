package com.deloitte.bdh.data.analyse.model.datamodel.request;

import com.deloitte.bdh.data.analyse.model.datamodel.BaseComponentDataRequestConfig;
import com.deloitte.bdh.data.analyse.model.datamodel.DataConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * {
 * "type": "table",
 * "dataModelHash": "mld_1013e-6ufxam0n-zn3drv6",
 * "dataConfig": {
 * "continuity_type": "simple",
 * "continuity_time_step": "1Hour",
 * "continuity_empty_val": "0",
 * "rateStdDate": "auto",
 * "needAutoRefresh": false,
 * "tableType": "normal",
 * "dataModel": {
 * "x": [{
 * "frontendId": "FKGSYYQVQ2MEQA",
 * "id": "SGB25297DD0DACEB96",
 * "type": "d",
 * "dataType": "date"
 * }, {
 * "frontendId": "FKGSYYT555JUSP",
 * "id": "SGA3080C6E0230AD5C",
 * "type": "d",
 * "dataType": "string"
 * }, {
 * "frontendId": "FKGT3S7NQ13JK5",
 * "id": "SGD2D67D0CCA672164",
 * "type": "d",
 * "dataType": "string"
 * }, {
 * "frontendId": "FKH0BYEPJ2TBGG",
 * "id": "SG2315EE1E48CFD012",
 * "type": "d",
 * "dataType": "string"
 * }],
 * "limit": 10000,
 * "querys": [],
 * "chartCondition": [{
 * "frontendId": "FKH0ASR9H45T5Y",
 * "id": "SGD2D67D0CCA672164",
 * "type": "d",
 * "dataType": "string",
 * "alias": "配置"
 * }]* 		},
 * "tableNotAggregate": false
 * },
 * "resourceHash": "c_1013e-6txrbz0i-ovyv54",
 * "pageHash": "r_1013e-708f9z6x-kg6dzg",
 * "conditions": [],
 * "chartConditions": []
 * }
 */
@Data
public class BaseComponentDataRequest extends BaseComponentDataRequestConfig {
    @ApiModelProperty(value = "图表类型")
    String type;

    String dataModelHash;
    @ApiModelProperty(value = "图标数据相关配置")
    DataConfig dataConfig;
    String resourceHash;
    String pageHash;

    Boolean isDebug;
}
