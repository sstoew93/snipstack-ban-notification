package org.snipstack.bannotification.web;

import org.snipstack.bannotification.model.BanNotification;
import org.snipstack.bannotification.service.EmailService;
import org.snipstack.bannotification.web.dto.BanNotificationRequest;
import org.snipstack.bannotification.web.dto.BanNotificationResponse;
import org.snipstack.bannotification.web.dto.UserBanResponse;
import org.snipstack.bannotification.web.dto.UserBanRequest;
import org.snipstack.bannotification.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/notification")
    public ResponseEntity<BanNotificationResponse> getBanNotification(@RequestBody BanNotificationRequest banNotificationRequest) {
        Optional<BanNotification> notification = emailService.findByUserId(banNotificationRequest.getUserId());

        if (notification.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        BanNotificationResponse banNotificationResponse = DtoMapper.toBanNotificationResponse(notification.get());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(banNotificationResponse);
    }


    @PostMapping("/send-notification")
    public ResponseEntity<UserBanResponse> sendBanNotificationEmail(@RequestBody UserBanRequest request) {

        BanNotification notification = this.emailService.sendNotification(request);

        UserBanResponse response = DtoMapper.fromBanNotification(notification);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}