package com.deloitte.bdh.data.collation.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.PageResult;
import com.deloitte.bdh.common.base.RetRequest;
import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.collation.database.po.TableColumn;
import com.deloitte.bdh.data.collation.database.po.TableData;
import com.deloitte.bdh.data.collation.enums.DownLoadTStatusEnum;
import com.deloitte.bdh.data.collation.model.BiDataSet;
import com.deloitte.bdh.data.collation.model.BiDateDownloadInfo;
import com.deloitte.bdh.data.collation.model.request.*;
import com.deloitte.bdh.data.collation.model.resp.DataSetResp;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.deloitte.bdh.data.collation.service.BiDateDownloadInfoService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lw
 * @since 2020-12-10
 */
@Api(tags = "数据整理-数据集")
@RestController
@RequestMapping("/biDataSet")
public class BiDataSetController {

    @Resource
    private BiDataSetService dataSetService;
    @Resource
    private BiDateDownloadInfoService dateDownloadInfoService;

    @ApiOperation(value = "获取数据集文件夹", notes = "获取数据集文件夹")
    @PostMapping("/getFiles")
    public RetResult<List<DataSetResp>> getFiles(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(dataSetService.getFiles(request.getData()));
    }

    @ApiOperation(value = "通过code获取数据集", notes = "通过code获取数据集")
    @PostMapping("/getDataSetByCode")
    public RetResult<List<DataSetResp>> getDataSetByCode(@RequestBody @Validated RetRequest<GetDataSetByCodeDto> request) {
        return RetResponse.makeOKRsp(dataSetService.getDataSetByCode(request.getData()));
    }

    @ApiOperation(value = "基于文件夹户获取数据集列表", notes = "基于文件夹户获取数据集列表")
    @PostMapping("/getDataSetPage")
    public RetResult<PageResult<List<DataSetResp>>> getDataSetPage(@RequestBody @Validated RetRequest<GetDataSetPageDto> request) {
        PageHelper.startPage(request.getData().getPage(), request.getData().getSize());
        return RetResponse.makeOKRsp(dataSetService.getDataSetPage(request.getData()));
    }

    @ApiOperation(value = "数据集的表重命名", notes = "数据集的表重命名")
    @PostMapping("/reName")
    public RetResult<Void> reNameDateSet(@RequestBody @Validated RetRequest<DataSetReNameDto> request) {
        dataSetService.reName(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "数据集文件夹重命名", notes = "数据集文件夹重命名")
    @PostMapping("/reNameFile")
    public RetResult<Void> reNameFile(@RequestBody @Validated RetRequest<DataSetReNameDto> request) {
        BiDataSet dataSet = dataSetService.getById(request.getData().getId());
        if (dataSet.getCreateUser().equals(BiTenantConfigController.OPERATOR)) {
            throw new RuntimeException("默认数据集请勿修改");
        }
        dataSet.setTableDesc(request.getData().getToTableDesc());
        dataSet.setTableName(request.getData().getToTableDesc());
        dataSetService.updateById(dataSet);
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "创建数据集文件夹", notes = "创建数据集文件夹")
    @PostMapping("/file/create")
    public RetResult<Void> fileCreate(@RequestBody @Validated RetRequest<CreateDataSetFileDto> request) {
        dataSetService.fileCreate(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "创建数据集", notes = "创建数据集")
    @PostMapping("/create")
    public RetResult<Void> create(@RequestBody @Validated RetRequest<CreateDataSetDto> request) {
        dataSetService.create(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "数据集结果预览（分页）", notes = "数据集结果预览（分页）")
    @PostMapping("/getDataSetInfoPage")
    public RetResult<TableData> getDataSetInfoPage(@RequestBody @Validated RetRequest<GetDataSetInfoDto> request) throws Exception {
        return RetResponse.makeOKRsp(dataSetService.getDataInfoPage(request.getData()));
    }

    @ApiOperation(value = "删除数据集或文件夹", notes = "删除数据集或文件夹")
    @PostMapping("/del")
    public RetResult<Void> delete(@RequestBody @Validated RetRequest<String> request) {
        dataSetService.delete(request.getData(), false);
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "导出", notes = "导出")
    @PostMapping("/export")
    public RetResult<Void> export(@RequestBody @Validated RetRequest<String> request) throws Exception {
        BiDataSet dataSet = dataSetService.getOne(new LambdaQueryWrapper<BiDataSet>().eq(BiDataSet::getId, request.getData()));
        if (null == dataSet) {
            return RetResponse.makeErrRsp("未找到目标对象");
        }
        int num = dateDownloadInfoService.count(new LambdaQueryWrapper<BiDateDownloadInfo>()
                .eq(BiDateDownloadInfo::getRefCode, dataSet.getCode())
                .eq(BiDateDownloadInfo::getStatus, DownLoadTStatusEnum.ING.getKey())
                .eq(BiDateDownloadInfo::getCreateUser, ThreadLocalHolder.getOperator())
        );
        if (num > 0) {
            return RetResponse.makeErrRsp("该数据正在导出种，请稍后再试");
        }
        List<TableColumn> columns = dataSetService.getColumns(dataSet.getCode());
        List<Map<String, Object>> list = dataSetService.getDataInfo(request.getData());

        BiDateDownloadInfo dateDownloadInfo = new BiDateDownloadInfo();
        dateDownloadInfo.setName(dataSet.getTableDesc());
        dateDownloadInfo.setRefCode(dataSet.getCode());
        dateDownloadInfo.setStatus(DownLoadTStatusEnum.ING.getKey());
        dateDownloadInfo.setTenantId(ThreadLocalHolder.getTenantId());
        dateDownloadInfoService.save(dateDownloadInfo);

        ThreadLocalHolder.async(() -> dateDownloadInfoService.export(dateDownloadInfo, columns, list));
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "获取下载地址", notes = "获取下载地址")
    @PostMapping("/downLoad")
    public RetResult<String> getDownLoadAddress(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(dateDownloadInfoService.downLoad(request.getData()));
    }
}
