package com.deloitte.bdh.data.collation.model.resp;

import com.deloitte.bdh.data.collation.model.BiEtlModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelResp extends BiEtlModel {

    private String cronDesc;

}
