package com.example.myApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * RoomDTO - Data Transfer Object for Room operations
 * This DTO is used for transferring room data between frontend and backend
 * It includes validation annotations to ensure data integrity
 * 
 * @Data annotation from Lombok generates getters, setters, toString, equals, and hashCode
 * @NoArgsConstructor generates a no-argument constructor
 * @AllArgsConstructor generates a constructor with all arguments
 * @Builder provides a builder pattern for object creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    
    /**
     * Unique identifier for the room
     * This field is optional for create operations (will be generated)
     */
    private String id;
    
    /**
     * Room number for identification
     * This field is required and must not be blank
     */
    @NotBlank(message = "Room number is required")
    @Size(min = 1, max = 20, message = "Room number must be between 1 and 20 characters")
    private String roomNumber;
    
    /**
     * Type or category of the room
     * This field is optional but recommended
     */
    @Size(max = 50, message = "Room type must not exceed 50 characters")
    private String roomType;
    
    /**
     * Maximum number of beds this room can accommodate
     * This field is required and must be a positive integer
     */
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    /**
     * Current number of occupied beds in this room
     * This field is read-only and managed by the system
     */
    private Integer occupiedBeds;
    
    /**
     * Floor number where this room is located
     * This field is optional but recommended
     */
    @Min(value = 0, message = "Floor number must be 0 or greater")
    private Integer floorNumber;
    
    /**
     * Description or additional details about the room
     * This field is optional
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    /**
     * Indicates whether the room is currently active
     * This field defaults to true for new rooms
     */
    private Boolean isActive;
    
    /**
     * Indicates whether the room is currently full
     * This field is read-only and calculated by the system
     */
    private Boolean isFull;
    
    /**
     * Number of available beds in the room
     * This field is read-only and calculated by the system
     */
    private Integer availableBeds;
    
    /**
     * Date and time when the room was created
     * This field is read-only and managed by the system
     */
    private java.time.LocalDateTime createdAt;
    
    /**
     * Date and time when the room information was last updated
     * This field is read-only and managed by the system
     */
    private java.time.LocalDateTime updatedAt;
}
