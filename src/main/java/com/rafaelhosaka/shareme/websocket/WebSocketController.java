package com.rafaelhosaka.shareme.websocket;

import com.rafaelhosaka.shareme.exception.NotificationNotFoundException;
import com.rafaelhosaka.shareme.exception.UserProfileNotFoundException;
import com.rafaelhosaka.shareme.message.Message;
import com.rafaelhosaka.shareme.notification.Notification;
import com.rafaelhosaka.shareme.notification.NotificationService;
import com.rafaelhosaka.shareme.user.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private SimpMessagingTemplate simpMessagingTemplate;
    private UserProfileService userProfileService;
    private NotificationService notificationService;

    public WebSocketController(SimpMessagingTemplate simpMessagingTemplate, UserProfileService userProfileService, NotificationService notificationService){
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userProfileService = userProfileService;
        this.notificationService = notificationService;
    }

    @MessageMapping("/change-status")
    public ChatStatus changeStatus(@Payload ChatStatus newStatus){
        try {
            simpMessagingTemplate.convertAndSendToUser(newStatus.getId(),"/status",newStatus);
            userProfileService.changeStatusById(newStatus.getId(), newStatus.isOnline());
        } catch (UserProfileNotFoundException e) {
            e.printStackTrace();
        }
        return newStatus;
    }

    @MessageMapping("/message")
    public Message receiveMessage(@Payload Message message){
        simpMessagingTemplate.convertAndSendToUser(message.getReceiver().getId(),"/private",message);
        return message;
    }

    @MessageMapping("/notification")
    public Notification receiveNotification(@Payload String notificationId){
        Notification notification = null;
        try {
            notification = notificationService.getNotificationById(notificationId);
            simpMessagingTemplate.convertAndSendToUser(notification.getOwnerUserId(),"/notification",notification);
            return notification;
        } catch (NotificationNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}