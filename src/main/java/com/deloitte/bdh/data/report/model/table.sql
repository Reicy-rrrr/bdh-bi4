-- 数据模型
create table BI_UI_DATA_MODEL
(
    ID                  int auto_increment
        primary key,
    PARENT_ID int null comment '上级id',
    NAME varchar(150) null comment '数据模型名称',
    DATASOURCE_ID  int null comment '数据源id',
    DATASOURCE_NAME  varchar(150) null comment '数据模型名称',
    TYPE varchar(150) null comment '文件夹/数据模型',
    CREATE_DATE         timestamp(6) not null,
    CREATE_USER         varchar(50)  not null,
    MODIFIED_DATE       timestamp(6) null,
    MODIFIED_USER       varchar(50)  null,
    IP                  varchar(40)  null,
    TENANT_ID           varchar(50)  not null
);
-- 数据模型 数据来源
create table BI_UI_DATA_MODEL_SOURCE
(
    ID                  int auto_increment
        primary key,
    MODEL_ID int null comment '数据模型id',
    SOURCE_TYPE varchar(150) null comment '来源类型 表/视图',
    DATASOURCE_ID  int null comment '数据源id',
    TABLE_NAME varchar(150) null comment '数据表名称',
    VIEW_SQL varchar(250) null comment '视图内容',
    CREATE_DATE         timestamp(6) not null,
    CREATE_USER         varchar(50)  not null,
    MODIFIED_DATE       timestamp(6) null,
    MODIFIED_USER       varchar(50)  null,
    IP                  varchar(40)  null,
    TENANT_ID           varchar(50)  not null
);
-- 数据模型 数据来源
create table BI_UI_DATA_MODEL_SOURCE_RELATION
(
    ID                  int auto_increment
        primary key,
    SOURCE_ID int null comment '源id',
    TARGET_ID  int null comment '目标id',
    JOIN_TYPE varchar(50)  null comment 'JOIN类型',
    CREATE_DATE         timestamp(6) not null,
    CREATE_USER         varchar(50)  not null,
    MODIFIED_DATE       timestamp(6) null,
    MODIFIED_USER       varchar(50)  null,
    IP                  varchar(40)  null,
    TENANT_ID           varchar(50)  not null
);
-- 数据模型 数据来源
create table BI_UI_DATA_MODEL_SOURCE_RELATION_FIELDS
(
    ID                  int auto_increment
        primary key,
    RELATION_ID int null comment '关系id',
    LEFT_FIELD  varchar(150)  null comment '左边连接字段',
    RIGHT_FIELD   varchar(150)  null comment '右边连接字段',
    CREATE_DATE         timestamp(6) not null,
    CREATE_USER         varchar(50)  not null,
    MODIFIED_DATE       timestamp(6) null,
    MODIFIED_USER       varchar(50)  null,
    IP                  varchar(40)  null,
    TENANT_ID           varchar(50)  not null
);

-- 数据模型 数据来源
create table BI_UI_DATA_MODEL_FOLDER
(
    ID                  int auto_increment
        primary key,
    MODEL_ID int null comment '数据模型id',
    PARENT_ID int null comment '上级id',
    NAME  varchar(150)  null comment '名称',
    TYPE   varchar(150)  null comment '右边连接字段',
    CREATE_DATE         timestamp(6) not null,
    CREATE_USER         varchar(50)  not null,
    MODIFIED_DATE       timestamp(6) null,
    MODIFIED_USER       varchar(50)  null,
    IP                  varchar(40)  null,
    TENANT_ID           varchar(50)  not null
);

create table BI_UI_DATA_MODEL_FIELD
(
    ID                  int auto_increment
        primary key,
    MODEL_ID int null comment '数据模型id',
    FOLDER_ID int null comment '所在文件夹',
    NAME  varchar(150)  null comment '别名',
    SOURCE_ID  varchar(150)  null comment '来源id',
    COLUMN  varchar(150)  null comment '物理字段名',
    IS_HIDDEN BIT  null comment '是否隐藏',
    SORT_ORDER int null comment '排序',
    DATA_TYPE varchar(150) null comment '数据类型',
    IS_DIMENTION BIT null comment '是否维度',
    IS_MENSURE BIT null comment '是否度量',
    GEO_INFO_TYPE varchar(150) null comment '地理信息类型',
    CREATE_DATE         timestamp(6) not null,
    CREATE_USER         varchar(50)  not null,
    MODIFIED_DATE       timestamp(6) null,
    MODIFIED_USER       varchar(50)  null,
    IP                  varchar(40)  null,
    TENANT_ID           varchar(50)  not null
);

-- auto-generated definition
create table BI_UI_REPORT_PAGE
(
    ID                  int auto_increment
        primary key,
    CODE varchar(150) null comment '报表编码',
    NAME varchar(150) null comment '报表名称',
    DES  varchar(250) null comment '报表描述',
    CREATE_DATE         timestamp(6) not null,
    CREATE_USER         varchar(50)  not null,
    MODIFIED_DATE       timestamp(6) null,
    MODIFIED_USER       varchar(50)  null,
    IP                  varchar(40)  null,
    TENANT_ID           varchar(50)  not null
);

-- auto-generated definition
create table BI_UI_REPORT_PAGE_CONFIG
(
    ID                  int auto_increment
        primary key,
    PAGE_ID int NOT NULL  comment '报表ID',
    CONTENT json null comment 'json配置内容',
    CREATE_DATE         timestamp(6) not null,
    CREATE_USER         varchar(50)  not null,
    MODIFIED_DATE       timestamp(6) null,
    MODIFIED_USER       varchar(50)  null,
    IP                  varchar(40)  null,
    TENANT_ID           varchar(50)  not null
);

-- auto-generated definition
create table BI_UI_DATA_MODEL
(
    ID                  int auto_increment
        primary key,
    PAGE_ID int NOT NULL  comment '报表ID',
    CONTENT json null comment 'json配置内容',
    CREATE_DATE         timestamp(6) not null,
    CREATE_USER         varchar(50)  not null,
    MODIFIED_DATE       timestamp(6) null,
    MODIFIED_USER       varchar(50)  null,
    IP                  varchar(40)  null,
    TENANT_ID           varchar(50)  not null
);

-- auto-generated definition
create table BI_UI_REPORT_DEMO_SALE_DETAIL
(
    ID                  int auto_increment
        primary key,
    ROW_ID decimal null comment '行 ID',
    SHIP_DAY  bigint NULL  comment '发运天数',
    ORDER_DATE_YEAR varchar(200) null comment '销售年份',
    IS_RETURN varchar(200) null comment '已退货？',
    RETURN_NOTE varchar(200) null comment '退货注释',
    APPROVER varchar(200) null comment '审批人',
    COUNTRY varchar(200) null comment '国家/地区',
    REGION varchar(200) null comment '销售区域',
    PROVINCE varchar(200) null comment '省/自治区',
    CITY varchar(200) null comment '城市',
    DISCOUNT decimal null comment '折扣',
    ORDER_DATE datetime null comment '订单日期',
    DELIVERY_METHOD varchar(200) null comment '邮寄方式',
    CUSTOMER_ID varchar(200) null comment '客户ID',
    CUSTOMER_NAME varchar(200) null comment '客户名称',
    CUSTOMER_ varchar(200) null comment '细分',
    PRODUCT_ID varchar(200) null comment '产品 ID',
    PRODUCT_CLASSIFY varchar(200) null comment '类别',
    PRODUCT_SUBCLASS varchar(200) null comment '子类别',
    PRODUCT_NAME varchar(200) null comment '产品名称',
    QUANTITY int null comment '数量',
    PROFIT decimal null comment '利润',
    SALES decimal null comment '销售额',
    ORDER_ID varchar(200) null comment '订单 ID',
    RETURN_REASON varchar(200) null comment '退货原因',
    DELIVERY_DATE datetime null comment '发货日期',


    CREATE_DATE         timestamp(6) not null,
    CREATE_USER         varchar(50)  not null,
    MODIFIED_DATE       timestamp(6) null,
    MODIFIED_USER       varchar(50)  null,
    IP                  varchar(40)  null,
    TENANT_ID           varchar(50)  not null
);