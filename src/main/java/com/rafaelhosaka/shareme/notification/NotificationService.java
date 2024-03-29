package com.rafaelhosaka.shareme.notification;

import com.rafaelhosaka.shareme.exception.NotificationNotFoundException;
import com.rafaelhosaka.shareme.exception.UserProfileNotFoundException;
import com.rafaelhosaka.shareme.friend.FriendRequest;
import com.rafaelhosaka.shareme.user.UserProfile;
import com.rafaelhosaka.shareme.user.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserProfileService userProfileService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserProfileService userProfileService){
        this.notificationRepository = notificationRepository;
        this.userProfileService = userProfileService;
    }

    public Notification createFriendRequestNotification(FriendRequest friendRequest) throws UserProfileNotFoundException {
        UserProfile requestingUser = userProfileService.getUserProfileById(friendRequest.getRequestingUserId());
        FriendRequestNotification notification = new FriendRequestNotification();
        notification.setFriendRequesting(requestingUser);
        notification.setOwnerUserId(friendRequest.getTargetUserId());
        return notificationRepository.save(notification);
    }

    public Notification createFriendAcceptedNotification(FriendRequest friendRequest) throws UserProfileNotFoundException {
        UserProfile acceptedUser = userProfileService.getUserProfileById(friendRequest.getTargetUserId());
        FriendAcceptedNotification notification = new FriendAcceptedNotification();
        notification.setAcceptedFriend(acceptedUser);
        notification.setOwnerUserId(friendRequest.getRequestingUserId());
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserId(String id) {
        return notificationRepository.getByUserId(id);
    }

    public Notification markAsRead(String id) throws NotificationNotFoundException {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new NotificationNotFoundException("Notification with id "+id+" not found")
        );
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public int unreadCount(String id) {
        return notificationRepository.getUnreadByUserId(id).size();
    }

    public Notification getNotificationById(String notificationId) throws NotificationNotFoundException{
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                () -> new NotificationNotFoundException("Notification with id "+notificationId+" not found")
        );
        return notification;
    }

    public void deleteNotification(String notificationId) throws NotificationNotFoundException {
        notificationRepository.deleteById(notificationId);
    }
}
