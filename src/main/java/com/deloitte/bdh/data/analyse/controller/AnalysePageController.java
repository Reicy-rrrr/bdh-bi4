package com.deloitte.bdh.data.analyse.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.*;
import com.deloitte.bdh.common.constant.CommonConstant;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.enums.ShareTypeEnum;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageComponent;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePublicShare;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageHomepageService;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageComponentService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePublicShareService;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author:LIJUN
 * Date:12/11/2020
 * Description:
 */
@Api(tags = "分析管理-报表")
@RestController
@RequestMapping("/ui/analyse/page")
public class AnalysePageController {

    @Resource
    AnalysePageService analysePageService;

    @Resource
    private BiUiAnalysePublicShareService shareService;

    @Resource
    BiUiAnalysePageComponentService biUiAnalysePageComponentService;

    @Resource
    AnalysePageHomepageService analysePageHomepageService;

    @Resource
    private DbHandler dbHandler;

    @Resource
    private BiDataSetService dataSetService;

    @ApiOperation(value = "查询文件夹下的页面", notes = "查询文件夹下的页面")
    @PostMapping("/getChildAnalysePageList")
    public RetResult<PageResult<AnalysePageDto>> getChildAnalysePageList(@RequestBody @Validated PageRequest<GetAnalysePageListDto> request) {
        if (StringUtils.equals(request.getData().getFromDeloitte(), YesOrNoEnum.YES.getKey())) {
            ThreadLocalHolder.set("tenantCode", CommonConstant.INTERNAL_DATABASE);
        }
        return RetResponse.makeOKRsp(analysePageService.getChildAnalysePageList(request));
    }

    @ApiOperation(value = "查看单个页面详情", notes = "查看单个页面详情")
    @PostMapping("/getAnalysePage")
    public RetResult<AnalysePageDto> getAnalysePage(@RequestBody @Validated RetRequest<GetAnalysePageDto> request) {
        if (StringUtils.equals(request.getData().getFromDeloitte(), YesOrNoEnum.YES.getKey())) {
            ThreadLocalHolder.set("tenantCode", CommonConstant.INTERNAL_DATABASE);
        }
        return RetResponse.makeOKRsp(analysePageService.getAnalysePage(request.getData().getPageId()));
    }

    @ApiOperation(value = "新增页面", notes = "新增页面")
    @PostMapping("/createAnalysePage")
    public RetResult<AnalysePageDto> createAnalysePage(@RequestBody @Validated RetRequest<CreateAnalysePageDto> request) {
        return RetResponse.makeOKRsp(analysePageService.createAnalysePage(request));
    }

    @ApiOperation(value = "复制德勤方案", notes = "复制德勤方案")
    @PostMapping("/copyDeloittePage")
    public RetResult<AnalysePageDto> copyDeloittePage(@RequestBody @Validated RetRequest<CopyDeloittePageDto> request) {
        String beginTenantCode = ThreadLocalHolder.getTenantCode();
        //切换到内部库
        ThreadLocalHolder.set("tenantCode", CommonConstant.INTERNAL_DATABASE);
        CopySourceDto copySourceDto = analysePageService.getCopySourceData(request.getData().getFromPageId());
        List<String> uniqueCodeList = copySourceDto.getOriginCodeList().stream().distinct().collect(Collectors.toList());
        //创建数据集、复制表和数据
        Map<String, String> codeMap = Maps.newHashMap();
        for (String code : uniqueCodeList) {
            //切换到内部库
            ThreadLocalHolder.set("tenantCode", CommonConstant.INTERNAL_DATABASE);
            Map<String, Object> map = analysePageService.buildNewDataSet(request.getData().getDataSetName(), request.getData().getDataSetCategoryId(), code);

            //切换到当前租户库
            ThreadLocalHolder.set("tenantCode", beginTenantCode);
            analysePageService.saveNewTable(map);
            codeMap.put(code, MapUtils.getString(map, "newCode"));
        }
        //切换到当前租户库
        ThreadLocalHolder.set("tenantCode", beginTenantCode);
        AnalysePageDto dto = analysePageService.saveNewPage(request.getData().getName(), request.getData().getCategoryId(), request.getData().getFromPageId(),
                copySourceDto.getLinkPageId(), copySourceDto.getContent(), copySourceDto.getChildrenArr(), codeMap);
        return RetResponse.makeOKRsp(dto);
    }

    @ApiOperation(value = "批量删除页面", notes = "批量删除页面")
    @PostMapping("/batchDelAnalysePage")
    public RetResult<Void> batchDelAnalysePage(@RequestBody @Validated RetRequest<BatchDeleteAnalyseDto> request) {
        analysePageService.batchDelAnalysePage(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "修改页面", notes = "修改页面")
    @PostMapping("/updateAnalysePage")
    public RetResult<AnalysePageDto> updateAnalysePage(@RequestBody @Validated RetRequest<UpdateAnalysePageDto> request) {
        return RetResponse.makeOKRsp(analysePageService.updateAnalysePage(request));
    }

    @ApiOperation(value = "发布页面", notes = "发布页面")
    @PostMapping("/publishAnalysePage")
    public RetResult<AnalysePageConfigDto> publishAnalysePageConfig(@RequestBody @Validated RetRequest<PublishAnalysePageDto> request) {
        return RetResponse.makeOKRsp(analysePageService.publishAnalysePage(request.getData()));
    }

    @ApiOperation(value = "查询草稿", notes = "查询草稿")
    @PostMapping("/getAnalysePageDrafts")
    public RetResult<PageResult<AnalysePageDto>> getAnalysePageDrafts(@RequestBody @Validated PageRequest<AnalyseNameDto> request) {
        return RetResponse.makeOKRsp(analysePageService.getAnalysePageDrafts(request));
    }

    @ApiOperation(value = "设置主页", notes = "设置主页")
    @PostMapping("/setHomePage")
    public RetResult<Void> setHomePage(@RequestBody @Validated RetRequest<String> request) {
        analysePageHomepageService.setHomePage(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "获取主页的ID", notes = "获取主页的ID")
    @PostMapping("/getHomePageId")
    public RetResult<String> getHomePageId(@RequestBody @Validated RetRequest<Void> request) {
        return RetResponse.makeOKRsp(analysePageHomepageService.getHomePageId());
    }

    @ApiOperation(value = "保存图形指标", notes = "保存图形指标")
    @PostMapping("/saveChartComponent")
    public RetResult<Boolean> saveChartComponent(@RequestBody @Validated RetRequest<pageComponentDto> request) {

        return RetResponse.makeOKRsp(biUiAnalysePageComponentService.saveChartComponent(request.getData()));
    }

    @ApiOperation(value = "删除图形指标", notes = "删除图形指标")
    @PostMapping("/delChartComponent")
    public RetResult<Boolean> delChartComponent(@RequestBody @Validated RetRequest<pageComponentDto> request) {

        return RetResponse.makeOKRsp(biUiAnalysePageComponentService.delChartComponent(request.getData()));
    }

    @ApiOperation(value = "获取图形指标列表", notes = "获取图形指标列表")
    @PostMapping("/getChartComponent")
    public RetResult<PageResult<BiUiAnalysePageComponent>> getChartComponent(@RequestBody @Validated PageRequest<pageComponentDto> request) {

        PageHelper.startPage(request.getPage(),request.getSize());
        List<BiUiAnalysePageComponent> list = biUiAnalysePageComponentService.getChartComponent(request.getData());
        PageInfo<BiUiAnalysePageComponent> info = new PageInfo(list);
        PageResult<BiUiAnalysePageComponent> result = new PageResult(info);
        result.setMore(info.isHasNextPage());
        result.setTotal(info.getTotal());
        return RetResponse.makeOKRsp(result);
    }


    @ApiOperation(value = "获取非公开报表的链接", notes = "获取非公开报表的链接")
    @PostMapping("/getUrl")
    public RetResult<BiUiAnalysePublicShare> getUrl(@RequestBody @Validated RetRequest<GetShareUrlDto> request) {
        LambdaQueryWrapper<BiUiAnalysePublicShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BiUiAnalysePublicShare::getRefPageId, request.getData().getPageId());
        List<String> typeList = Lists.newArrayList(ShareTypeEnum.ZERO.getKey(), ShareTypeEnum.ONE.getKey(), ShareTypeEnum.TWO.getKey());
        lambdaQueryWrapper.in(BiUiAnalysePublicShare::getType, typeList);
        BiUiAnalysePublicShare share = shareService.getOne(lambdaQueryWrapper);
        return RetResponse.makeOKRsp(share);
    }

    @ApiOperation(value = "替换数据集", notes = "替换数据集")
    @PostMapping("/replace")
    public RetResult<Void> replaceDateSet(@RequestBody @Validated RetRequest<ReplaceDataSetDto> request) throws Exception {
        analysePageService.replaceDataSet(request.getData());
        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "获取报表使用的表", notes = "获取报表使用的表")
    @PostMapping("/getUsedTableName")
    public RetResult<List<String>> getUsedTableName(@RequestBody @Validated RetRequest<String> request) {
        return RetResponse.makeOKRsp(analysePageService.getUsedTableName(request.getData()));
    }

}
