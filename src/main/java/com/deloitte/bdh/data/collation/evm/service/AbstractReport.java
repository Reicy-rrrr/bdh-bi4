package com.deloitte.bdh.data.collation.evm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.data.collation.evm.dto.Sheet;
import com.deloitte.bdh.data.collation.evm.enums.ReportCodeEnum;
import com.deloitte.bdh.data.collation.model.BiReport;
import com.deloitte.bdh.data.collation.service.BiReportService;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractReport implements ReportService {
    @Resource
    private BiReportService reportService;

    abstract protected ReportCodeEnum getType();

    abstract protected void clear();

    protected Map<String, Sheet> buildEntity(List<String> codes) {
        Map<String, Sheet> map = Maps.newLinkedHashMap();
        for (String code : codes) {
            List<BiReport> list = reportService.list(new LambdaQueryWrapper<BiReport>()
                    .eq(BiReport::getReportCode, code).orderByAsc(BiReport::getPeriod));
            if (CollectionUtils.isNotEmpty(list)) {
                Sheet sheet = new Sheet();
                sheet.build(list);
                map.put(code, sheet);
            }
        }
        return map;
    }

    abstract protected List<LinkedHashMap<String, Object>> assembly(Map<String, Sheet> map);


    @Override
    public void process() {
        Map<String, Sheet> map = buildEntity(getType().relySheets().left);
        clear();
        assembly(map);
    }


}