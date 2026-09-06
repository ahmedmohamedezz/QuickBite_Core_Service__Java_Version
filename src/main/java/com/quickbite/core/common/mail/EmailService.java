package com.quickbite.core.common.mail;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {
//    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
//
//    private final JavaMailSender mailSender;
//
//    @Value("${app.mail.from}")
//    private final String fromEmail;
//
//    /**
//     * Sends an HTML email asynchronously to avoid blocking the caller thread.
//     */
//    @Async
//    public void sendHtmlEmail(String to, String subject, String htmlContent) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//
//            // true indicates multipart message (supports HTML + inline assets/attachments)
//            MimeMessageHelper helper = new MimeMessageHelper(
//                    message,
//                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
//                    StandardCharsets.UTF_8.name()
//            );
//
//            helper.setFrom(fromEmail);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(htmlContent, true); // true = process as HTML
//
//            mailSender.send(message);
//            log.info("Email successfully sent to {}", to);
//
//        } catch (MessagingException e) {
//            log.error("Failed to send email to {}", to, e);
//            // Handle exception or push to a dead-letter queue / fallback mechanism
//        }
//    }

    public void mockEmail(String message, String content) {
        System.out.println(message + content);
    }
}