package com.quickbite.core.pkg.mail.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmailAttachment(
        String filename,
        byte[] content,
        String contentType
) {}
