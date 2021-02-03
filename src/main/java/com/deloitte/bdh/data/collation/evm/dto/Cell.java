package com.deloitte.bdh.data.collation.evm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cell {
    private String indexCode;
    private String period;
    private String rowNo;
    private String colNo;
    private String cell1;
    private String cell2;


}
