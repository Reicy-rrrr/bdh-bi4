package com.deloitte.bdh.data.collation.nifi.dto;

import lombok.Data;


@Data
public class RunContext extends Nifi {
    private String previewCode = null;

}