package com.hospital.reserve.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.reserve.model.order.OrderInfo;

public interface OrderService extends IService<OrderInfo> {
    Long saveOrder(String scheduleId, Long patientId);
}
