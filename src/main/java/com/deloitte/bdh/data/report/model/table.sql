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