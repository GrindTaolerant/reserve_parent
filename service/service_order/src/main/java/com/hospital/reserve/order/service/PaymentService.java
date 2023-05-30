package com.hospital.reserve.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.reserve.model.order.OrderInfo;
import com.hospital.reserve.model.order.PaymentInfo;

import java.util.Map;

public interface PaymentService extends IService<PaymentInfo> {
    void savePaymentInfo(OrderInfo order, Integer status);

    void paySuccess(String out_trade_no, Map<String, String> resultMap);

    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);
}
