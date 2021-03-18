package com.deloitte.bdh.data.collation.evm.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Rule {
    private String targetCode;
    private String targetName;
    private String expression;
    private String type;

    public Rule() {
    }

    public Rule(String targetCode, String targetName, String expression) {
        this.targetCode = targetCode;
        this.targetName = targetName;
        this.expression = expression;
    }

    public Rule(String targetCode, String targetName, String expression, String type) {
        this.targetCode = targetCode;
        this.targetName = targetName;
        this.expression = expression;
        this.type = type;
    }
}
