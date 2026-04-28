package com.grantflow;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;

    @GetMapping
    public List<Notification> mine(HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId());
    }

    @PatchMapping("/{id}/read")
    public Notification markRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
