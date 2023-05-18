package com.hospital.reserve.msm.service.Impl;

import com.hospital.reserve.msm.service.EmailSenderService;
import com.hospital.reserve.vo.msm.MsmVo;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    public EmailSenderServiceImpl(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Override
    public void send(String toEmail, String subject, String code) {

        if(StringUtils.isEmpty(toEmail)){
            return;
        }

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("hospitalreservemailsender@gmail.com");
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(code);

        this.mailSender.send(simpleMailMessage);


    }

    //might be problem
    @Override
    public boolean send(MsmVo msmVo) {
        if(!StringUtils.isEmpty(msmVo.getPhone())){
            String code = (String)msmVo.getParam().get("code");
            String toMail = msmVo.getPhone();
            String subject = "Order Confirmation";
            this.send(toMail, subject, code);
            return true;
        }
        return false;
    }
}
