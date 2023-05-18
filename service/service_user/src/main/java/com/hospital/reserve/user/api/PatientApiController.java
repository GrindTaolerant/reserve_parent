package com.hospital.reserve.user.api;


import com.hospital.reserve.common.result.Result;
import com.hospital.reserve.common.utils.AuthContextHolder;
import com.hospital.reserve.model.user.Patient;
import com.hospital.reserve.user.service.PatientService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {


    @Autowired
    private PatientService patientService;

    //get patient list
    @GetMapping("auth/findAll")
    public Result findAll(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> list = patientService.findAllUserId(userId);
        return Result.ok(list);
    }

    //add patient
    @PostMapping("auth/save")
    public Result savePatient(@RequestBody Patient patient, HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    //get patient with id
    @GetMapping("auth/get/{id}")
    public Result getPatient(@PathVariable Long id){
        Patient patient = patientService.getPatientId(id);
        return Result.ok(patient);
    }

    //update patient
    @PostMapping("auth/update")
    public Result updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return Result.ok();
    }

    //delete patient
    @DeleteMapping("auth/remove/{id}")
    public Result removePatient(@PathVariable Long id) {
        patientService.removeById(id);
        return Result.ok();
    }

    //get patient info with id
    @ApiOperation(value = "get patient")
    @GetMapping("inner/get/{id}")
    public Patient getPatientOrder(@PathVariable Long id){
        Patient patient = patientService.getPatientId(id);
        return patient;
    }




}
