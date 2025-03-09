package org.snipstack.bannotification.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserBanResponse {

    private String email;

    private String username;

    private LocalDateTime bannedAt;

}
