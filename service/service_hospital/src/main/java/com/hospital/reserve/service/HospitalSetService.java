package com.hospital.reserve.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.reserve.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);
}
