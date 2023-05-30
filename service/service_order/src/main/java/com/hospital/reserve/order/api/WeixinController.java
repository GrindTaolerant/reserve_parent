package com.hospital.reserve.order.api;


import com.hospital.reserve.common.result.Result;
import com.hospital.reserve.order.service.PaymentService;
import com.hospital.reserve.order.service.WeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/order/weixin")
public class WeixinController {

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentService paymentService;



    @GetMapping("queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable Long orderId){
        Map<String, String> resultMap = weixinService.queryPayStatus(orderId);
        if(resultMap == null){
            return Result.fail().message("Payment Failed");
        }

        if("SUCCESS".equals(resultMap.get("trade_state"))){
            //update order status
            String out_trade_no = resultMap.get("out_trade_no");
            paymentService.paySuccess(out_trade_no, resultMap);
            return Result.ok().message("Payment Success");
        }

        return Result.ok().message("In Process");
    }

    @GetMapping("createNative/{orderId}")
    public Result createNative(@PathVariable Long orderId){
        Map map = weixinService.createNative(orderId);
        return Result.ok(map);
    }
}
