package org.snipstack.bannotification.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUnbanRequest {

    private String username;

}
