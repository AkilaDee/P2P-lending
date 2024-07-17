package com.p2p.p2pbackend.service;

public interface EmailService {
    void sendSimpleEmail(String toEmail, String subject, String body);
}
