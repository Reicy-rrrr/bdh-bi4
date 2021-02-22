package com.deloitte.bdh.data.collation.service;

import com.deloitte.bdh.data.collation.enums.ArrangeTypeEnum;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.*;

import java.util.List;


public interface EtlService {

    /**
     * 预览表字段值
     *
     * @param dto 预览表字段值
     * @return List
     * @throws Exception
     */
    List<Object> previewField(ViewFieldValueDto dto) throws Exception;

    /**
     * 引入数据源组件（创建）
     *
     * @param dto 数据源组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent resourceJoin(ResourceComponentDto dto) throws Exception;

    /**
     * 修改数据源组件
     *
     * @param dto 数据源组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent resourceUpdate(UpdateResourceComponentDto dto) throws Exception;

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
    BiComponent joinCreate(JoinComponentDto dto) throws Exception;

    /**
     * 修改关联组件
     *
     * @param dto 修改关联组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent joinUpdate(UpdateJoinComponentDto dto) throws Exception;

    /**
     * 引入聚合组件（创建）
     *
     * @param dto 聚合组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent groupCreate(GroupComponentDto dto) throws Exception;

    /**
     * 修改聚合组件
     *
     * @param dto 修改聚合组件dto
     * @return BiComponent
     * @throws Exception
     */
    BiComponent groupUpdate(UpdateGroupComponentDto dto) throws Exception;

    /**
     * 引入整理组件（创建）
     *
     * @param dto         整理组件dto
     * @param arrangeType 整理类型
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeCreate(ArrangeComponentDto dto, ArrangeTypeEnum arrangeType) throws Exception;

    /**
     * 修改整理组件
     *
     * @param dto         修改整理组件dto
     * @param arrangeType 整理类型
     * @return BiComponent
     * @throws Exception
     */
    BiComponent arrangeUpdate(UpdateArrangeComponentDto dto, ArrangeTypeEnum arrangeType) throws Exception;

    /**
     * 输出组件（创建）
     *
     * @param dto 输出组件dto
     * @return ComponentVo
     * @throws Exception
     */
    BiComponent outCreate(OutComponentDto dto) throws Exception;

    /**
     * 修改输出组件
     *
     * @param dto 修改输出组件dto
     * @return ComponentVo
     * @throws Exception
     */
    BiComponent outUpdate(UpdateOutComponentDto dto) throws Exception;

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

    /**
     * 获取运算符列表
     *
     * @return List<CalculateOperatorResp>
     * @throws Exception
     */
    List<CalculateOperatorResp> getOperators() throws Exception;

    /**
     * 验证组件计算公式有效性
     *
     * @param dto 验证公式dto
     * @return String
     * @throws Exception
     */
    String checkFormula(ComponentFormulaCheckDto dto) throws Exception;
}
