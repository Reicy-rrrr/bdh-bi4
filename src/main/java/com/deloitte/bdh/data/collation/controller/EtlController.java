package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.enums.ArrangeTypeEnum;
import com.deloitte.bdh.data.collation.integration.EtlService;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.ComponentPreviewResp;
import com.deloitte.bdh.data.collation.model.resp.ComponentResp;
import com.deloitte.bdh.data.collation.model.resp.ResourceViewResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-09-25
 */
@Api(tags = "数据整理-整理组件相关")
@RestController
@RequestMapping("/bi/etl")
public class EtlController {
    @Autowired
    private EtlService etlService;


    @ApiOperation(value = "引入数据源组件", notes = "引入数据源组件")
    @PostMapping("/resource/join")
    public RetResult<BiComponent> resourceJoin(@RequestBody @Validated RetRequest<ResourceComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.resourceJoin(request.getData()));
    }

    @ApiOperation(value = "修改数据源组件", notes = "修改数据源组件")
    @PostMapping("/resource/update")
    public RetResult<BiComponent> resourceUpdate(@RequestBody @Validated RetRequest<UpdateResourceComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.resourceUpdate(request.getData()));
    }

    @ApiOperation(value = "数据源组件同步状态实时查看", notes = "数据源组件同步状态实时查看")
    @PostMapping("/resource/realTimeView")
    public RetResult<ResourceViewResp> realTimeView(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.realTimeView(request.getData()));
    }

    @ApiOperation(value = "引入输出组件", notes = "引入输出组件")
    @PostMapping("/out/join")
    public RetResult<BiComponent> outCreate(@RequestBody @Validated RetRequest<OutComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.outCreate(request.getData()));
    }

    @ApiOperation(value = "修改输出组件", notes = "修改输出组件")
    @PostMapping("/out/update")
    public RetResult<BiComponent> outUpdate(@RequestBody @Validated RetRequest<UpdateOutComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.outUpdate(request.getData()));
    }

    @ApiOperation(value = "引入关联组件", notes = "引入关联组件")
    @PostMapping("/join/join")
    public RetResult<BiComponent> joinCreate(@RequestBody @Validated RetRequest<JoinComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.joinCreate(request.getData()));
    }

    @ApiOperation(value = "修改关联组件", notes = "修改关联组件")
    @PostMapping("/join/update")
    public RetResult<BiComponent> joinUpdate(@RequestBody @Validated RetRequest<UpdateJoinComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.joinUpdate(request.getData()));
    }

    @ApiOperation(value = "引入聚合组件", notes = "引入聚合组件")
    @PostMapping("/group/join")
    public RetResult<BiComponent> groupCreate(@RequestBody @Validated RetRequest<GroupComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.groupCreate(request.getData()));
    }

    @ApiOperation(value = "修改聚合组件", notes = "修改聚合组件")
    @PostMapping("/group/update")
    public RetResult<BiComponent> groupUpdate(@RequestBody @Validated RetRequest<UpdateGroupComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.groupUpdate(request.getData()));
    }

    @ApiOperation(value = "引入整理组件(拆分)", notes = "引入整理组件(拆分)")
    @PostMapping("/arrange/split/join")
    public RetResult<BiComponent> arrangeSplit(@RequestBody @Validated RetRequest<ArrangeSplitDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCreate(request.getData(), ArrangeTypeEnum.SPLIT));
    }


    @ApiOperation(value = "引入整理组件(移除)", notes = "引入整理组件(移除)")
    @PostMapping("/arrange/remove/join")
    public RetResult<BiComponent> arrangeRemove(@RequestBody @Validated RetRequest<ArrangeRemoveDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCreate(request.getData(), ArrangeTypeEnum.REMOVE));
    }

    @ApiOperation(value = "引入整理组件(替换)", notes = "引入整理组件(替换)")
    @PostMapping("/arrange/replace/join")
    public RetResult<BiComponent> arrangeReplace(@RequestBody @Validated RetRequest<ArrangeReplaceDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCreate(request.getData(), ArrangeTypeEnum.REPLACE));
    }

    @ApiOperation(value = "引入整理组件(合并)", notes = "引入整理组件(合并)")
    @PostMapping("/arrange/combine/join")
    public RetResult<BiComponent> arrangeCombine(@RequestBody @Validated RetRequest<ArrangeCombineDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCreate(request.getData(), ArrangeTypeEnum.COMBINE));
    }

    @ApiOperation(value = "引入整理组件(排空)", notes = "引入整理组件(排空)")
    @PostMapping("/arrange/nonnull/join")
    public RetResult<BiComponent> arrangeNonNull(@RequestBody @Validated RetRequest<ArrangeNonNullDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCreate(request.getData(), ArrangeTypeEnum.NON_NULL));
    }


    @ApiOperation(value = "引入整理组件(大小写转换)", notes = "引入整理组件(大小写转换)")
    @PostMapping("/arrange/case/join")
    public RetResult<BiComponent> arrangeCaseConvert(@RequestBody @Validated RetRequest<ArrangeCaseConvertDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCreate(request.getData(), ArrangeTypeEnum.CONVERT_CASE));
    }

    @ApiOperation(value = "引入整理组件(去字段空格)", notes = "引入整理组件(去字段空格)")
    @PostMapping("/arrange/blank/join")
    public RetResult<BiComponent> arrangeBlank(@RequestBody @Validated RetRequest<ArrangeBlankDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCreate(request.getData(), ArrangeTypeEnum.BLANK));
    }

    @ApiOperation(value = "引入整理组件(分组)", notes = "引入整理组件(分组)")
    @PostMapping("/arrange/group/join")
    public RetResult<BiComponent> arrangeGroup(@RequestBody @Validated RetRequest<ArrangeGroupDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCreate(request.getData(), ArrangeTypeEnum.GROUP));
    }

    @ApiOperation(value = "引入整理组件(字段修改)", notes = "引入整理组件(字段修改)")
    @PostMapping("/arrange/modify/join")
    public RetResult<BiComponent> arrangeModify(@RequestBody @Validated RetRequest<ArrangeModifyDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCreate(request.getData(), ArrangeTypeEnum.MODIFY));
    }

    @ApiOperation(value = "修改引入整理组件(拆分)", notes = "修改引入整理组件(拆分)")
    @PostMapping("/arrange/split/update")
    public RetResult<BiComponent> arrangeSplitUpdate(@RequestBody @Validated RetRequest<UpdateArrangeSplitDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeUpdate(request.getData(), ArrangeTypeEnum.SPLIT));
    }

    @ApiOperation(value = "修改引入整理组件(移除)", notes = "修改引入整理组件(移除)")
    @PostMapping("/arrange/remove/update")
    public RetResult<BiComponent> arrangeRemoveUpdate(@RequestBody @Validated RetRequest<UpdateArrangeRemoveDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeUpdate(request.getData(), ArrangeTypeEnum.REMOVE));
    }

    @ApiOperation(value = "修改引入整理组件(替换)", notes = "修改引入整理组件(替换)")
    @PostMapping("/arrange/replace/update")
    public RetResult<BiComponent> arrangeReplaceUpdate(@RequestBody @Validated RetRequest<UpdateArrangeReplaceDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeUpdate(request.getData(), ArrangeTypeEnum.REPLACE));
    }

    @ApiOperation(value = "修改引入整理组件(合并)", notes = "修改引入整理组件(合并)")
    @PostMapping("/arrange/combine/update")
    public RetResult<BiComponent> arrangeCombineUpdate(@RequestBody @Validated RetRequest<UpdateArrangeCombineDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeUpdate(request.getData(), ArrangeTypeEnum.COMBINE));
    }

    @ApiOperation(value = "修改引入整理组件(排空)", notes = "修改引入整理组件(排空)")
    @PostMapping("/arrange/nonnull/update")
    public RetResult<BiComponent> arrangeNonNullUpdate(@RequestBody @Validated RetRequest<UpdateArrangeNonNullDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeUpdate(request.getData(), ArrangeTypeEnum.NON_NULL));
    }

    @ApiOperation(value = "修改引入整理组件(大小写转换)", notes = "修改引入整理组件(大小写转换)")
    @PostMapping("/arrange/case/update")
    public RetResult<BiComponent> arrangeCaseConvertUpdate(@RequestBody @Validated RetRequest<UpdateArrangeCaseConvertDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeUpdate(request.getData(), ArrangeTypeEnum.CONVERT_CASE));
    }

    @ApiOperation(value = "修改引入整理组件(去字段空格)", notes = "修改引入整理组件(去字段空格)")
    @PostMapping("/arrange/blank/update")
    public RetResult<BiComponent> arrangeBlankUpdate(@RequestBody @Validated RetRequest<UpdateArrangeBlankDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeUpdate(request.getData(), ArrangeTypeEnum.BLANK));
    }

    @ApiOperation(value = "修改引入整理组件(分组)", notes = "修改引入整理组件(分组)")
    @PostMapping("/arrange/group/update")
    public RetResult<BiComponent> arrangeGroupUpdate(@RequestBody @Validated RetRequest<UpdateArrangeGroupDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeUpdate(request.getData(), ArrangeTypeEnum.GROUP));
    }

    @ApiOperation(value = "修改引入整理组件(字段修改)", notes = "修改引入整理组件(字段修改)")
    @PostMapping("/arrange/modify/update")
    public RetResult<BiComponent> arrangeModifyUpdate(@RequestBody @Validated RetRequest<UpdateArrangeModifyDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeUpdate(request.getData(), ArrangeTypeEnum.MODIFY));
    }

    @ApiOperation(value = "移除组件", notes = "移除组件")
    @PostMapping("/component/remove")
    public RetResult<Void> remove(@RequestBody @Validated RetRequest<String> request) throws Exception {
        etlService.remove(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "处理组件", notes = "处理组件")
    @PostMapping("/component/handle")
    public RetResult<ComponentResp> handle(@RequestBody @Validated RetRequest<ComponentPreviewDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.handle(request.getData()));
    }

    @ApiOperation(value = "预览组件", notes = "预览组件")
    @PostMapping("/component/data/preview")
    public RetResult<ComponentPreviewResp> previewData(@RequestBody @Validated RetRequest<ComponentPreviewDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.previewData(request.getData()));
    }

    @ApiOperation(value = "预览组件空值", notes = "预览组件空值")
    @PostMapping("/component/nullData/preview")
    public RetResult<ComponentPreviewResp> previewNullData(@RequestBody @Validated RetRequest<ComponentPreviewNullDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.previewNullData(request.getData()));
    }

    @ApiOperation(value = "预览组件字段值", notes = "预览组件字段值")
    @PostMapping("/component/fieldData/preview")
    public RetResult<List> previewFieldData(@RequestBody @Validated RetRequest<ComponentPreviewFieldDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.previewFieldData(request.getData()));
    }

    @ApiOperation(value = "查看sql", notes = "查看sql")
    @PostMapping("/component/sql/preview")
    public RetResult<String> previewSql(@RequestBody @Validated RetRequest<ComponentPreviewDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.previewSql(request.getData()));
    }
}
