package com.deloitte.bdh.data.analyse.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Author:LIJUN
 * Date:09/12/2020
 * Description:
 */
@Data
public class SelectPublishedPageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String resourceType;

    private String permittedAction;

    private String tenantId;

    private String name;

    private List<String> resourcesIds;

    private String isEdit;
}
