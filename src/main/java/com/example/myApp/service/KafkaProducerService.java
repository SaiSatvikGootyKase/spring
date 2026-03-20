package com.example.myApp.service;

import com.example.myApp.entity.Room;
import com.example.myApp.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer Service
 * Handles publishing events to Kafka topics for the room allocation system
 * 
 * @Service marks this as a Spring service component
 * @RequiredArgsConstructor generates constructor with required fields (final fields)
 * @Slf4j provides logging capabilities
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Topic names
    public static final String USER_EVENTS_TOPIC = "user-events";
    public static final String ROOM_EVENTS_TOPIC = "room-events";
    public static final String ALLOCATION_EVENTS_TOPIC = "allocation-events";
    public static final String NOTIFICATION_EVENTS_TOPIC = "notification-events";

    /**
     * Publishes user-related events
     * 
     * @param eventType Type of user event (CREATED, UPDATED, DELETED)
     * @param user The user object
     */
    public void publishUserEvent(String eventType, User user) {
        try {
            UserEvent event = UserEvent.builder()
                .eventType(eventType)
                .userId(user.getId())
                .user(user)
                .timestamp(System.currentTimeMillis())
                .build();

            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(USER_EVENTS_TOPIC, user.getId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("User event published successfully: {} for user: {}", 
                        eventType, user.getId());
                } else {
                    log.error("Failed to publish user event: {} for user: {}", 
                        eventType, user.getId(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error publishing user event: {} for user: {}", 
                eventType, user.getId(), e);
        }
    }

    /**
     * Publishes room-related events
     * 
     * @param eventType Type of room event (CREATED, UPDATED, DELETED, OCCUPANCY_CHANGED)
     * @param room The room object
     */
    public void publishRoomEvent(String eventType, Room room) {
        try {
            RoomEvent event = RoomEvent.builder()
                .eventType(eventType)
                .roomId(room.getId())
                .room(room)
                .timestamp(System.currentTimeMillis())
                .build();

            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(ROOM_EVENTS_TOPIC, room.getId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Room event published successfully: {} for room: {}", 
                        eventType, room.getId());
                } else {
                    log.error("Failed to publish room event: {} for room: {}", 
                        eventType, room.getId(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error publishing room event: {} for room: {}", 
                eventType, room.getId(), e);
        }
    }

    /**
     * Publishes allocation-related events
     * 
     * @param eventType Type of allocation event (ALLOCATED, DEALLOCATED, TRANSFERRED)
     * @param userId The user ID
     * @param roomId The room ID
     * @param bedNumber The bed number
     */
    public void publishAllocationEvent(String eventType, String userId, String roomId, String bedNumber) {
        try {
            AllocationEvent event = AllocationEvent.builder()
                .eventType(eventType)
                .userId(userId)
                .roomId(roomId)
                .bedNumber(bedNumber)
                .timestamp(System.currentTimeMillis())
                .build();

            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(ALLOCATION_EVENTS_TOPIC, userId, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Allocation event published successfully: {} for user: {} to room: {}", 
                        eventType, userId, roomId);
                } else {
                    log.error("Failed to publish allocation event: {} for user: {} to room: {}", 
                        eventType, userId, roomId, ex);
                }
            });

        } catch (Exception e) {
            log.error("Error publishing allocation event: {} for user: {} to room: {}", 
                eventType, userId, roomId, e);
        }
    }

    /**
     * Publishes notification events
     * 
     * @param notificationType Type of notification (EMAIL, SMS, PUSH)
     * @param recipient The recipient (email/phone)
     * @param message The notification message
     */
    public void publishNotificationEvent(String notificationType, String recipient, String message) {
        try {
            NotificationEvent event = NotificationEvent.builder()
                .notificationType(notificationType)
                .recipient(recipient)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();

            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(NOTIFICATION_EVENTS_TOPIC, recipient, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Notification event published successfully: {} to: {}", 
                        notificationType, recipient);
                } else {
                    log.error("Failed to publish notification event: {} to: {}", 
                        notificationType, recipient, ex);
                }
            });

        } catch (Exception e) {
            log.error("Error publishing notification event: {} to: {}", 
                notificationType, recipient, e);
        }
    }

    // Event DTOs
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class UserEvent {
        private String eventType;
        private String userId;
        private User user;
        private Long timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class RoomEvent {
        private String eventType;
        private String roomId;
        private Room room;
        private Long timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class AllocationEvent {
        private String eventType;
        private String userId;
        private String roomId;
        private String bedNumber;
        private Long timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class NotificationEvent {
        private String notificationType;
        private String recipient;
        private String message;
        private Long timestamp;
    }
}
