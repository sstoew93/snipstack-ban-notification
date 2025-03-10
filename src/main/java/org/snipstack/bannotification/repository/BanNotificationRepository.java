package org.snipstack.bannotification.repository;

import org.snipstack.bannotification.model.BanNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BanNotificationRepository extends JpaRepository<BanNotification, UUID> {

    Optional<BanNotification> findByUserId(UUID userId);

    Optional<BanNotification> findByUsername(String username);
}
