package com.deloitte.bdh.data.analyse.constants;

import com.deloitte.bdh.data.collation.enums.MysqlDataTypeEnum;
import com.deloitte.bdh.data.collation.enums.OracleDataTypeEnum;
import com.google.common.collect.Lists;

import java.util.List;

public class AnalyseConstants {

    /**
     * 默认父级ID
     */
    public static final String PARENT_ID_ZERO = "0";
    /**
     * 已发布
     */
    public static final String PAGE_CONFIG_PUBLISH = "PUBLISH";
    /**
     * 编辑中
     */
    public static final String PAGE_CONFIG_EDIT = "EDIT";

    public static final String EMAIL_URL = "/fndPortalSendLog/sendEmailAndSms";

    public static final String PORTAL_COMMON_EMAIL = "EMAIL";

    public static final String EMAIL_TEMPLATE_SUBSCRIBE = "screenshot";

    public static final String DOCUMENT_DIR = "bdhdocuments/";


    public static final List<String> MENSURE_TYPE = Lists.newArrayList(
            OracleDataTypeEnum.FLOAT.getType().toUpperCase(),
            OracleDataTypeEnum.DOUBLE.getType().toUpperCase(),
            OracleDataTypeEnum.DOUBLE_PRECISION.getType().toUpperCase(),
            OracleDataTypeEnum.NUMBER.getType().toUpperCase(),
            OracleDataTypeEnum.DECIMAL.getType().toUpperCase(),
            OracleDataTypeEnum.BINARY_FLOAT.getType().toUpperCase(),
            OracleDataTypeEnum.BINARY_DOUBLE.getType().toUpperCase(),
            OracleDataTypeEnum.DEC.getType().toUpperCase(),

            MysqlDataTypeEnum.TINYINT.getType().toUpperCase(),
            MysqlDataTypeEnum.SMALLINT.getType().toUpperCase(),
            MysqlDataTypeEnum.MEDIUMINT.getType().toUpperCase(),
            MysqlDataTypeEnum.INT.getType().toUpperCase(),
            MysqlDataTypeEnum.INTEGER.getType().toUpperCase(),
            MysqlDataTypeEnum.BIGINT.getType().toUpperCase(),
            MysqlDataTypeEnum.FLOAT.getType().toUpperCase(),
            MysqlDataTypeEnum.DOUBLE.getType().toUpperCase(),
            MysqlDataTypeEnum.DECIMAL.getType().toUpperCase()
    );


}
