package org.snipstack.bannotification.web;

import org.snipstack.bannotification.model.BanNotification;
import org.snipstack.bannotification.service.EmailService;
import org.snipstack.bannotification.web.dto.*;
import org.snipstack.bannotification.web.mapper.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-notification")
    public ResponseEntity<UserBanResponse> sendBanNotificationEmail(@RequestBody UserBanRequest request) {
        BanNotification notification = this.emailService.sendBanNotification(request);

        UserBanResponse response = DtoMapper.fromBanNotification(notification);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/unban-notification")
    public ResponseEntity<Void> unbanBanNotification(@RequestBody UserUnbanRequest request) {
        Optional<BanNotification> byUsername = this.emailService.findByUsername(request.getUsername());
        if (byUsername.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        this.emailService.sendUnbanNotification(request);
        this.emailService.deleteById(byUsername.get().getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/banned-users")
    public List<BanNotification> getBannedUsers() {
        return emailService.getAllBannedUsers();
    }
}