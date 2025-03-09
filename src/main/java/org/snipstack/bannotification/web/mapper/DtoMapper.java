package org.snipstack.bannotification.web.mapper;

import org.snipstack.bannotification.model.BanNotification;
import org.snipstack.bannotification.web.dto.BanNotificationResponse;
import org.snipstack.bannotification.web.dto.UserBanResponse;

public class DtoMapper {


    public static UserBanResponse fromBanNotification(BanNotification banNotification) {
        return UserBanResponse.builder()
                .email(banNotification.getEmail())
                .username(banNotification.getUsername())
                .bannedAt(banNotification.getBannedAt())
                .build();
    }

    public static BanNotificationResponse toBanNotificationResponse(BanNotification banNotification) {
        return BanNotificationResponse.builder()
                .email(banNotification.getEmail())
                .username(banNotification.getUsername())
                .bannedAt(banNotification.getBannedAt())
                .build();
    }


}
