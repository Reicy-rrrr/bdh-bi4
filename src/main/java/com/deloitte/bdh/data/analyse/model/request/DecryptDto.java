package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author:LIJUN
 * Date:17/12/2020
 * Description:
 */
@Data
public class DecryptDto {

    /**
     * 密文
     */
    @ApiModelProperty(value = "密文")
    private String ciphertext;

    /**
     * 解密类型
     */
    @ApiModelProperty(value = "解密类型:0-公开分享，1-订阅")
    private String decryptType;
}
