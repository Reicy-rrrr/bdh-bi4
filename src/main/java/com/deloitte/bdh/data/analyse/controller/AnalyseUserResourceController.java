package com.deloitte.bdh.data.analyse.controller;

import com.deloitte.bdh.data.analyse.service.AnalyseUserResourceService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Author:LIJUN
 * Date:08/12/2020
 * Description:
 */
@Api(value = "分析管理-文件夹")
@RestController
@RequestMapping("/ui/analyse/resource")
public class AnalyseUserResourceController {

    @Resource
    AnalyseUserResourceService userResourceService;

}
