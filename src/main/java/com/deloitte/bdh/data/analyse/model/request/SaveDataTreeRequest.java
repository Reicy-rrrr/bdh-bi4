package com.deloitte.bdh.data.analyse.model.request;

import com.deloitte.bdh.data.analyse.model.resp.AnalyseFolderTree;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

/**
 * Author:LIJUN
 * Date:09/11/2020
 * Description:
 */
@Data
public class SaveDataTreeRequest implements Serializable {

    @ApiModelProperty(value = "树状结构", required = true)
    @NotEmpty(message = "树不能为空")
    private List<AnalyseFolderTree> treeList;

}
