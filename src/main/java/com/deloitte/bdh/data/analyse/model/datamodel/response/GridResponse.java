package com.deloitte.bdh.data.analyse.model.datamodel.response;

import lombok.Data;

/**
 * {
 * 	"status": 0,
 * 	"msg": "",
 * 	"data": {
 * 		"columns": [{
 * 			"id": "日期",
 * 			"name": "日期"
 *                }, {
 * 			"id": "车型",
 * 			"name": "车型"
 *        }, {
 * 			"id": "配置",
 * 			"name": "配置"
 *        }, {
 * 			"id": "是否购买车险",
 * 			"name": "是否购买车险"
 *        }, {
 * 			"id": "订单ID",
 * 			"name": "订单ID"
 *        }],
 * 		"rows": [ {
 * 			"日期": "2020-05-31",
 * 			"车型": "车型9",
 * 			"配置": "高配",
 * 			"是否购买车险": "是",
 * 			"订单ID": "SR00202005154792"
 *        }]* 	}
 * }
 */
@Data
public class GridResponse {
}
