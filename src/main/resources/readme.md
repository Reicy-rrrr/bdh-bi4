# BDH-BI系统设计_后台

------

## 概述

1. BDH-BI系统为一款自助可视化分析工具、用户通过配置自定义数据源和数据模型，可实现无需任何SQL以及编码对数据进行可视化分析。采用前后端分离模式，后端所采用技术为Spring Boot + Spring Cloud微服务模式，缓存使用Redis，持久层使用Mybatis + Mybatis Plus，数据库使用Mysql，消息使用mq，数据整理使用nifi。
2. 系统用户分为superuser、editor、viewer三种角色、superuser拥有所有权限，editor用户编辑权限，viewer拥有查看权限。在三种角色下用户分为内部租户用户和外部租户用户，内部租户用户拥有德勤方案的编辑权限。

------

## 功能设计

系统分为数据整理、分析管理、德勤方案三部分，三部分互相关联配合，最终整理输出可视化报表。

### 数据整理

- #### 数据连接

  数据连接分为2大类（文件型数据源与关系型/非关系型数据源），可以通过添加数据源配置数据连接信息或者上传文件来实现BI项目数据源的控制。

  注意点：

  创建非文件型数据源，会在 NIFI 上面创建 controllerService , 表 BI_ETL_DATABASE_INF 的 CONTROLLER_SERVICE_ID 字段维护着返回的ID。

  数据源创建后默认为启用状态，对应 NIFI 上面的 controllerService 状态，要删除前先禁用掉。

  

- #### 新建模型

  新建模型为BI项目对数据源进行ETL的功能。

  首先创建模型模板名称（该模型模板创建好后，**在数据模型列表 、NIFI 以及 调度平台** 上同时创建相关数据），其次进入模型整理画板后，添加数据，再基于业务需求进行ETL过程。

  相关的所有接口都在 EtlController 里面。

  注意点：

  添加数据源配置时，字段的数量后端是有长度限制的（因为BI以Mysql作为ODS层），目前的刷新方式分为全量、增量与直连（目前全量与增量都是基于增量字段来实现同步，建议增量字段用自增ID）。

  添加数据源保存后，非直连会配置在ODS层创建要同步的表以及在NIFI 创建 **SYNC_SQL** 类型的同步模型，并开启数据的同步（在执行记录可以看到同步信息），直连的则不需要同步到ODS层。

  模型整理画板中的各种组件就是对数据源同步后的ODS层的表的Sql 拼接，组装Sql 的接口在 **EtlController.handle** 中。

  一个模型只能有一个输出组件，且组件与组件直接必须要有关联，该校验在配置好组件后模型保存或者启动时都会进行校验。

  立即触发只会在NIFI上生成 **OUT_SQL **模型进行同步数据，若要同时进行**SYNC_SQL、OUT_SQL**则需要由调度平台触发。

  ```
  SYNC_SQL("SYNC_SQL", "同步数据源的组件"),
  OUT_SQL("OUT_SQL", "ETL到数据库")
  ```

  

- #### 数据模型

  展示模型列表。模型启动时会在数据集创建一条整理类型的数据集记录。

  模型对应模型表 **BI_ETL_MODEL**，EFFECT 为生效失效状态（对应NIFI的状态，目前未用到，默认创建即生效），

  ```
  ENABLE("ENABLE", "有效"),
  DISABLE("DISABLE", "失效");
  ```

  status 为模型启用状态（运行/停止）

  ```
  RUNNING("RUNNING", "运行中"),
  STOP("STOPPED", "停止");
  ```

  syncStatus为当前数据同步状态（是/否）

  ```
  YES("1", "是"),
  NO("0", "否");
  ```

  

- #### 执行记录

  展示数据模型的同步记录列表。任务类型分为如上所述的 数据同步与数据整理类型（**SYNC_SQL**与**OUT_SQL**类型）。

  

- #### 数据集

  数据集类型如下：

  ```
  DIRECT("0", "直连"),
  MODEL("1", "整理"),
  COPY("2", "复制"),
  DEFAULT("9", "默认文件")
  ```

  直连由**数据连接**直接创建而来，整理则是由 **模型启动** 后创建而来，复制则由**分析克隆**后创建而来，默认文件是项目初始化进去的。

  数据集里面的维度和度量，是进入预览后基于数据字段类型来动态判断的，若数据集类型为直连类型，远程端的表结构发生变化，那在预览时可能出现表字段不存在或维度与度量不准确情况，这种情况应有租户自己去控制。而通过整理来的数据集，在模型重启或者修改了模型模板时，都会删除对应的数据集，已保证数据集字段与维度度量的准确。





### 		分析管理

- #### 		我的分析![image-20210309170107662](C:\Users\junlicq\AppData\Roaming\Typora\typora-user-images\image-20210309170107662.png)

  ***我的分析***分为报表发布之后的展示列表，可通过新建文件夹对报表进行分类，报表可发布到指定的文件夹下。可对每一个报表进行重命名、查看、查看权限、编辑、数据集替换、数据集上传、删除操作

- 新建报表

  ![image-20210309171107346](C:\Users\junlicq\AppData\Roaming\Typora\typora-user-images\image-20210309171107346.png)

  报表可通过输入报表名称和描述信息来新建，报表新建之后会进入***草稿箱***，报表仅可使用部分组件。

- 新建仪表板

  ![image-20210309171351347](C:\Users\junlicq\AppData\Roaming\Typora\typora-user-images\image-20210309171351347.png)

  仪表板可通过输入仪表板名称和描述信息来新建，仪表板新建之后会进入***草稿箱***，仪表板可使用全部组件。

- 草稿箱

  ![image-20210309171519689](C:\Users\junlicq\AppData\Roaming\Typora\typora-user-images\image-20210309171519689.png)

  报表和仪表板在新建或者编辑之后会进入到***草稿箱***，在草稿箱编辑发布之后到***我的分析***查看，在草稿箱页面可对报表和仪表板进行删除、编辑操作。

- 图形指标库

  ![image-20210309172123271](C:\Users\junlicq\AppData\Roaming\Typora\typora-user-images\image-20210309172123271.png)

  在报表详情页面可选择将某一个组件添加至图形指标库，组件添加至图形指标库后为公共组件。图形指标库的组件可选择添加至其他报表。

  

### 		德勤方案

![image-20210309173159244](C:\Users\junlicq\AppData\Roaming\Typora\typora-user-images\image-20210309173159244.png)

德勤方案为内部用户新建、编辑好的模板。普通租户可查看、克隆德勤方案，将德勤方案的报表克隆到我的分析文件夹下。



## 详细设计

### 		接口调用

- 接口描述：新增报表

- 接口地址：/bi/ui/analyse/page/createAnalysePage

- Content-Type：application/json

- header：x-bdh-tenant-code

- 请求示例

  ```json
  {
  	"data": {
  		"deloitteFlag": "",
  		"des": "",
  		"haveNav": "",
  		"icon": "",
  		"name": "",
  		"parentId": "",
  		"type": ""
  	},
  	"ip": "",
  	"lang": "cn",
  	"operator": "",
  	"sid": "",
  	"source": "PC",
  	"tenantId": "",
  	"version": "1"
  }
  ```

- 响应示例：

  ```json
  {
  	"code": 0,
  	"data": {
  		"code": "",
  		"createDate": "",
  		"createUser": "",
  		"deloitteFlag": "",
  		"des": "",
  		"editId": "",
  		"haveNav": "",
  		"homePage": "",
  		"icon": "",
  		"id": "",
  		"isPublic": "",
  		"modifiedDate": "",
  		"modifiedUser": "",
  		"name": "",
  		"originPageId": "",
  		"parentId": "",
  		"permittedAction": [],
  		"publishId": "",
  		"type": ""
  	},
  	"detail": "",
  	"host": "",
  	"message": "",
  	"success": true,
  	"timestamp": "",
  	"traceId": ""
  }
  ```

  

### 	后台设计

- 项目结构

  ![image-20210309175400836](C:\Users\junlicq\AppData\Roaming\Typora\typora-user-images\image-20210309175400836.png)

  项目主要分为analyse包和collation包，analyse包为分析管理，collation为数据整理。

- 组件数据

  入参：

  ```json
  {
  	"data": {
  		"dataConfig": {
  			"dataModel": {
  				"category": [
  					{
  						"aggregateType": "",
  						"alias": "",
  						"contrastValue": {},
  						"dataType": "",
  						"dataUnit": "",
  						"defaultValue": "",
  						"formatType": "",
  						"frontendId": "",
  						"id": "",
  						"needGroup": true,
  						"orderType": "",
  						"precision": 0,
  						"quota": "",
  						"symbol": "",
  						"type": "",
  						"value": ""
  					}
  				],
  				"componentId": "",
  				"conditions": [
  					{
  						"aggregateType": "",
  						"formatType": "",
  						"id": [],
  						"quota": "",
  						"symbol": "",
  						"value": []
  					}
  				],
  				"customParams": {},
  				"page": 0,
  				"pageId": "",
  				"pageSize": 0,
  				"tableName": "",
  				"x": [
  					{
  						"aggregateType": "",
  						"alias": "",
  						"contrastValue": {},
  						"dataType": "",
  						"dataUnit": "",
  						"defaultValue": "",
  						"formatType": "",
  						"frontendId": "",
  						"id": "",
  						"needGroup": true,
  						"orderType": "",
  						"precision": 0,
  						"quota": "",
  						"symbol": "",
  						"type": "",
  						"value": ""
  					}
  				],
  				"y": [
  					{
  						"aggregateType": "",
  						"alias": "",
  						"contrastValue": {},
  						"dataType": "",
  						"dataUnit": "",
  						"defaultValue": "",
  						"formatType": "",
  						"frontendId": "",
  						"id": "",
  						"needGroup": true,
  						"orderType": "",
  						"precision": 0,
  						"quota": "",
  						"symbol": "",
  						"type": "",
  						"value": ""
  					}
  				],
  				"y2": [
  					{
  						"aggregateType": "",
  						"alias": "",
  						"contrastValue": {},
  						"dataType": "",
  						"dataUnit": "",
  						"defaultValue": "",
  						"formatType": "",
  						"frontendId": "",
  						"id": "",
  						"needGroup": true,
  						"orderType": "",
  						"precision": 0,
  						"quota": "",
  						"symbol": "",
  						"type": "",
  						"value": ""
  					}
  				]
  			},
  			"needAutoRefresh": true,
  			"tableType": ""
  		},
  		"fromDeloitte": "",
  		"pageId": "",
  		"type": ""
  	},
  	"ip": "",
  	"lang": "cn",
  	"operator": "",
  	"sid": "",
  	"source": "PC",
  	"tenantId": "",
  	"version": "1"
  }
  ```

  入参解释：

  - type：图表类型，对应***DataImplEnum***枚举类中的type值，表示请求数据的图表类型。

  - pageId：当前报表的id。

  - fromDeloitte：是否德勤方案报表，如果是德勤方案报表，将切换到内部租户库。

  - dataConfig：数据配置

    - needAutoRefresh：是否自动刷新（暂时无此功能）

    - tableType：表格子类型，对应***DataImplEnum***枚举类中的tableType值，表示请求数据的图表子类型。

    - dataModel：数据模型

      - pageId：当前报表的id。

      - componentId：组件id，前端组件id，与后端逻辑无关。

      - tableName：当前组件所用到的表，值为数据集code，查询时将先从数据集获取对应的表名称。

      - x：除线柱外的图表，所拖入的维度和度量字段，以及线柱的X轴字段。

        - frontendId：前端对应key值
        - id：数据库表字段名。
        - type：字段类型，string、int...
        - dataType：数据库字段类型，decimal...
        - alias：设置的别名，当设置别名时，返回的数据字段名也是别名。
        - quota：拖入字段属性：维度=WD，度量=DL。
        - 聚合方式：可对字段设置聚合方式，SUM、AVG...
        - 排序方式：当前字段数据的排序方式，DESC、ASC。
        - symbol：>，=，<
        - value：值
        - formatType：若维度是时间，可选择对时间格式化，取值范围为***FormatTypeEnum***枚举类
        - precision：精度，若字段是数值，可设置精度保留几位小数。
        - dataUnit：可选择对数据加上单位，取值范围为***DataUnitEnum***枚举类。
        - defaultValue：当查询数据为空返回的默认值。
        - needGroup：是否需要对字段进行聚合处理。

      - y：线柱的y轴字段，查询时放入X轴查询，返回数据时再放入y轴，方便前端找到对应的数据。

      - y2：当线柱图表为双y轴时，第二个y轴字段。在查询时将字段放入X轴查询，返回数据时再放入y2轴，方便前端找到对应的数据。

      - category：当用户拖入图例时，将图例字段放入此。

      - page：分页开始页。

      - pageSize：每页大小。

      - customParams：自定义参数

      - conditions：条件，当添加过滤组件或者数据集过滤时，可设置condition来过滤数据。

        构造方式：

        - 单选：

          ```json
          {
          	"formatType": "",
          	"id": ["porduct_classify"],
          	"quota": "WD",
          	"symbol": "EQ",
          	"value": ["家具"]
          }
          ```

          多选：

          ```json
          {
          	"formatType": "",
          	"id": ["porduct_classify"],
          	"quota": "WD",
          	"symbol": "EQ",
          	"value": ["家具"]
          },
          {
          	"formatType": "",
          	"id": ["porduct_classify"],
          	"quota": "WD",
          	"symbol": "EQ",
          	"value": ["办公用品"]
          }
          ```

          包含：

          ```json
          {
          	"formatType": "",
          	"id": ["porduct_classify"],
          	"quota": "WD",
          	"symbol": "LIKE",
          	"value": ["办公"]
          }
          ```

          不包含：

          ```json
          {
          	"formatType": "",
          	"id": ["porduct_classify"],
          	"quota": "WD",
          	"symbol": "NOT_LIKE",
          	"value": ["办公"]
          }
          ```

          开头为：

          ```json
          {
          	"formatType": "",
          	"id": ["porduct_classify"],
          	"quota": "WD",
          	"symbol": "LIKE_PRE",
          	"value": ["办公"]
          }
          ```

          结尾为：

          ```json
          {
          	"formatType": "",
          	"id": ["porduct_classify"],
          	"quota": "WD",
          	"symbol": "LIKE_END",
          	"value": ["办公"]
          }
          ```

          精确匹配：

          ```json
          {
          	"formatType": "",
          	"id": ["porduct_classify"],
          	"quota": "WD",
          	"symbol": "EQ",
          	"value": ["办公"]
          }
          ```

          不为空：

          ```json
          {
          	"formatType": "",
          	"id": ["porduct_classify"],
          	"quota": "WD",
          	"symbol": "IS NOT",
          	"value": 
          }
          ```

          日期类型

          ```json
          {
          	"formatType": "YEAR_MONTH_DAY",
          	"id": ["order_date"],
          	"quota": "WD",
          	"symbol": "LT",
          	"value": ["2020-10-01"]
          }
          ```

          度量类型

          ```json
          {
          	"formatType": "",
          	"aggregateType": "SUM" 
          	"id": ["quanlity"],
          	"quota": "DL",
          	"symbol": "GT",
          	"value": ["500"]
          }
          ```

          aggregateType取值范围：

          ```java
          SUM("SUM", "求和"),
          AVG("AVG", "均值"),
          MAX("MAX", "最大"),
          MIN("MIN", "最小"),
          COUNT("COUNT", "计数"),
          COUNT_DISTINCT("COUNT_DISTINCT", "去重复")
          ```

          symbol取值范围：

          ```java
          EQ("EQ", "=", "精确匹配"),
          GT("GT", ">", "大于"),
          GTE("GT", ">=", "大于等于"),
          LT("LT", "<", "小于"),
          LTE("LTE", "<=", "小于等于"),
          IN("IN", "IN", "包含"),
          NOT_IN("NOT_IN", "NOT IN", "不包含"),
          LIKE("LIKE", "LIKE", "模糊匹配"),
          NOT_LIKE("NOT_LIKE", "NOT LIKE", "模糊匹配"),
          LIKE_PRE("LIKE_PRE", "LIKE", "开头为"),
          LIKE_END("LIKE_END", "LIKE", "结尾为"),
          IS("IS",  "IS", ""),
          IS_NOT("IS_NOT", "IS NOT", "")
          ```

## 数据库设计

1. Table - BI_UI_ANALYSE_CATEGORY

   | Field Name    | Null？ | Type      | Description |
   | ------------- | ------ | --------- | ----------- |
   | ID            | N      | int       |             |
   | CODE          | Y      | varchar   | 文件夹编码  |
   | NAME          | Y      | varchar   | 文件夹名称  |
   | TYPE          | Y      | varchar   | 文件夹类型  |
   | DES           | Y      | varchar   | 文件夹描述  |
   | PARENT_ID     | Y      | varchar   | 上级id      |
   | ICON          | Y      | varchar   |             |
   | IP            | Y      | varchar   |             |
   | TENANT_ID     | N      | varchar   | 租户id      |
   | CREATE_DATE   | Y      | timestamp |             |
   | CREATE_USER   | N      | varchar   |             |
   | MODIFIED_DATE | Y      | timestamp |             |
   | MODIFIED_USER | Y      | varchar   |             |

   