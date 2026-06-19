package com.travvite.backend.controller;

import com.travvite.backend.model.Notification;
import com.travvite.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ===== GET NOTIFICATIONS =====
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                notificationService.getNotificationsUser(userId));
    }

    // ===== MARQUER COMME LU =====
    @PutMapping("/{id}/lu")
    public ResponseEntity<?> marquerLu(@PathVariable Long id) {
        notificationService.marquerCommeLu(id);
        return ResponseEntity.ok(Map.of("message", "Notification lue"));
    }
}