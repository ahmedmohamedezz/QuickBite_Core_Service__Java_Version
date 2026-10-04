package com.quickbite.core.pkg.mail;

import com.quickbite.core.pkg.mail.dto.MailMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MailInterface {
    void send(MailMessage message);

    CompletableFuture<Void> sendAsync(MailMessage message);

    void sendBulk(List<MailMessage> messages);
}
