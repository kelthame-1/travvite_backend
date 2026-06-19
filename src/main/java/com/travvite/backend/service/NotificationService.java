package com.travvite.backend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.travvite.backend.model.*;
import com.travvite.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ===== ENVOYER NOTIFICATION =====
    @Async
    public void envoyerNotification(User user, String titre,
                                    String corps, TypeNotification type) {
        // 1 — سجل في DB
        notificationRepository.save(
                Notification.builder()
                        .user(user)
                        .titre(titre)
                        .corps(corps)
                        .type(type)
                        .lu(false)
                        .build()
        );

        // 2 — أرسل FCM إذا عنده token
        if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
            try {
                Message message = Message.builder()
                        .setToken(user.getFcmToken())
                        .putData("titre", titre)
                        .putData("corps", corps)
                        .putData("type", type.name())
                        .build();
                FirebaseMessaging.getInstance().send(message);
            } catch (Exception e) {
                log.error("Erreur FCM: {}", e.getMessage());
            }
        }
    }

    // ===== GET NOTIFICATIONS USER =====
    public List<Notification> getNotificationsUser(Long userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ===== MARQUER COMME LU =====
    public void marquerCommeLu(Long notificationId) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notification.setLu(true);
        notificationRepository.save(notification);
    }
}