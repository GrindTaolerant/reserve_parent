package com.hospital.reserve.user.service;

import com.hospital.reserve.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService {
    Map<String, Object> loginUser(LoginVo loginVo);
}
