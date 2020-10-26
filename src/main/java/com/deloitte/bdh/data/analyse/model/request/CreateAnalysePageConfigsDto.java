package com.deloitte.bdh.data.analyse.model.request;

import lombok.Data;

@Data
public class CreateAnalysePageConfigsDto {
    String pageId;
    String content;
    String tenantId;
}
