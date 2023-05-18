package com.hospital.reserve.order.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.hospital.reserve.order.mapper")
public class OrderConfig {
}
