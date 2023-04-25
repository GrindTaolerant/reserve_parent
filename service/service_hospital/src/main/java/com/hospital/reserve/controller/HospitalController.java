package com.hospital.reserve.controller;


import com.hospital.reserve.common.result.Result;
import com.hospital.reserve.model.hosp.Hospital;
import com.hospital.reserve.service.HospitalService;
import com.hospital.reserve.vo.hosp.HospitalQueryVo;
import com.hospital.reserve.vo.hosp.HospitalSetQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    //hospital list
    @GetMapping("/list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel =  hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(pageModel);
    }


}
