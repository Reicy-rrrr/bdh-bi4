package com.deloitte.bdh.data.collation.dao.bi;

import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.common.base.Mapper;
import com.deloitte.bdh.data.collation.model.BiComponentTree;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lw
 * @since 2020-10-26
 */
public interface BiComponentMapper extends Mapper<BiComponent> {
    /**
     * 查询组件树形结构
     *
     * @param modelCode
     * @param componentCode
     * @return
     */
    BiComponentTree selectByEnd(@Param("modelCode") String modelCode, @Param("componentCode") String componentCode);

    /**
     * 查询组件树形结构
     *
     * @param modelCode
     * @param componentCode
     * @return
     */
    BiComponentTree selectTree(@Param("modelCode") String modelCode, @Param("componentCode") String componentCode);
}
