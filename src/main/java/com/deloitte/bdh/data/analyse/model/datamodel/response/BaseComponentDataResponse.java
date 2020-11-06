package com.deloitte.bdh.data.analyse.model.datamodel.response;

import lombok.Data;

import java.util.Map;

/**
 * {
 * 	"status": 0,
 * 	"msg": "",
 * 	"data": {
 * 		"status": 0,
 * 		"postData": {
 * 			"conditions": [],
 * 			"chartConditions": []
 *                },
 * 		"sql": "SELECT\n  `car_sale`.`日期` AS FKGSYYQVQ2MEQA,\n  `car_sale`.`车型` AS FKGSYYT555JUSP,\n  `car_sale`.`配置` AS FKGT3S7NQ13JK5,\n  `car_sale`.`是否购买车险` AS FKH0BYEPJ2TBGG,\n  `car_sale`.`订单ID` AS FKH0DI9KI2M1PP\nFROM\n  `car_sale`\nGROUP BY\n  `car_sale`.`日期`,\n  `car_sale`.`车型`,\n  `car_sale`.`配置`,\n  `car_sale`.`是否购买车险`,\n  `car_sale`.`订单ID`\nORDER BY\n  `car_sale`.`日期` asc\nLIMIT 0, 10000",
 * 		"databaseType": "MySQL 5.X",
 * 		"generateSqlTime": 14,
 * 		"dataset": [{
 * 			"FKGSYYQVQ2MEQA": "2020-05-30T16:00:00.000Z",
 * 			"FKGSYYT555JUSP": "车型9",
 * 			"FKGT3S7NQ13JK5": "高配",
 * 			"FKH0BYEPJ2TBGG": "是",
 * 			"FKH0DI9KI2M1PP": "SR00202005154792"
 *        }],
 * 		"runSqlTime": 29,
 * 		"datasetColumnHeader": [{
 * 			"id": "FKGSYYQVQ2MEQA",
 * 			"name": "日期(FKGSYYQVQ2MEQA)"
 *        }],
 * 		"renameHash": [],
 * 		"data": {
 * 			"columns": [{
 * 				"id": "日期",
 * 				"name": "日期"
 *            }, {
 * 				"id": "车型",
 * 				"name": "车型"
 *            }, {
 * 				"id": "配置",
 * 				"name": "配置"
 *            }, {
 * 				"id": "是否购买车险",
 * 				"name": "是否购买车险"
 *            }, {
 * 				"id": "订单ID",
 * 				"name": "订单ID"
 *            }],
 * 			"rows": [{
 * 				"日期": "2020-05-01",
 * 				"车型": "车型1",
 * 				"配置": "中配",
 * 				"是否购买车险": "否",
 * 				"订单ID": "SR00202005152379"
 *            } {
 * 				"日期": "2020-05-31",
 * 				"车型": "车型9",
 * 				"配置": "高配",
 * 				"是否购买车险": "是",
 * 				"订单ID": "SR00202005154792"
 *            }]
 *        },
 * 		"formatDataTime": 56,
 * 		"databaseHash": "d_1013e-akrxglq5-kej8q1"*
 * 	}
 * }
 */
@Data
public class BaseComponentDataResponse {
    String sql;
    Map data;
}
