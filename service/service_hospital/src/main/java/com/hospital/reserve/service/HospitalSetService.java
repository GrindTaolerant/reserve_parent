package com.hospital.reserve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.reserve.model.hosp.HospitalSet;
import com.hospital.reserve.vo.order.SignInfoVo;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);

    SignInfoVo getSignInfoVo(String hoscode);
}
