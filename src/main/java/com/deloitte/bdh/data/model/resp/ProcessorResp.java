package com.deloitte.bdh.data.model.resp;


import com.deloitte.bdh.data.model.BiEtlParams;
import com.deloitte.bdh.data.model.BiEtlProcessor;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * <p>
 * 完整的信息
 * </p>
 *
 * @author lw
 * @since 2020-09-25
 */

@ApiModel(description = "完整的信息")
@Setter
@Getter
@ToString
public class ProcessorResp extends BiEtlProcessor {

    List<BiEtlParams> list;
}