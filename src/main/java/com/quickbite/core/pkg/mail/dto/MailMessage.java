package com.quickbite.core.pkg.mail.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MailMessage(
        String to,
        List<String> cc,
        List<String> bcc,
        String subject,
        String body,
        boolean isHtml,
        List<EmailAttachment> attachments,
        Map<String, String> metadata // Provider-specific headers/tags
) {
    // Builder/Convenience constructor for simple emails
    public static MailMessage simple(String to, String subject, String body) {
        return new MailMessage(to, List.of(), List.of(), subject, body, false, List.of(), Map.of());
    }

    public static MailMessage html(String to, String subject, String htmlBody) {
        return new MailMessage(to, List.of(), List.of(), subject, htmlBody, true, List.of(), Map.of());
    }
}