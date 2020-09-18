package com.deloitte.bdh.data.controller;


import com.deloitte.bdh.common.base.RetResponse;
import com.deloitte.bdh.common.base.RetResult;
import com.deloitte.bdh.data.model.FndPortalUser;
import com.deloitte.bdh.data.service.FndPortalUserService;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Ashen
 * @since 2020-08-20
 */
@RestController
@RequestMapping("/fndPortalUser")
public class FndPortalUserController {

	@Autowired
	private FndPortalUserService fndPortalUserService;

	@ApiOperation(value = "获取用户列表", notes = "获取用户列表")
	@GetMapping("/list")
	public RetResult<List<FndPortalUser>> list() {
		List<FndPortalUser> list = fndPortalUserService.list();
		return RetResponse.makeOKRsp(list);
	}
}
