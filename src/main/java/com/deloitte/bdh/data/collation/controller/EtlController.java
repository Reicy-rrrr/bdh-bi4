package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.integration.EtlService;
import com.deloitte.bdh.data.collation.model.BiComponent;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.ComponentPreviewResp;
import com.deloitte.bdh.data.collation.model.resp.ComponentResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-09-25
 */
@RestController
@RequestMapping("/bi/etl")
public class EtlController {
    @Autowired
    private EtlService etlService;


    @ApiOperation(value = "引入数据源组件", notes = "引入数据源组件")
    @PostMapping("/resource/join")
    public RetResult<BiComponent> resource(@RequestBody @Validated RetRequest<ResourceComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.resource(request.getData()));
    }

    @ApiOperation(value = "引入输出组件", notes = "引入输出组件")
    @PostMapping("/out/join")
    public RetResult<BiComponent> out(@RequestBody @Validated RetRequest<OutComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.out(request.getData()));
    }

    @ApiOperation(value = "引入关联组件", notes = "引入关联组件")
    @PostMapping("/join/join")
    public RetResult<BiComponent> join(@RequestBody @Validated RetRequest<JoinComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.join(request.getData()));
    }

    @ApiOperation(value = "引入聚合组件", notes = "引入聚合组件")
    @PostMapping("/group/join")
    public RetResult<BiComponent> group(@RequestBody @Validated RetRequest<GroupComponentDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.group(request.getData()));
    }

//    @ApiOperation(value = "引入整理组件", notes = "引入整理组件")
//    @PostMapping("/arrange/join")
//    public RetResult<BiComponent> arrange(@RequestBody @Validated RetRequest<ArrangeComponentDto> request) throws Exception {
//        return RetResponse.makeOKRsp(etlService.arrange(request.getData()));
//    }

    @ApiOperation(value = "引入整理组件(拆分)", notes = "引入整理组件(拆分)")
    @PostMapping("/arrange/split/join")
    public RetResult<BiComponent> arrangeSplit(@RequestBody @Validated RetRequest<ArrangeSplitDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeSplit(request.getData()));
    }

    @ApiOperation(value = "引入整理组件(移除)", notes = "引入整理组件(移除)")
    @PostMapping("/arrange/remove/join")
    public RetResult<BiComponent> arrangeRemove(@RequestBody @Validated RetRequest<ArrangeRemoveDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeRemove(request.getData()));
    }

    @ApiOperation(value = "引入整理组件(替换)", notes = "引入整理组件(替换)")
    @PostMapping("/arrange/replace/join")
    public RetResult<BiComponent> arrangeReplace(@RequestBody @Validated RetRequest<ArrangeReplaceDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeReplace(request.getData()));
    }

    @ApiOperation(value = "引入整理组件(合并)", notes = "引入整理组件(合并)")
    @PostMapping("/arrange/combine/join")
    public RetResult<BiComponent> arrangeCombine(@RequestBody @Validated RetRequest<ArrangeCombineDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCombine(request.getData()));
    }

    @ApiOperation(value = "引入整理组件(排空)", notes = "引入整理组件(排空)")
    @PostMapping("/arrange/nonnull/join")
    public RetResult<BiComponent> arrangeNonNull(@RequestBody @Validated RetRequest<ArrangeNonNullDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeNonNull(request.getData()));
    }


    @ApiOperation(value = "引入整理组件(大小写转换)", notes = "引入整理组件(大小写转换)")
    @PostMapping("/arrange/case/join")
    public RetResult<BiComponent> arrangeCaseConvert(@RequestBody @Validated RetRequest<ArrangeCaseConvertDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeCaseConvert(request.getData()));
    }

//    @ApiOperation(value = "引入整理组件(去前后空格)", notes = "引入整理组件(去前后空格)")
//    @PostMapping("/arrange/trim/join")
//    public RetResult<BiComponent> arrangeTrim(@RequestBody @Validated RetRequest<ArrangeTrimDto> request) throws Exception {
//        return RetResponse.makeOKRsp(etlService.arrangeTrim(request.getData()));
//    }

    @ApiOperation(value = "引入整理组件(去字段空格)", notes = "引入整理组件(去字段空格)")
    @PostMapping("/arrange/blank/join")
    public RetResult<BiComponent> arrangeBlank(@RequestBody @Validated RetRequest<ArrangeBlankDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeBlank(request.getData()));
    }

    @ApiOperation(value = "引入整理组件(分组)", notes = "引入整理组件(分组)")
    @PostMapping("/arrange/group/join")
    public RetResult<BiComponent> arrangeGroup(@RequestBody @Validated RetRequest<ArrangeGroupDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.arrangeGroup(request.getData()));
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

    @ApiOperation(value = "查看sql", notes = "查看sql")
    @PostMapping("/component/sql/preview")
    public RetResult<String> previewSql(@RequestBody @Validated RetRequest<ComponentPreviewDto> request) throws Exception {
        return RetResponse.makeOKRsp(etlService.previewSql(request.getData()));
    }
}
