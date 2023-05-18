package com.hospital.reserve.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.reserve.model.user.Patient;
import com.hospital.reserve.model.user.UserInfo;

import java.util.List;

public interface PatientService extends IService<Patient> {
    List<Patient> findAllUserId(Long userId);

    Patient getPatientId(Long id);
}
