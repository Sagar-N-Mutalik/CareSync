package com.securedhealthrecords.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MailerSendService {

    private final WebClient webClient;
    private final String fromEmail;
    private final String fromName;

    public MailerSendService(
            @Value("${mailersend.api-key}") String apiKey,
            @Value("${mailersend.from-email}") String fromEmail,
            @Value("${mailersend.from-name}") String fromName) {
        
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        
        this.webClient = WebClient.builder()
                .baseUrl("https://api.mailersend.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            Map<String, Object> emailData = Map.of(
                "from", Map.of(
                    "email", fromEmail,
                    "name", fromName
                ),
                "to", List.of(Map.of(
                    "email", toEmail,
                    "name", toEmail
                )),
                "subject", "Your OTP for Secured Health Records",
                "html", buildOtpEmailHtml(otp),
                "text", "Your OTP for Secured Health Records is: " + otp + ". This OTP will expire in 10 minutes."
            );

            sendEmail(emailData);
            log.info("OTP email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void sendShareNotificationEmail(String toEmail, String shareLink, String sharedBy) {
        try {
            Map<String, Object> emailData = Map.of(
                "from", Map.of(
                    "email", fromEmail,
                    "name", fromName
                ),
                "to", List.of(Map.of(
                    "email", toEmail,
                    "name", toEmail
                )),
                "subject", "Health Records Shared with You",
                "html", buildShareEmailHtml(shareLink, sharedBy),
                "text", String.format("Health records have been shared with you by %s. Access them here: %s", sharedBy, shareLink)
            );

            sendEmail(emailData);
            log.info("Share notification email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send share notification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send share notification email", e);
        }
    }

    private void sendEmail(Map<String, Object> emailData) {
        webClient.post()
                .uri("/email")
                .bodyValue(emailData)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.debug("MailerSend API response: {}", response))
                .doOnError(error -> log.error("MailerSend API error: {}", error.getMessage()))
                .block(); // Block for synchronous behavior
    }

    private String buildOtpEmailHtml(String otp) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Your OTP - Secured Health Records</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }
                    .content { background-color: #f8f9fa; padding: 30px; border-radius: 5px; margin: 20px 0; }
                    .otp-code { font-size: 32px; font-weight: bold; color: #e74c3c; text-align: center; 
                               background-color: #fff; padding: 20px; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔒 Secured Health Records</h1>
                    </div>
                    <div class="content">
                        <h2>Your One-Time Password (OTP)</h2>
                        <p>Use this OTP to complete your authentication:</p>
                        <div class="otp-code">%s</div>
                        <p><strong>Important:</strong> This OTP will expire in 10 minutes for your security.</p>
                        <p>If you didn't request this OTP, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 Secured Health Records. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, otp);
    }

    private String buildShareEmailHtml(String shareLink, String sharedBy) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Health Records Shared - Secured Health Records</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }
                    .content { background-color: #f8f9fa; padding: 30px; border-radius: 5px; margin: 20px 0; }
                    .share-button { display: inline-block; background-color: #3498db; color: white; 
                                   padding: 15px 30px; text-decoration: none; border-radius: 5px; 
                                   margin: 20px 0; font-weight: bold; }
                    .footer { text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🏥 Secured Health Records</h1>
                    </div>
                    <div class="content">
                        <h2>Health Records Shared with You</h2>
                        <p>Hello,</p>
                        <p><strong>%s</strong> has shared health records with you through our secure platform.</p>
                        <p>Click the button below to access the shared records:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="share-button">View Shared Records</a>
                        </p>
                        <p><strong>Security Note:</strong> This link is secure and will expire after the specified time period.</p>
                        <p>If you have any questions, please contact the person who shared these records with you.</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 Secured Health Records. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, sharedBy, shareLink);
    }
}
