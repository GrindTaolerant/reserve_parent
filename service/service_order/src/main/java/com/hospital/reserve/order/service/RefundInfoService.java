package com.hospital.reserve.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.reserve.model.order.PaymentInfo;
import com.hospital.reserve.model.order.RefundInfo;

public interface RefundInfoService extends IService<RefundInfo> {
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
