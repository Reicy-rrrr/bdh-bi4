package com.deloitte.bdh.data.collation.integration;

import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.ComponentPreviewResp;
import com.deloitte.bdh.data.collation.model.resp.ComponentResp;


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
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrange(ArrangeComponentDto dto) throws Exception;

    /**
     * 引入拆分整理组件（创建）
     *
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeSplit(ArrangeSplitDto dto) throws Exception;

    /**
     * 引入移除字段整理组件（创建）
     *
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeRemove(ArrangeRemoveDto dto) throws Exception;

    /**
     * 引入替换字段内容整理组件（创建）
     *
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeReplace(ArrangeReplaceDto dto) throws Exception;

    /**
     * 引入合并字段整理组件（创建）
     *
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeCombine(ArrangeCombineDto dto) throws Exception;

    /**
     * 引入字段排空整理组件（创建）
     *
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeNonNull(ArrangeNonNullDto dto) throws Exception;

    /**
     * 引入转换大小写整理组件（创建）
     *
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeCaseConvert(ArrangeCaseConvertDto dto) throws Exception;

    /**
     * 引入去前后空格整理组件（创建）
     *
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeTrim(ArrangeTrimDto dto) throws Exception;

    /**
     * 引入去字段空格整理组件（创建）
     *
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeBlank(ArrangeBlankDto dto) throws Exception;

    /**
     * 引入分组整理组件（创建）
     *
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeGroup(ArrangeGroupDto dto) throws Exception;

    /**
     * 引入字段修改整理组件（创建）
     *
     * @param dto 整理组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeModify(ArrangeModifyDto dto) throws Exception;

    /**
     * 输出组件（创建）
     *
     * @param dto 输出组件dto
     * @return ComponentVo
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
