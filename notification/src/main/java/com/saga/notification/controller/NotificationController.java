package com.saga.notification.controller;

import com.saga.common.dto.NotificationDto;
import com.saga.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/notification")
    public NotificationDto.NotificationResponse sendNotification(@RequestBody NotificationDto.NotificationRequest request) {
        return notificationService.sendNotification(request);
    }
}
