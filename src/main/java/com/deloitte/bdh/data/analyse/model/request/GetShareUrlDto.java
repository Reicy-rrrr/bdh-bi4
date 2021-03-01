package com.deloitte.bdh.data.analyse.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class GetShareUrlDto implements Serializable {

    @ApiModelProperty(value = "page id")
    @NotNull(message = "报表id")
    private String pageId;

    @ApiModelProperty(value = "1")
    private String fromDeloitte;

}
