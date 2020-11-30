package com.deloitte.bdh.data.collation.integration;

import com.deloitte.bdh.data.collation.enums.ArrangeTypeEnum;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.ComponentPreviewResp;
import com.deloitte.bdh.data.collation.model.resp.ComponentResp;
import com.deloitte.bdh.data.collation.model.resp.ResourceViewResp;

import java.util.List;


public interface EtlService {

    /**
     * 引入数据源组件（创建）
     *
     * @param dto 数据源组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent resource(ResourceComponentDto dto) throws Exception;

    /**
     * 数据源组件同步状态实时查看
     *
     * @param code 数据源组件编码
     * @return ResourceViewResp
     * @throws Exception
     */
    ResourceViewResp realTimeView(String code);

    /**
     * 引入关联组件（创建）
     *
     * @param dto 关联组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent join(JoinComponentDto dto) throws Exception;

    /**
     * 引入聚合组件（创建）
     *
     * @param dto 聚合组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent group(GroupComponentDto dto) throws Exception;

    /**
     * 引入整理组件（创建）
     *
     * @param dto         整理组件dto
     * @param arrangeType 整理类型
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrange(ArrangeComponentDto dto, ArrangeTypeEnum arrangeType) throws Exception;

    /**
     * 输出组件（创建）
     *
     * @param dto 输出组件dto
     * @return ComponentVo
     * @throws Exception
     */
    BiComponent out(OutComponentDto dto) throws Exception;

    /**
     * 处理组件
     *
     * @param dto
     * @return ComponentVo
     * @throws Exception
     */
    ComponentResp handle(ComponentPreviewDto dto) throws Exception;

    /**
     * 预览组件数据
     *
     * @param dto 组件预览dto
     * @return ComponentPreviewVo
     * @throws Exception
     */
    ComponentPreviewResp previewData(ComponentPreviewDto dto) throws Exception;

    /**
     * 预览组件空值数据
     *
     * @param dto 组件预览dto
     * @return ComponentPreviewVo
     * @throws Exception
     */
    ComponentPreviewResp previewNullData(ComponentPreviewNullDto dto) throws Exception;

    /**
     * 预览组件字段唯一值
     *
     * @param dto 组件预览dto
     * @return List
     * @throws Exception
     */
    List<Object> previewFieldData(ComponentPreviewFieldDto dto) throws Exception;

    /**
     * 预览组件sql
     *
     * @param dto 组件预览dto
     * @return ComponentPreviewVo
     * @throws Exception
     */
    String previewSql(ComponentPreviewDto dto) throws Exception;

    /**
     * 移除组件
     *
     * @param code
     * @throws Exception
     */
    void remove(String code) throws Exception;
}
