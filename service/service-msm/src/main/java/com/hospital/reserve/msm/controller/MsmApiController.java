package com.hospital.reserve.msm.controller;

import com.hospital.reserve.common.result.Result;
import com.hospital.reserve.msm.service.EmailSenderService;
import com.hospital.reserve.msm.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
public class MsmApiController {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("send/{email}")
    public Result sendEmail(@PathVariable String email){

        //key - phone number, value - code
        String code = redisTemplate.opsForValue().get(email);
        if(!StringUtils.isEmpty(code)){
            return Result.ok(code);
        }

        //if not in redis
        //generate code and
        code = RandomUtil.getSixBitRandom();
        String subject = "Auto Email Code Message";

        emailSenderService.send(email, subject, code);

//        if(isSend){
        redisTemplate.opsForValue().setIfAbsent(email, code, 2, TimeUnit.MINUTES);

//        }else{
//            return Result.fail().message("failed to send message");
//        }

        return Result.ok();

    }

}
