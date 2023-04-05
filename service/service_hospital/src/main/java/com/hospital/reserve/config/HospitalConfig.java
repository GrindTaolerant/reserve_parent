package com.hospital.reserve.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.hospital.reserve.mapper")
public class HospitalConfig {
}
