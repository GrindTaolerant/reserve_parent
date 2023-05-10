package com.hospital.reserve.msm.service;

public interface EmailSenderService {
    void send(String toEmail, String subject, String code);
}
