package com.deloitte.bdh.data.collation.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.deloitte.bdh.common.constant.DSConstant;
import com.deloitte.bdh.common.exception.BizException;
import com.deloitte.bdh.common.util.GenerateCodeUtil;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ResourceMessageEnum;
import com.deloitte.bdh.data.collation.model.BiComponentConnection;
import com.deloitte.bdh.data.collation.dao.bi.BiComponentConnectionMapper;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.request.ComponentLinkDto;
import com.deloitte.bdh.data.collation.service.BiComponentConnectionService;
import com.deloitte.bdh.common.base.AbstractService;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
@Service
@DS(DSConstant.BI_DB)
public class BiComponentConnectionServiceImpl extends AbstractService<BiComponentConnectionMapper, BiComponentConnection> implements BiComponentConnectionService {
    @Resource
    private BiComponentConnectionMapper mapper;
    @Resource
    private BiEtlModelService biEtlModelService;

    @Override
    public BiComponentConnection link(ComponentLinkDto dto) {
        BiEtlModel model = biEtlModelService.getById(dto.getModelId());
        if (null == model) {
            throw new BizException(ResourceMessageEnum.MODEL_NOT_EXIST.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.MODEL_NOT_EXIST.getMessage(), ThreadLocalHolder.getLang()));
        }
        if (dto.getFromComponentCode().equals(dto.getToComponentCode())) {
            throw new BizException(ResourceMessageEnum.TARGET_NOT_SELF.getCode(),
                    localeMessageService.getMessage(ResourceMessageEnum.TARGET_NOT_SELF.getMessage(), ThreadLocalHolder.getLang()));
        }
        BiComponentConnection connection = new BiComponentConnection();
        connection.setCode(GenerateCodeUtil.genConnects());
        connection.setFromComponentCode(dto.getFromComponentCode());
        connection.setToComponentCode(dto.getToComponentCode());
        connection.setRefModelCode(model.getCode());
        connection.setVersion("1");
        connection.setTenantId(ThreadLocalHolder.getTenantId());
        mapper.insert(connection);
        return connection;
    }
}
