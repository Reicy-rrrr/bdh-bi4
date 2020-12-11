package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.model.BiComponentParams;
import com.deloitte.bdh.common.base.Service;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
public interface BiComponentParamsService extends Service<BiComponentParams> {

    /**
     * 判断是否存在指定key 和 value 的参数
     *
     * @param paramKey   参数key
     * @param paramValue 参数value
     * @return boolean
     */
    boolean isParamExists(String paramKey, String paramValue);
}
