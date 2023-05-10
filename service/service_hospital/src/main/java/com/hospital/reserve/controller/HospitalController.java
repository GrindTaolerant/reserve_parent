package com.hospital.reserve.controller;


import com.hospital.reserve.common.result.Result;
import com.hospital.reserve.model.hosp.Hospital;
import com.hospital.reserve.service.HospitalService;
import com.hospital.reserve.vo.hosp.HospitalQueryVo;
import com.hospital.reserve.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    //hospital list
    @GetMapping("/list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel =  hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        List<Hospital> content = pageModel.getContent();
        long totalElements = pageModel.getTotalElements();
        return Result.ok(pageModel);
    }

    @GetMapping("updateHospStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id,
                                   @PathVariable Integer status){
        hospitalService.updateStatus(id, status);
        return Result.ok();
    }

    //hospital Detail information
    @ApiOperation(value = "Hospital Detail Information")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){

        Map<String, Object> map = hospitalService.getHospById(id);
        return Result.ok(map);
    }


}
