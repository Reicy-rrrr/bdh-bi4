package com.deloitte.bdh.data.collation.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Author:LIJUN
 * Date:28/12/2020
 * Description:
 */
@Data
public class SelectDataSetDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String resourceType;

    private String permittedAction;

    private String tenantId;

    private List<String> parentIdList;

    private String isFile;

    private List<String> createUserList;

}
