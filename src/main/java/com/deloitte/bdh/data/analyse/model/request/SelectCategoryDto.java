package com.deloitte.bdh.data.analyse.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:LIJUN
 * Date:10/12/2020
 * Description:
 */
@Data
public class SelectCategoryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String resourceType;

    private String permittedAction;

    private String tenantId;

    private String name;

    private String type;
}
