package com.deloitte.bdh.data.collation.evm.dto;

import com.deloitte.bdh.data.collation.model.BiReport;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sheet {
    private int type = 0;
    private String reportCode;
    private String reportName;
    private Map<String, Row> rowMap;
    private Map<String, Column> columnMap;

    public void build(List<BiReport> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            this.reportCode = list.get(0).getReportCode();
            this.reportName = list.get(0).getReportName();

            this.rowMap = Maps.newLinkedHashMap();
            this.columnMap = Maps.newLinkedHashMap();
            for (BiReport report : list) {
                String indexCode = report.getIndexCode();
                Row row = new Row();
                List<BiReport> rowList = list.stream().filter(s -> s.getIndexCode().equals(indexCode)).collect(Collectors.toList());
                row.build(rowList);
                this.rowMap.put(indexCode, row);

                String period = report.getPeriod();
                Column column = new Column();
                List<BiReport> colList = list.stream().filter(s -> s.getPeriod().equals(period)).collect(Collectors.toList());
                column.build(colList);
                this.columnMap.put(period, column);
            }
        }
    }

    /**
     * 获取某行中某列的值
     * 根据期间/列编号、指标编码/行编号 获取 单元格值
     * y: period or colNo,x: index or rowNo
     */
    public String xCellValue(String x, String y) {
        return rowMap.get(x).getColCell().get(y).getCell2();
    }

    /**
     * 获取某行中某几列的值求sum
     * 根据期间/列编号、指标编码/行编号 获取 单元格值
     * y: period or colNo,x: index or rowNo
     */
    public String xCellValueToSum(String x, String... y) {
        return rowMap.get(x).colSum(y);
    }

    /**
     * 获取当前单元格当后列的一个值
     */
    public String xCellValueAfter(String x, String y) {
        if (!rowMap.containsKey(y)) {
            return null;
        }
        return rowMap.get(y).getAfterValue(x);
    }

    /**
     * 获取当前单元格当前列的一个值
     */
    public String xCellValuePre(String x, String y) {
        if (!rowMap.containsKey(y)) {
            return null;
        }
        return rowMap.get(y).getPreValue(x);
    }

    /**
     * 获取某列下某行的值
     * 根据期间/列编号, 获取指标编码/行编号 单元格值
     * y: period or colNo,x: index or rowNo
     */
    public String yCellValue(String y, String x) {
        if (!columnMap.containsKey(y)) {
            return "0";
        }
        Map<String, Cell> rowCell = columnMap.get(y).getRowCell();
        if (!rowCell.containsKey(x)) {
            return "0";
        }
        return rowCell.get(x).getCell2();
    }

    /**
     * 获取某列中某几行的值求sum
     * 根据期间/列编号、指标编码/行编号 获取 单元格值
     * y: period or colNo,x: index or rowNo
     */
    public String yCellValue(String y, String... x) {
        return columnMap.get(y).colSum(x);
    }

    /**
     * 获取所有的列号
     * 根据期间/列编号、指标编码/行编号 获取 单元格值
     * y: period or colNo,x: index or rowNo
     */
    public List<String> yCellNo() {
        return new ArrayList<>(columnMap.keySet());
    }
}
