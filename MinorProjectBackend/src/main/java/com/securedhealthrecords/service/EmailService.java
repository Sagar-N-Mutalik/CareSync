package com.securedhealthrecords.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Secured Health Records - OTP Verification");
            message.setText(buildOtpEmailBody(otp));
            
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send OTP email");
        }
    }
    
    public void sendShareNotificationEmail(String toEmail, String shareLink, String senderName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Secured Health Records - Files Shared with You");
            message.setText(buildShareEmailBody(shareLink, senderName));
            
            mailSender.send(message);
            log.info("Share notification email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send share notification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send share notification email");
        }
    }
    
    private String buildOtpEmailBody(String otp) {
        return String.format(
            "Dear User,\n\n" +
            "Your OTP for Secured Health Records verification is: %s\n\n" +
            "This OTP is valid for 10 minutes. Please do not share this code with anyone.\n\n" +
            "If you did not request this OTP, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Secured Health Records Team",
            otp
        );
    }
    
    private String buildShareEmailBody(String shareLink, String senderName) {
        return String.format(
            "Dear User,\n\n" +
            "%s has shared some health records with you through Secured Health Records.\n\n" +
            "You can access the shared files using the following secure link:\n" +
            "%s\n\n" +
            "This link will expire after the specified duration. No registration is required to view the files.\n\n" +
            "Best regards,\n" +
            "Secured Health Records Team",
            senderName, shareLink
        );
    }
}
