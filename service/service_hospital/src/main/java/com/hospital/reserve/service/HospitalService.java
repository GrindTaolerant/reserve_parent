package com.hospital.reserve.service;

import com.hospital.reserve.model.hosp.Hospital;
import com.hospital.reserve.vo.hosp.HospitalQueryVo;
import com.hospital.reserve.vo.hosp.HospitalSetQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> paramMap);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);
}
