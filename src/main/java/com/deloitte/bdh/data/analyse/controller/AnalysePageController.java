package com.deloitte.bdh.data.analyse.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deloitte.bdh.common.base.*;
import com.deloitte.bdh.common.client.FeignClientService;
import com.deloitte.bdh.common.client.dto.TenantBasicVo;
import com.deloitte.bdh.common.properties.BiProperties;
import com.deloitte.bdh.common.util.ThreadLocalHolder;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePage;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePageComponent;
import com.deloitte.bdh.data.analyse.model.BiUiAnalysePublicShare;
import com.deloitte.bdh.data.analyse.model.request.*;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageConfigDto;
import com.deloitte.bdh.data.analyse.model.resp.AnalysePageDto;
import com.deloitte.bdh.data.analyse.service.AnalysePageHomepageService;
import com.deloitte.bdh.data.analyse.service.AnalysePageService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePageComponentService;
import com.deloitte.bdh.data.analyse.service.BiUiAnalysePublicShareService;
import com.deloitte.bdh.data.analyse.service.IssueService;
import com.deloitte.bdh.data.collation.database.DbHandler;
import com.deloitte.bdh.data.collation.enums.YesOrNoEnum;
import com.deloitte.bdh.data.collation.service.BiDataSetService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    @Resource
    private BiProperties biProperties;

    @Resource
    private IssueService issueService;

    @Resource
    private FeignClientService feignClientService;

    @ApiOperation(value = "查询文件夹下的页面", notes = "查询文件夹下的页面")
    @PostMapping("/getChildAnalysePageList")
    public RetResult<PageResult<AnalysePageDto>> getChildAnalysePageList(@RequestBody @Validated PageRequest<GetAnalysePageListDto> request) {
        if (StringUtils.equals(request.getData().getFromDeloitte(), YesOrNoEnum.YES.getKey())) {
            ThreadLocalHolder.set("tenantCode", biProperties.getInnerTenantCode());
        }
        return RetResponse.makeOKRsp(analysePageService.getChildAnalysePageList(request));
    }

    @ApiOperation(value = "查看单个页面详情", notes = "查看单个页面详情")
    @PostMapping("/getAnalysePage")
    public RetResult<AnalysePageDto> getAnalysePage(@RequestBody @Validated RetRequest<GetAnalysePageDto> request) {
        if (StringUtils.equals(request.getData().getFromDeloitte(), YesOrNoEnum.YES.getKey())) {
            ThreadLocalHolder.set("tenantCode", biProperties.getInnerTenantCode());
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
    public RetResult<Map<String, String>> copyDeloittePage(@RequestBody @Validated RetRequest<CopyDeloittePageDto> request) {
        return RetResponse.makeOKRsp(issueService.copyDeloittePage(request.getData()));
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

        PageHelper.startPage(request.getPage(), request.getSize());
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
        if (StringUtils.equals(request.getData().getFromDeloitte(), YesOrNoEnum.YES.getKey())) {
            ThreadLocalHolder.set("tenantCode", biProperties.getInnerTenantCode());
        }
//        LambdaQueryWrapper<BiUiAnalysePublicShare> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(BiUiAnalysePublicShare::getRefPageId, request.getData().getPageId());
//        List<String> typeList = Lists.newArrayList(ShareTypeEnum.ZERO.getKey(), ShareTypeEnum.ONE.getKey(), ShareTypeEnum.TWO.getKey());
//        lambdaQueryWrapper.in(BiUiAnalysePublicShare::getType, typeList);
        BiUiAnalysePublicShare share = shareService.get(request.getData().getPageId());
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


    @ApiOperation(value = "分发德勤方案", notes = "分发德勤方案")
    @PostMapping("/issueDeloittePage")
    public RetResult<Map<String, String>> issueDeloittePage(@RequestBody @Validated RetRequest<IssueDeloitteDto> request) {
        return RetResponse.makeOKRsp(issueService.issueDeloittePage(request.getData()));
    }

    @ApiOperation(value = "获取租户列表", notes = "获取租户列表")
    @PostMapping("/getTenantCodes")
    public RetResult<List<TenantBasicVo>> getTenantCodes(@RequestBody @Validated RetRequest<String> request) {
        List<TenantBasicVo> list = feignClientService.queryTenantList(request.getData());
        if (CollectionUtils.isNotEmpty(list)) {
            list.removeIf(tenantBasicVo -> null != tenantBasicVo.getTenantCode() && tenantBasicVo.getTenantCode().equals(biProperties.getInnerTenantCode()));
        }
        return RetResponse.makeOKRsp(list);
    }

    @ApiOperation(value = "德勤方案下获取该层级下的报表集合", notes = "德勤方案下获取报表的层级下的报表")
    @PostMapping("/getPageListForDeloitte")
    public RetResult<List<AnalysePageDto>> getPageListForDeloitte(@RequestBody @Validated RetRequest<String> request) {
        String pageId = request.getData();
        boolean parentId = true;
        do {
            BiUiAnalysePage page = analysePageService.getOne(new LambdaQueryWrapper<BiUiAnalysePage>().eq(BiUiAnalysePage::getParentId, pageId));
            if (null == page) {
                parentId = false;
            } else {
                pageId = page.getId();
            }
        } while (parentId);
        List<AnalysePageDto> list = analysePageService.getPageWithChildren(pageId);
        list.removeIf(var -> request.getData().contains(var.getId()));
        return RetResponse.makeOKRsp(list);
    }
}
