package org.snipstack.bannotification.service;

import org.snipstack.bannotification.model.BanNotification;
import org.snipstack.bannotification.repository.BanNotificationRepository;
import org.snipstack.bannotification.web.dto.UserBanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public BanNotification sendNotification(UserBanRequest details) {
        Optional<BanNotification> optional = this.banNotificationRepository.findByUserId(details.getUserId());

        if (optional.isPresent()) {
            throw new NullPointerException("User already banned!");
        }

        SimpleMailMessage message = getSimpleMailMessage(details);

        this.mailSender.send(message);

        BanNotification banNotification = getBanNotification(details);
        return this.banNotificationRepository.save(banNotification);
    }

    private static SimpleMailMessage getSimpleMailMessage(UserBanRequest details) {
        SimpleMailMessage message = new SimpleMailMessage();

        String messageBody = "Dear " + details.getUsername() + ",\n\n" +
                "We regret to inform you that your account has been banned from our forum.\n\n" +
                "If you believe this was a mistake or wish to appeal, please contact us at support@snipstack.com.\n\n" +
                "Best regards,\nSnipStack Community Admin Team";

        message.setTo(details.getEmail());
        message.setSubject("SnipStack Notification: Your account has been banned!");
        message.setText(messageBody);
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

    public Optional<BanNotification> findByUserId(UUID id) {
        return this.banNotificationRepository.findByUserId(id);
    }
}
