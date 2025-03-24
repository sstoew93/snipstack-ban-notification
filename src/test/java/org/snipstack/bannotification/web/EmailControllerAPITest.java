package org.snipstack.bannotification.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.snipstack.bannotification.model.BanNotification;
import org.snipstack.bannotification.service.EmailService;
import org.snipstack.bannotification.web.dto.UserBanRequest;
import org.snipstack.bannotification.web.dto.UserUnbanRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
public class EmailControllerAPITest {

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private MockMvc mockMvc;

    private BanNotification banNotification;

    @BeforeEach
    void setUp() {
         banNotification = BanNotification.builder()
                .username("sstoew")
                .email("sstoew@snipstack.com")
                .id(UUID.randomUUID())
                .bannedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getBannedUsersRequest_ShouldReturnBannedUsers() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1/banned-users");

        List<BanNotification> bannedUsers = List.of(new BanNotification(), new BanNotification());

        when(emailService.getAllBannedUsers()).thenReturn(bannedUsers);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(emailService, times(1)).getAllBannedUsers();
    }

    @Test
    void unbanNotificationRequest_ShouldDeleteBanNotificationById() throws Exception {
        String body = "{\"username\": \"sstoew\"}";
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/unban-notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        when(emailService.findByUsername(banNotification.getUsername())).thenReturn(Optional.of(banNotification));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        verify(emailService, times(1)).sendUnbanNotification(any(UserUnbanRequest.class));
        verify(emailService, times(1)).deleteById(banNotification.getId());
    }

    @Test
    void unbanNotificationRequest_ShouldThrowException() throws Exception {
        String body = "{\"username\": \"sstoew\"}";

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/unban-notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);

        when(emailService.findByUsername(banNotification.getUsername())).thenReturn(Optional.empty());

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

        verify(emailService, never()).sendUnbanNotification(any(UserUnbanRequest.class));
        verify(emailService, never()).deleteById(UUID.randomUUID());
    }

    @Test
    void sendBanNotificationRequest_ShouldSendBanNotification() throws Exception {
        BanNotification banNotification = BanNotification.builder()
                .username("sstoew")
                .email("sstoew@snipstack.com")
                .id(UUID.randomUUID())
                .build();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/send-notification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(banNotification));

                when(emailService.sendBanNotification(any(UserBanRequest.class))).thenReturn(banNotification);

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        verify(emailService, times(1)).sendBanNotification(any(UserBanRequest.class));
    }
}
