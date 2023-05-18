package com.hospital.reserve.hospital.client;


import com.hospital.reserve.vo.hosp.ScheduleOrderVo;
import com.hospital.reserve.vo.order.SignInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-hospital")
@Repository

public interface HospitalFeignClient {

    @GetMapping("/api/hosp/hospital/inner/getScheduleOrderVo/{scheduleId}")
    ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId);

    @GetMapping("/api/hosp/hospital/inner/getSignInfoVo/{hoscode}")
    SignInfoVo getSignInfoVo(@PathVariable("hoscode") String hoscode);


}
