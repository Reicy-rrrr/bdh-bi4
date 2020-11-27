package com.deloitte.bdh.data.analyse.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.json.JsonUtil;
import com.deloitte.bdh.common.util.AesUtil;
import com.deloitte.bdh.common.util.Md5Util;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePublicShare;
import com.deloitte.bdh.data.analyse.dao.bi.BiUiAnalysePublicShareMapper;
import com.deloitte.bdh.data.analyse.model.request.AnalysePublicShareDto;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePublicShareService;
import com.deloitte.bdh.common.base.AbstractService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-11-23
 */
@Service
@DS(DSConstant.BI_DB)
public class BiUiAnalysePublicShareServiceImpl extends AbstractService<BiUiAnalysePublicShareMapper, BiUiAnalysePublicShare> implements BiUiAnalysePublicShareService {

    @Value("${bi.analyse.encryptPass}")
    private String encryptPass;
    @Value("${bi.analyse.public.address}")
    private String publicAddress;
    @Resource
    private BiUiAnalysePublicShareMapper shareMapper;

    @Override
    public String update(AnalysePublicShareDto dto) {
        BiUiAnalysePublicShare share = shareMapper.selectOne(new LambdaQueryWrapper<BiUiAnalysePublicShare>()
                .eq(BiUiAnalysePublicShare::getRefPageId, dto.getPageId())
        );
        if (null == share) {
            throw new RuntimeException("未找到对应的目标对象:" + JsonUtil.readObjToJson(dto));
        }
        if ("0".equals(dto.getType())) {
            share.setAddress("");
            share.setCode("");
            share.setPassword("");
        } else {
            Map<String, Object> params = Maps.newHashMap();
            params.put("tenantCode", ThreadLocalHolder.getTenantCode());
            params.put("refPageId", dto.getPageId());
            params.put("isEncrypt", "0");
            if ("2".equals(dto.getType())) {
                params.put("isEncrypt", "1");
                share.setPassword(Md5Util.getMD5(dto.getPassword(), encryptPass + ThreadLocalHolder.getTenantCode()));
            }
            share.setCode(AesUtil.encryptNoSymbol(JsonUtil.readObjToJson(params), encryptPass));
            share.setAddress(publicAddress);
        }
        share.setType(dto.getType());
        shareMapper.updateById(share);
        return share.getAddress() + "/" + share.getCode();
    }

}
