package com.hospital.reserve.msm.service;

import com.hospital.reserve.vo.msm.MsmVo;

public interface EmailSenderService {
    void send(String toEmail, String subject, String code);

    //mq message send
    boolean send(MsmVo msmVo);
}
