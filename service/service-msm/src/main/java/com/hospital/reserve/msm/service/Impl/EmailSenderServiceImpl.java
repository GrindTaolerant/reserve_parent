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
            Map<String, Object> param = msmVo.getParam();
            String toMail = msmVo.getPhone();

            return this.send(toMail, param);
        }
        return false;
    }


    //mq send
    private boolean send(String toEmail, Map<String, Object> param){
        if(StringUtils.isEmpty(toEmail)){
            return false;
        }

        String subject = (String) param.get("title");
        Integer amount = (Integer) param.get("amount");
        String reserveDate = (String) param.get("reserveDate");
        String name = (String) param.get("name");


        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("hospitalreservemailsender@gmail.com");
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setSubject(subject);


        simpleMailMessage.setText(subject + ", " + amount + ", " + reserveDate + ", " + name);


        this.mailSender.send(simpleMailMessage);
        return true;
    }

}
