package com.deloitte.bdh.data.analyse.model.request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * Author:LIJUN
 * Date:25/02/2021
 * Description:
 */
@Data
public class CopySourceDto {

    private String pageId;

    private JSONObject content;

    private JSONArray childrenArr;

    private List<String> originCodeList;

    private List<String> linkPageId;

    private String pageName;

}
