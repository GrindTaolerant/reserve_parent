package com.hospital.reserve.controller.api;


import com.hospital.reserve.common.result.Result;
import com.hospital.reserve.model.hosp.Hospital;
import com.hospital.reserve.service.DepartmentService;
import com.hospital.reserve.service.HospitalService;
import com.hospital.reserve.vo.hosp.DepartmentVo;
import com.hospital.reserve.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;


    @ApiOperation(value = "get hospital list")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        List<Hospital> content = pageModel.getContent();
        int totalPages = pageModel.getTotalPages();
        return Result.ok(pageModel);
    }


    @ApiOperation(value = "get hospital by name")
    @GetMapping("findByHosName/{hosname}")
    public Result findByHosName(@PathVariable String hosname){
        List<Hospital> list = hospitalService.findByHosname(hosname);

        return Result.ok(list);
    }

    //get Department of Hospital
    @ApiOperation(value = "get departments by hoscode")
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }


    @ApiOperation(value = "get appointment information")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(@PathVariable String hoscode){
        Map<String, Object> map = hospitalService.item(hoscode);
        return Result.ok(map);
    }

}
