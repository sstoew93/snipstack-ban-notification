package org.snipstack.bannotification.integration;

import org.junit.jupiter.api.Test;
import org.snipstack.bannotification.model.BanNotification;
import org.snipstack.bannotification.repository.BanNotificationRepository;
import org.snipstack.bannotification.service.EmailService;
import org.snipstack.bannotification.web.dto.UserBanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EmailServiceITest {

    @Autowired
    private EmailService emailService;
    @Autowired
    private BanNotificationRepository banNotificationRepository;

    @Test
    public void sendBanNotification_ShouldSendEmailAndSaveTheBanNotification() {
        UUID userId = UUID.randomUUID();

        UserBanRequest banRequest = UserBanRequest.builder()
                .userId(userId)
                .email("sstoew@snipstack.com")
                .username("sstoew")
                .build();

        emailService.sendBanNotification(banRequest);

        Optional<BanNotification> optional = banNotificationRepository.findByUsername(banRequest.getUsername());
        assertTrue(optional.isPresent());

        BanNotification banNotification = optional.get();
        assertEquals(banRequest.getUsername(), banNotification.getUsername());
        assertEquals(banRequest.getEmail(), banNotification.getEmail());
        assertEquals(1, banNotificationRepository.count());


    }


}
