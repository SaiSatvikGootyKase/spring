package com.example.myApp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Room Entity - Represents a room in the hostel/building
 * This entity stores room information and contains multiple beds
 * 
 * @Document annotation specifies that this class is a MongoDB document
 * @Data annotation from Lombok generates getters, setters, toString, equals, and hashCode
 * @NoArgsConstructor generates a no-argument constructor
 * @AllArgsConstructor generates a constructor with all arguments
 * @Builder provides a builder pattern for object creation
 */
@Document(collection = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    
    /**
     * Unique identifier for the room
     * MongoDB will automatically generate this ID if not provided
     */
    @Id
    private String id;
    
    /**
     * Room number for identification
     * This should be unique within the building/hostel
     * Examples: "101", "A-201", "Floor1-Room5"
     */
    private String roomNumber;
    
    /**
     * Type or category of the room
     * Examples: "Single", "Double", "Dormitory", "Suite"
     * Helps in categorizing different types of rooms
     */
    private String roomType;
    
    /**
     * Maximum number of beds this room can accommodate
     * This defines the capacity of the room
     * Should be a positive integer
     */
    private int capacity;
    
    /**
     * Current number of occupied beds in this room
     * This should always be less than or equal to capacity
     * Helps in tracking room availability
     */
    private int occupiedBeds;
    
    /**
     * Floor number where this room is located
     * Helps in organizing rooms by floor levels
     * Examples: 1, 2, 3, etc.
     */
    private int floorNumber;
    
    /**
     * Description or additional details about the room
     * Can include amenities, special features, etc.
     * This is an optional field
     */
    private String description;
    
    /**
     * Indicates whether the room is currently active and available for allocation
     * true - room is active and can be used for bed allocation
     * false - room is under maintenance or deactivated
     */
    private boolean isActive;
    
    /**
     * Indicates whether the room is currently full (all beds occupied)
     * This is a computed field based on occupiedBeds and capacity
     * true - all beds are occupied (occupiedBeds == capacity)
     * false - there are available beds
     */
    private boolean isFull;
    
    /**
     * Date and time when the room was created in the system
     * Automatically set when room is first created
     */
    private java.time.LocalDateTime createdAt;
    
    /**
     * Date and time when the room information was last updated
     * Automatically updated whenever room information changes
     */
    private java.time.LocalDateTime updatedAt;
    
    /**
     * Helper method to check if room has available beds
     * @return true if room has available beds, false otherwise
     */
    public boolean hasAvailableBeds() {
        return occupiedBeds < capacity && isActive;
    }
    
    /**
     * Helper method to get number of available beds
     * @return number of available beds in the room
     */
    public int getAvailableBedsCount() {
        return isActive ? (capacity - occupiedBeds) : 0;
    }
}
