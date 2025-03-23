package org.snipstack.bannotification;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.snipstack.bannotification.model.BanNotification;
import org.snipstack.bannotification.repository.BanNotificationRepository;
import org.snipstack.bannotification.service.EmailService;
import org.snipstack.bannotification.web.dto.UserBanRequest;
import org.snipstack.bannotification.web.dto.UserUnbanRequest;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceUTest {

    @Mock
    private BanNotificationRepository banNotificationRepository;

    @Mock
    private MailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private BanNotification ban;

    @BeforeEach
    void setUp() {
         ban = BanNotification.builder()
                .id(UUID.randomUUID())
                .username("sstoew")
                .bannedAt(LocalDateTime.now())
                .userId(UUID.randomUUID())
                .email("sstoew@snipstack.com")
                .build();
    }

    @Test
    void getAllBannedUsers_ShouldReturnAllBannedUsers() {

        BanNotification banNotification2 = BanNotification.builder()
                .id(UUID.randomUUID())
                .username("sstoew2")
                .bannedAt(LocalDateTime.now())
                .userId(UUID.randomUUID())
                .email("sstoew2@snipstack.com")
                .build();

        when(banNotificationRepository.findAll()).thenReturn(List.of(ban, banNotification2));

        List<BanNotification> allBannedUsers = emailService.getAllBannedUsers();

        assertTrue(allBannedUsers.containsAll(List.of(ban, banNotification2)));
        verify(banNotificationRepository, times(1)).findAll();
    }

    @Test
    void deleteById_ShouldDeleteBannedUser() {
        emailService.deleteById(ban.getId());

        verify(banNotificationRepository, times(1)).deleteById(ban.getId());
    }

    @Test
    void deleteById_ShouldNotBeCalled_WhenNotInvoked() {
        verify(banNotificationRepository, never()).deleteById(UUID.randomUUID());
    }

    @Test
    void findByUsername_ShouldReturnBannedUser() {
        when(banNotificationRepository.findByUsername("sstoew")).thenReturn(Optional.of(ban));

        emailService.findByUsername("sstoew");

        verify(banNotificationRepository, times(1)).findByUsername("sstoew");
    }

    @Test
    void findByUsernameDoesNotExist_ShouldReturnEmptyOptional() {
        when(banNotificationRepository.findByUsername("sstoew")).thenReturn(Optional.empty());
        Optional<BanNotification> optional = emailService.findByUsername("sstoew");

        assertTrue(optional.isEmpty());
        verify(banNotificationRepository, times(1)).findByUsername("sstoew");

    }

    @Test
    void sendBanNotification_ShouldThrowException_WhenUserAlreadyBanned() {
        UserBanRequest request = UserBanRequest.builder()
                .userId(UUID.randomUUID())
                .email("sstoew@snipstack.com")
                .username("sstoew")
                .build();

        when(banNotificationRepository.findByUsername("sstoew")).thenReturn(Optional.of(ban));
        assertThrows(IllegalArgumentException.class, () -> emailService.sendBanNotification(request));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendUnbanNotification_ShouldThrowException_WhenUserNotBanned() {
        UserUnbanRequest request = UserUnbanRequest.builder()
                .username("sstoew")
                .build();

        when(banNotificationRepository.findByUsername("sstoew")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> emailService.sendUnbanNotification(request));
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendUnbanNotification_ShouldSendEmail() {
        UserUnbanRequest request = UserUnbanRequest.builder()
                .username("sstoew")
                .build();

        when(banNotificationRepository.findByUsername("sstoew")).thenReturn(Optional.of(ban));

        emailService.sendUnbanNotification(request);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendBanNotification_ShouldSendEmail() {
        UserBanRequest request = UserBanRequest.builder()
                .username("sstoew")
                .userId(UUID.randomUUID())
                .email("sstoew@snipstack.com")
                .build();

        when(banNotificationRepository.findByUsername("sstoew")).thenReturn(Optional.empty());

        emailService.sendBanNotification(request);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
