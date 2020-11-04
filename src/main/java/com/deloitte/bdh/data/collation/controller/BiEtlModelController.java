package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.model.BiEtlModel;
import com.deloitte.bdh.data.collation.model.request.CreateModelDto;
import com.deloitte.bdh.data.collation.model.request.EffectModelDto;
import com.deloitte.bdh.data.collation.model.request.GetModelPageDto;
import com.deloitte.bdh.data.collation.model.request.UpdateModelDto;
import com.deloitte.bdh.data.collation.service.BiEtlModelService;
import com.github.pagehelper.PageHelper;
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
 * @since 2020-09-24
 */
@RestController
@RequestMapping("/biEtlModel")
public class BiEtlModelController {
    @Autowired
    private BiEtlModelService biEtlModelService;

    @ApiOperation(value = "基于租户获取模型列表", notes = "基于租户获取模型列表")
    @PostMapping("/getModelPage")
    public RetResult<PageResult<List<BiEtlModel>>> getModelPage(@RequestBody @Validated RetRequest<GetModelPageDto> request) {
        PageHelper.startPage(request.getData().getPage(), request.getData().getSize());
        return RetResponse.makeOKRsp(biEtlModelService.getModelPage(request.getData()));
    }

    @ApiOperation(value = "查看单个Model详情", notes = "查看单个Model详情")
    @PostMapping("/getModel")
    public RetResult<BiEtlModel> getModel(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(biEtlModelService.getById(request.getData()));
    }

    @ApiOperation(value = "新增Model", notes = "新增Model")
    @PostMapping("/createModel")
    public RetResult<BiEtlModel> createModel(@RequestBody @Validated RetRequest<CreateModelDto> request) throws Exception {
        return RetResponse.makeOKRsp(biEtlModelService.createModel(request.getData()));
    }

    @ApiOperation(value = "改变 Model的状态", notes = "有效/无效")
    @PostMapping("/effectModel")
    public RetResult<BiEtlModel> effectModel(@RequestBody @Validated RetRequest<EffectModelDto> request) throws Exception {
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


    @ApiOperation(value = "运行/停止 model", notes = "运行/停止 Model")
    @PostMapping("/run")
    public RetResult<Void> run(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biEtlModelService.runModel(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "validate model", notes = "validate Model")
    @PostMapping("/validate")
    public RetResult<Void> validate(@RequestBody @Validated RetRequest<String> request) throws Exception {
        biEtlModelService.validate(request.getData());
        return RetResponse.makeOKRsp();
    }
}
