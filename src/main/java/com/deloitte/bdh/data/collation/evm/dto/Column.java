package com.deloitte.bdh.data.collation.evm.dto;

import com.deloitte.bdh.data.collation.model.BiReport;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Column {
    private Map<String, Cell> rowCell;

    public void build(List<BiReport> list) {
        this.rowCell = Maps.newLinkedHashMap();
        for (BiReport report : list) {
            Cell cell = Cell.builder()
                    .indexCode(report.getIndexCode())
                    .period(report.getPeriod())
                    .rowNo(report.getRowNo())
                    .colNo(report.getColNo())
                    .cell1(report.getCell1())
                    .cell2(report.getCell2())
                    .build();
            this.rowCell.put(report.getIndexCode(), cell);
        }
    }

    public String colSum(String... rowNos) {
        BigDecimal decimal = BigDecimal.ZERO;
        if (MapUtils.isNotEmpty(rowCell)) {
            for (String rowNo : rowNos) {
                //todo 数字校验
                String var = MapUtils.getString(rowCell, rowNo, "0");
                decimal.add(new BigDecimal(var));
            }
        }
        return decimal.toString();
    }
}
