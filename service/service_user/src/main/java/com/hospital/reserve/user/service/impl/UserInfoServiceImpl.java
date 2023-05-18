package com.hospital.reserve.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hospital.reserve.common.helper.JwtHelper;
import com.hospital.reserve.common.result.ResultCodeEnum;
import com.hospital.reserve.common.result.YyghException;
import com.hospital.reserve.enums.AuthStatusEnum;
import com.hospital.reserve.model.acl.User;
import com.hospital.reserve.model.user.Patient;
import com.hospital.reserve.model.user.UserInfo;
import com.hospital.reserve.user.mapper.UserInfoMapper;
import com.hospital.reserve.user.service.PatientService;
import com.hospital.reserve.user.service.UserInfoService;
import com.hospital.reserve.vo.user.LoginVo;
import com.hospital.reserve.vo.user.UserAuthVo;
import com.hospital.reserve.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PatientService patientService;



    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        //get phone and code from loginVo
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        //check if null
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //check if code match : TODO
        String redisCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(redisCode)){
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }


        //绑定手机号码
        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.selectWxInfoOpenId(loginVo.getOpenid());
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }

        //if userInfo is null, then use phone login
        if(userInfo == null){
            //check if first login: check in database
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            userInfo = baseMapper.selectOne(queryWrapper);

            if(userInfo == null){
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }


        //check if not allowed
        if(userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //if not, login
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);

        //jwt generate tokens
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }

    @Override
    public UserInfo selectWxInfoOpenId(String openId) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openId);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);


        return userInfo;
    }

    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        UserInfo userInfo = baseMapper.selectById(userId);

        //set auth data
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());

        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());

        baseMapper.updateById(userInfo);
    }

    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        String name = userInfoQueryVo.getKeyword();
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();

        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }

        IPage<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);

        pages.getRecords().stream().forEach(item -> {
            this.packageUserInfo(item);
        });


        return pages;
    }

    @Override
    public void lock(Long userId, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus((status));
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String,Object> map = new HashMap<>();
        //根据userid查询用户信息
        UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
        map.put("userInfo",userInfo);
        //根据userid查询就诊人信息
        List<Patient> patientList = patientService.findAllUserId(userId);
        map.put("patientList",patientList);

        return map;

    }

    @Override
    public void approval(Long userId, Integer authStatus) {
        if(authStatus.intValue()==2 || authStatus.intValue()==-1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    private UserInfo packageUserInfo(UserInfo userInfo) {

        //get auth code
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));

        //check user status
        String statusString = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);

        return userInfo;

    }


}
