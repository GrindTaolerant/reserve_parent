package com.hospital.reserve.user.controller;


import com.hospital.reserve.common.result.Result;
import com.hospital.reserve.common.utils.AuthContextHolder;
import com.hospital.reserve.model.user.UserInfo;
import com.hospital.reserve.user.service.UserInfoService;
import com.hospital.reserve.vo.user.LoginVo;
import com.hospital.reserve.vo.user.UserAuthVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "user login")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String, Object> info = userInfoService.loginUser(loginVo);
        return Result.ok(info);
    }


    //user auto api
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){

        userInfoService.userAuth(AuthContextHolder.getUserId(request), userAuthVo);
        return Result.ok();
    }

    //get user id info
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }


}
