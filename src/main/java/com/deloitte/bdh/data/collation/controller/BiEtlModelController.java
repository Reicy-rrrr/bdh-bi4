package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.cron.CronUtil;
import com.deloitte.bdh.common.util.StringUtil;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.request.CreateModelDto;
import com.deloitte.bdh.data.collation.model.request.EffectModelDto;
import com.deloitte.bdh.data.collation.model.request.GetModelPageDto;
import com.deloitte.bdh.data.collation.model.request.UpdateModelContent;
import com.deloitte.bdh.data.collation.model.request.UpdateModelDto;
import com.deloitte.bdh.data.collation.model.resp.ModelResp;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
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
 * @since 2020-09-24
 */
@Api(tags = "数据整理-模型")
@RestController
@RequestMapping("/biEtlModel")
public class BiEtlModelController {
    @Autowired
    private BiEtlModelService biEtlModelService;

    @ApiOperation(value = "基于租户获取模型文件夹树", notes = "基于租户获取模型文件夹树")
    @PostMapping("/getModelTree")
    public RetResult<List<BiEtlModel>> getModelTree(@RequestBody @Validated RetRequest request) {
        return RetResponse.makeOKRsp(biEtlModelService.getModelTree());
    }

    @ApiOperation(value = "基于文件夹户获取模型列表", notes = "基于文件夹户获取模型列表")
    @PostMapping("/getModelPage")
    public RetResult<PageResult<List<ModelResp>>> getModelPage(@RequestBody @Validated RetRequest<GetModelPageDto> request) {
        PageHelper.startPage(request.getData().getPage(), request.getData().getSize());
        return RetResponse.makeOKRsp(biEtlModelService.getModelPage(request.getData()));
    }

    @ApiOperation(value = "查看单个Model详情", notes = "查看单个Model详情")
    @PostMapping("/getModel")
    public RetResult<ModelResp> getModel(@RequestBody @Validated RetRequest<String> request) {
        BiEtlModel model = biEtlModelService.getById(request.getData());
        ModelResp resp = new ModelResp();
        if (null != model) {
            BeanUtils.copyProperties(model, resp);
            if (!StringUtil.isEmpty(model.getCronData())) {
                resp.setCronDesc(CronUtil.createDescription(model.getCronData()));
            }
        }
        return RetResponse.makeOKRsp(resp);
    }

    @ApiOperation(value = "新增Model", notes = "新增Model")
    @PostMapping("/createModel")
    public RetResult<BiEtlModel> createModel(@RequestBody @Validated RetRequest<CreateModelDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlModelService.createModel(request.getData()));
    }

    @ApiOperation(value = "启用、停用 Model的状态", notes = "有效/无效")
    @PostMapping("/effectModel")
    public RetResult<BiEtlModel> effectModel(@RequestBody @Validated RetRequest<EffectModelDto> request) {
        return RetResponse.makeOKRsp(biEtlModelService.effectModel(request.getData()));
    }

    @ApiOperation(value = "删除 Model", notes = "删除 Model")
    @PostMapping("/delModel")
    public RetResult<Void> delModel(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biEtlModelService.delModel(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改 Model", notes = "修改 Model")
    @PostMapping("/updateModel")
    public RetResult<BiEtlModel> updateModel(@RequestBody @Validated RetRequest<UpdateModelDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlModelService.updateModel(request.getData()));
    }

    @ApiOperation(value = "修改大字段", notes = "修改大字段")
    @PostMapping("/updateContent")
    public RetResult<BiEtlModel> updateContent(@RequestBody @Validated RetRequest<UpdateModelContent> request) {
        BiEtlModel model = biEtlModelService.getById(request.getData().getId());
        model.setContent(request.getData().getContent());
        biEtlModelService.updateById(model);
        return RetResponse.makeOKRsp(model);
    }

    @ApiOperation(value = "运行/停止 model", notes = "运行/停止 Model")
    @PostMapping("/run")
    public RetResult<BiEtlModel> run(@RequestBody @Validated RetRequest<String> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlModelService.runModel(request.getData()));
    }

    @ApiOperation(value = "validate model", notes = "validate Model")
    @PostMapping("/runValidate")
    public RetResult<Void> runValidate(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biEtlModelService.runValidate(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "触发执行 model", notes = "马上执行一次")
    @PostMapping("/trigger")
    public RetResult<Void> trigger(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biEtlModelService.trigger(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "解析cron", notes = "解析cron")
    @PostMapping("/parseCron")
    public RetResult<String> parseCron(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(CronUtil.createCronExpression(request.getData()));
    }

}
