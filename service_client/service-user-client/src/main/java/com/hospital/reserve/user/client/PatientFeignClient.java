package com.hospital.reserve.user.client;


import com.hospital.reserve.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-user")
@Repository
public interface PatientFeignClient {

    @GetMapping("/api/user/patient/inner/get/{id}")
    public Patient getPatient(@PathVariable("id") Long id);

}
