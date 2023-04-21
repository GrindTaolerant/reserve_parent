package com.hospital.reserve.controller.api;



import com.hospital.reserve.common.exception.HospitalException;
import com.hospital.reserve.common.helper.HttpRequestHelper;
import com.hospital.reserve.common.result.Result;
import com.hospital.reserve.common.result.ResultCodeEnum;
import com.hospital.reserve.common.utils.MD5;
import com.hospital.reserve.model.hosp.Department;
import com.hospital.reserve.model.hosp.Hospital;
import com.hospital.reserve.service.DepartmentService;
import com.hospital.reserve.service.HospitalService;
import com.hospital.reserve.service.HospitalSetService;
import com.hospital.reserve.vo.hosp.DepartmentQueryVo;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
@CrossOrigin
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;


    //Delete Department
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");

        //sign check
        String hospSign = (String) paramMap.get("sign");

        String signKey = hospitalSetService.getSignKey(hoscode);

        String signKeyMd5 = MD5.encrypt(signKey);

        if(!hospSign.equals(signKeyMd5)){
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }

        departmentService.remove(hoscode, depcode);
        return Result.ok();

    }


    //find Department
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        String hoscode = (String) paramMap.get("hoscode");
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String)paramMap.get("limit"));

        //sign check
        String hospSign = (String) paramMap.get("sign");

        String signKey = hospitalSetService.getSignKey(hoscode);

        String signKeyMd5 = MD5.encrypt(signKey);

        if(!hospSign.equals(signKeyMd5)){
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        Page<Department> pageModel = departmentService.findPageDepartment(page, limit, departmentQueryVo);
        return Result.ok(pageModel);

    }


    //post department API
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        String hoscode = (String) paramMap.get("hoscode");

        String hospSign = (String) paramMap.get("sign");

        String signKey = hospitalSetService.getSignKey(hoscode);

        String signKeyMd5 = MD5.encrypt(signKey);

        if(!hospSign.equals(signKeyMd5)){
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }

        //use department service method
        departmentService.save(paramMap);

        return Result.ok();
    }


    //find hospital API
    @PostMapping("hospital/show")
    public Result getHospital(HttpServletRequest request){
        //get post hospital information
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //get hoscode
        String hoscode = (String) paramMap.get("hoscode");

        String hospSign = (String) paramMap.get("sign");

        String signKey = hospitalSetService.getSignKey(hoscode);

        String signKeyMd5 = MD5.encrypt(signKey);

        if(!hospSign.equals(signKeyMd5)){
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }

        //get service method
        Hospital hospital = hospitalService.getByHoscode(hoscode);

        return Result.ok(hospital);
    }

    //post hospital API
    @PostMapping("saveHospital")
    public Result saveHosp(HttpServletRequest request){
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        String hospSign = (String) paramMap.get("sign");

        String hoscode = (String) paramMap.get("hoscode");

        String signKey = hospitalSetService.getSignKey(hoscode);

        String signKeyMd5 = MD5.encrypt(signKey);

        if(!hospSign.equals(signKeyMd5)){
            throw new HospitalException(ResultCodeEnum.SIGN_ERROR);
        }

        String logoData = (String) paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData", logoData);

        hospitalService.save(paramMap);
        return Result.ok();
    }
}
