package com.deloitte.bdh.common.constant;

import java.math.BigDecimal;

/**
 * Created by yuankliu on 08/05/2018.
 */
public class CommonConstant {
    public static final String NEW_LINE = "\r\n";

    public static final String EN = "en";
    public static final String CN = "cn";
    public static final String SEP = ":";

    public static final String BI_MENU_TYPE = "2";
    public static final String BI_TARGER_TYPE = "3";

    public static final String MONTHLY = "monthly";
    public static final String ANNUAL = "annual";

    public static final String AES_TOKEN = "deloitte_tax_bdh";

    // 密码即将过期提醒消息类型
    public static final String MESSAGE_TYPE_REMIND = "remind";
    /**
     * 验证码发送间隔
     */
    public static final int CODE_EXPIRETIME_BASE = 60;
    /**
     * 验证码超时时间
     */
    public static final int CODE_EXPIRETIME = 60 * 5;
    /**
     * 短信验证码长度
     */
    public static final int SMS_CODE_LENGTH = 6;

    public static final String PORTAL_COMMON_EMAIL = "EMAIL";
    public static final String PORTAL_COMMON_SMS = "SMS";

    public static final String USER_DEFAULT_IMAGES = "userDefaultImages/";
    public static final String IMAGES = "images/";

    public static final int LOGIN_RETRY_NUM = 10;


    /**
     * 记录是否有效
     * <p>
     * 1	有效 0 无效
     */
    public static final int ACTIVE_FLAG_ACTIVE = 1;
    public static final int ACTIVE_FLAG_INACTIVE = 0;

    /**
     * 单据类型 HELP：在线帮助单据;FEEDBACK：反馈单据
     */
    public static final String WF_ITEM_TYPE_HELP = "HELP";
    public static final String WF_ITEM_TYPE_FEEDBACK = "FEEDBACK";

    /**
     * 工作台system_id
     */
    public static final BigDecimal MYWORK_SYSTEM_ID = new BigDecimal(1000);

    /**
     * 分页查询：默认当前页
     */
    public static final Integer DEFAULT_PAGE = 1;
    /**
     * 分页查询：默认每页记录数
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大列数（字段数，文件列数）
     */
    public static final Integer MAX_COLUMN_SIZE = 100;

    public static final String INTERNAL_DATABASE = "1001";
}
