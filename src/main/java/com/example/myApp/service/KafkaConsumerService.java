package com.example.myApp.service;

import com.example.myApp.service.KafkaProducerService.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer Service
 * Handles consuming events from Kafka topics for the room allocation system
 * 
 * @Service marks this as a Spring service component
 * @RequiredArgsConstructor generates constructor with required fields (final fields)
 * @Slf4j provides logging capabilities
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final UserService userService;
    private final RoomService roomService;
    private final RoomAllocationService allocationService;

    /**
     * Consumes user events from user-events topic
     * 
     * @param userEvent The user event payload
     * @param acknowledgment The acknowledgment for manual commit
     * @param topic The topic name
     * @param partition The partition number
     * @param offset The message offset
     */
    @KafkaListener(
        topics = KafkaProducerService.USER_EVENTS_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserEvent(
            @Payload UserEvent userEvent,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            log.info("Processing user event: {} for user: {} from topic: {}, partition: {}, offset: {}", 
                userEvent.getEventType(), userEvent.getUserId(), topic, partition, offset);

            switch (userEvent.getEventType()) {
                case "CREATED":
                    handleUserCreated(userEvent);
                    break;
                case "UPDATED":
                    handleUserUpdated(userEvent);
                    break;
                case "DELETED":
                    handleUserDeleted(userEvent);
                    break;
                default:
                    log.warn("Unknown user event type: {}", userEvent.getEventType());
            }

            // Manually acknowledge the message
            acknowledgment.acknowledge();
            log.debug("Successfully acknowledged user event: {}", userEvent.getEventType());

        } catch (Exception e) {
            log.error("Error processing user event: {} for user: {}", 
                userEvent.getEventType(), userEvent.getUserId(), e);
            // In a production environment, you might want to implement dead letter queue handling here
        }
    }

    /**
     * Consumes room events from room-events topic
     */
    @KafkaListener(
        topics = KafkaProducerService.ROOM_EVENTS_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleRoomEvent(
            @Payload RoomEvent roomEvent,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            log.info("Processing room event: {} for room: {} from topic: {}, partition: {}, offset: {}", 
                roomEvent.getEventType(), roomEvent.getRoomId(), topic, partition, offset);

            switch (roomEvent.getEventType()) {
                case "CREATED":
                    handleRoomCreated(roomEvent);
                    break;
                case "UPDATED":
                    handleRoomUpdated(roomEvent);
                    break;
                case "DELETED":
                    handleRoomDeleted(roomEvent);
                    break;
                case "OCCUPANCY_CHANGED":
                    handleRoomOccupancyChanged(roomEvent);
                    break;
                default:
                    log.warn("Unknown room event type: {}", roomEvent.getEventType());
            }

            acknowledgment.acknowledge();
            log.debug("Successfully acknowledged room event: {}", roomEvent.getEventType());

        } catch (Exception e) {
            log.error("Error processing room event: {} for room: {}", 
                roomEvent.getEventType(), roomEvent.getRoomId(), e);
        }
    }

    /**
     * Consumes allocation events from allocation-events topic
     */
    @KafkaListener(
        topics = KafkaProducerService.ALLOCATION_EVENTS_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAllocationEvent(
            @Payload AllocationEvent allocationEvent,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            log.info("Processing allocation event: {} for user: {} in room: {} from topic: {}, partition: {}, offset: {}", 
                allocationEvent.getEventType(), allocationEvent.getUserId(), allocationEvent.getRoomId(), 
                topic, partition, offset);

            switch (allocationEvent.getEventType()) {
                case "ALLOCATED":
                    handleUserAllocated(allocationEvent);
                    break;
                case "DEALLOCATED":
                    handleUserDeallocated(allocationEvent);
                    break;
                case "TRANSFERRED":
                    handleUserTransferred(allocationEvent);
                    break;
                default:
                    log.warn("Unknown allocation event type: {}", allocationEvent.getEventType());
            }

            acknowledgment.acknowledge();
            log.debug("Successfully acknowledged allocation event: {}", allocationEvent.getEventType());

        } catch (Exception e) {
            log.error("Error processing allocation event: {} for user: {}", 
                allocationEvent.getEventType(), allocationEvent.getUserId(), e);
        }
    }

    /**
     * Consumes notification events from notification-events topic
     */
    @KafkaListener(
        topics = KafkaProducerService.NOTIFICATION_EVENTS_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotificationEvent(
            @Payload NotificationEvent notificationEvent,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            log.info("Processing notification event: {} to: {} from topic: {}, partition: {}, offset: {}", 
                notificationEvent.getNotificationType(), notificationEvent.getRecipient(), 
                topic, partition, offset);

            // Process different types of notifications
            processNotification(notificationEvent);

            acknowledgment.acknowledge();
            log.debug("Successfully acknowledged notification event: {}", notificationEvent.getNotificationType());

        } catch (Exception e) {
            log.error("Error processing notification event: {} to: {}", 
                notificationEvent.getNotificationType(), notificationEvent.getRecipient(), e);
        }
    }

    // Event handler methods
    private void handleUserCreated(UserEvent userEvent) {
        log.info("User created event received for user: {}", userEvent.getUserId());
        // Additional business logic for user creation events
        // e.g., send welcome email, update analytics, etc.
    }

    private void handleUserUpdated(UserEvent userEvent) {
        log.info("User updated event received for user: {}", userEvent.getUserId());
        // Additional business logic for user update events
        // e.g., sync with external systems, update cache, etc.
    }

    private void handleUserDeleted(UserEvent userEvent) {
        log.info("User deleted event received for user: {}", userEvent.getUserId());
        // Additional business logic for user deletion events
        // e.g., cleanup related resources, archive data, etc.
    }

    private void handleRoomCreated(RoomEvent roomEvent) {
        log.info("Room created event received for room: {}", roomEvent.getRoomId());
        // Additional business logic for room creation events
    }

    private void handleRoomUpdated(RoomEvent roomEvent) {
        log.info("Room updated event received for room: {}", roomEvent.getRoomId());
        // Additional business logic for room update events
    }

    private void handleRoomDeleted(RoomEvent roomEvent) {
        log.info("Room deleted event received for room: {}", roomEvent.getRoomId());
        // Additional business logic for room deletion events
    }

    private void handleRoomOccupancyChanged(RoomEvent roomEvent) {
        log.info("Room occupancy changed event received for room: {}", roomEvent.getRoomId());
        // Additional business logic for occupancy changes
        // e.g., update statistics, trigger alerts, etc.
    }

    private void handleUserAllocated(AllocationEvent allocationEvent) {
        log.info("User allocated event received for user: {} to room: {}", 
            allocationEvent.getUserId(), allocationEvent.getRoomId());
        // Additional business logic for user allocation
        // e.g., send confirmation, update reports, etc.
    }

    private void handleUserDeallocated(AllocationEvent allocationEvent) {
        log.info("User deallocated event received for user: {} from room: {}", 
            allocationEvent.getUserId(), allocationEvent.getRoomId());
        // Additional business logic for user deallocation
        // e.g., send notifications, update availability, etc.
    }

    private void handleUserTransferred(AllocationEvent allocationEvent) {
        log.info("User transferred event received for user: {} to room: {}", 
            allocationEvent.getUserId(), allocationEvent.getRoomId());
        // Additional business logic for user transfer
        // e.g., update records, send notifications, etc.
    }

    private void processNotification(NotificationEvent notificationEvent) {
        log.info("Processing {} notification to: {}", 
            notificationEvent.getNotificationType(), notificationEvent.getRecipient());
        
        // Here you would integrate with actual notification services
        // e.g., email service, SMS service, push notification service
        
        switch (notificationEvent.getNotificationType()) {
            case "EMAIL":
                // Integrate with email service
                log.debug("Sending email notification to: {}", notificationEvent.getRecipient());
                break;
            case "SMS":
                // Integrate with SMS service
                log.debug("Sending SMS notification to: {}", notificationEvent.getRecipient());
                break;
            case "PUSH":
                // Integrate with push notification service
                log.debug("Sending push notification to: {}", notificationEvent.getRecipient());
                break;
            default:
                log.warn("Unknown notification type: {}", notificationEvent.getNotificationType());
        }
    }
}
