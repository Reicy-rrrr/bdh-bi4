package com.deloitte.bdh.data.collation.evm.dto;

import com.deloitte.bdh.data.collation.model.BiReport;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Row {
    private LinkedHashMap<String, Cell> colCell;
    private LinkedList<String> colNos;

    public void build(List<BiReport> list) {
        this.colCell = Maps.newLinkedHashMap();
        this.colNos = Lists.newLinkedList();
        for (BiReport report : list) {
            Cell cell = Cell.builder()
                    .indexCode(report.getIndexCode())
                    .period(report.getPeriod())
                    .rowNo(report.getRowNo())
                    .colNo(report.getColNo())
                    .cell1(report.getCell1())
                    .cell2(report.getCell2())
                    .build();
            this.colCell.put(report.getPeriod(), cell);
            colNos.add(report.getPeriod());
        }
    }


    public String colSum(String... colNos) {
        BigDecimal decimal = BigDecimal.ZERO;
        if (MapUtils.isNotEmpty(colCell)) {
            for (String colNo : colNos) {
                //todo 数字校验
                String var = MapUtils.getString(colCell, colNo, "0");
                decimal.add(new BigDecimal(var));
            }
        }
        return decimal.toString();
    }

    public String getPreValue(String colNo) {
        int index = colNos.indexOf(colNo);
        if (0 == index) {
            return null;
        }
        return colNos.get(index - 1);
    }

    public String getAfterValue(String colNo) {
        int index = colNos.indexOf(colNo);
        if (colNos.size() - 1 == index) {
            return null;
        }
        return colNos.get(index + 1);
    }
}
