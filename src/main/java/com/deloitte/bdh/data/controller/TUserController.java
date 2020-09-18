package com.deloitte.bdh.data.controller;


import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.model.TUser;
import com.deloitte.bdh.data.service.TUserService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/tUser")
public class TUserController {

    @Autowired
    private TUserService tUserService;

    @ApiOperation(value = "获取用户列表test", notes = "获取用户列表")
    @GetMapping("/list")
    public RetResult<List<TUser>> list() {
        PageHelper.startPage(1, 10);
        List<TUser> list = tUserService.list();
        return RetResponse.makeOKRsp(list);
    }
}
