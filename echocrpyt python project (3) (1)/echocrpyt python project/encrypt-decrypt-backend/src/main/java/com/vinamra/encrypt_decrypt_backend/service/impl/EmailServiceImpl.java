package com.vinamra.encrypt_decrypt_backend.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.vinamra.encrypt_decrypt_backend.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEncryptedFileEmail(String toEmail, byte[] fileContent, String attachmentFileName, String password) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Your Encrypted File Backup");

            // Set HTML content
            String htmlContent = "<html>" +
                    "<body>" +
                    "<p>Dear user,</p>" +
                    "<p>Here is the encrypted file you requested as a backup.</p>" +
                    "<p><strong>Your password for this file is:</strong> <code>" + password + "</code></p>" +
                    "<p>Thank you for using our service.</p>" +
                    "<p>Sincerely,<br/>Team EchoCrypt</p>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlContent, true); // true = enable HTML

            helper.addAttachment(attachmentFileName, new org.springframework.core.io.ByteArrayResource(fileContent));

            mailSender.send(message);
            log.info("Encrypted file '{}' successfully sent to {}", attachmentFileName, toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send encrypted file '{}' to {}: {}", attachmentFileName, toEmail, e.getMessage(), e);
        }
    }
}
