package com.deloitte.bdh.data.collation.controller;


import com.deloitte.bdh.common.annotation.NoInterceptor;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.collation.model.request.BiEtlDbFileUploadDto;
import com.deloitte.bdh.data.collation.model.resp.BiEtlDbFileUploadResp;
import com.deloitte.bdh.data.collation.service.BiEtlDbFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author chenghzhang
 * @since 2020-10-12
 */
@Api(tags = "数据整理-处理文件")
@RestController
@RequestMapping("/biEtlDbFile")
public class BiEtlDbFileController {
    @Autowired
    private BiEtlDbFileService biEtlDbFileService;

    @ApiOperation(value = "上传文件", notes = "上传文件")
    @PostMapping("/upload")
    @NoInterceptor
    public RetResult<BiEtlDbFileUploadResp> upload(@ModelAttribute BiEtlDbFileUploadDto fileUploadDto) {
        return RetResponse.makeOKRsp(biEtlDbFileService.upload(fileUploadDto));
    }

    @ApiOperation(value = "删除文件", notes = "删除文件")
    @PostMapping("/delete")
    public RetResult<Boolean> delete(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(biEtlDbFileService.delete(request));
    }

}
