package org.snipstack.bannotification.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BanNotificationRequest {

    private UUID userId;
}
