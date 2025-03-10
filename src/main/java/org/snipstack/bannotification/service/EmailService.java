package org.snipstack.bannotification.service;

import org.snipstack.bannotification.model.BanNotification;
import org.snipstack.bannotification.repository.BanNotificationRepository;
import org.snipstack.bannotification.web.dto.UserBanRequest;
import org.snipstack.bannotification.web.dto.UserUnbanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailService {

    private final BanNotificationRepository banNotificationRepository;
    private final MailSender mailSender;

    @Autowired
    public EmailService(BanNotificationRepository banNotificationRepository, MailSender mailSender) {
        this.banNotificationRepository = banNotificationRepository;
        this.mailSender = mailSender;
    }

    public BanNotification sendBanNotification(UserBanRequest details) {
        Optional<BanNotification> optional = this.banNotificationRepository.findByUsername(details.getUsername());

        if (optional.isPresent()) {
            throw new IllegalArgumentException("User already banned!");
        }

        SimpleMailMessage message = buildEmailMessage(
                details.getEmail(),
                "SnipStack Notification: Your account has been suspended!",
                "Dear " + details.getUsername() + ",\n\n" +
                        "We regret to inform you that your account has been suspended from our platform.\n\n" +
                        "If you believe this was a mistake or wish to appeal, please contact us at support@snipstack.com.\n\n" +
                        "Best regards,\nSnipStack Community Admin Team"
        );

        this.mailSender.send(message);

        BanNotification banNotification = getBanNotification(details);
        return this.banNotificationRepository.save(banNotification);
    }

    public void sendUnbanNotification(UserUnbanRequest details) {
        Optional<BanNotification> optional = this.banNotificationRepository.findByUsername(details.getUsername());

        if (optional.isEmpty()) {
            throw new IllegalArgumentException("User is not banned!");
        }

        String email = optional.get().getEmail();
        SimpleMailMessage message = buildEmailMessage(
                email,
                "SnipStack Notification: Your account has been reinstated!",
                "Dear " + details.getUsername() + ",\n\n" +
                        "We are pleased to inform you that your account has been reinstated on our platform.\n\n" +
                        "You can now log in and continue using our services.\n\n" +
                        "Best regards,\nSnipStack Community Admin Team"
        );

        this.mailSender.send(message);
    }

    private SimpleMailMessage buildEmailMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }

    private static BanNotification getBanNotification(UserBanRequest details) {
        return BanNotification.builder()
                .userId(details.getUserId())
                .username(details.getUsername())
                .email(details.getEmail())
                .bannedAt(LocalDateTime.now())
                .build();
    }

    public Optional<BanNotification> findByUsername(String username) {
        return this.banNotificationRepository.findByUsername(username);
    }

    @Transactional
    public void deleteById(UUID id) {
        this.banNotificationRepository.deleteById(id);
    }

    public List<BanNotification> getAllBannedUsers() {
        return this.banNotificationRepository.findAll();
    }
}
