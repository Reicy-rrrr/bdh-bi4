package com.deloitte.bdh.data.collation.evm.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    private int order;
    private String targetCode;
    private String targetName;
    private String expression;


}
