package com.example.myApp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Bed Entity - Represents an individual bed within a room
 * This entity stores bed information and its allocation status
 * 
 * @Document annotation specifies that this class is a MongoDB document
 * @Data annotation from Lombok generates getters, setters, toString, equals, and hashCode
 * @NoArgsConstructor generates a no-argument constructor
 * @AllArgsConstructor generates a constructor with all arguments
 * @Builder provides a builder pattern for object creation
 */
@Document(collection = "beds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bed {
    
    /**
     * Unique identifier for the bed
     * MongoDB will automatically generate this ID if not provided
     */
    @Id
    private String id;
    
    /**
     * Bed number for identification within the room
     * This should be unique within each room
     * Examples: "Bed1", "B1", "Upper-Berth", "Lower-Berth"
     */
    private String bedNumber;
    
    /**
     * ID of the room where this bed is located
     * Establishes the relationship between Bed and Room entities
     * This is a foreign key reference to the Room collection
     */
    private String roomId;
    
    /**
     * Reference to the Room entity (optional, for lazy loading)
     * @DBRef creates a reference to another document in MongoDB
     * This allows us to fetch the complete Room object when needed
     */
    @DBRef
    private Room room;
    
    /**
     * ID of the user currently allocated to this bed
     * This will be null if the bed is not allocated to any user
     * Establishes the relationship between Bed and User entities
     */
    private String allocatedUserId;
    
    /**
     * Reference to the User entity (optional, for lazy loading)
     * @DBRef creates a reference to another document in MongoDB
     * This allows us to fetch the complete User object when needed
     */
    @DBRef
    private User allocatedUser;
    
    /**
     * Indicates whether the bed is currently occupied
     * true - bed is allocated to a user
     * false - bed is available for allocation
     */
    private boolean isOccupied;
    
    /**
     * Type or category of the bed
     * Examples: "Single", "Double", "Bunk-Bed-Upper", "Bunk-Bed-Lower"
     * Helps in categorizing different types of beds
     */
    private String bedType;
    
    /**
     * Position or location of the bed within the room
     * Examples: "Near-Window", "Near-Door", "Corner", "Center"
     * Helps in identifying bed location for users
     */
    private String position;
    
    /**
     * Indicates whether the bed is currently active and available for allocation
     * true - bed is active and can be allocated
     * false - bed is under maintenance or deactivated
     */
    private boolean isActive;
    
    /**
     * Additional notes or comments about the bed
     * Can include maintenance records, special features, etc.
     * This is an optional field
     */
    private String notes;
    
    /**
     * Date and time when the bed was created in the system
     * Automatically set when bed is first created
     */
    private java.time.LocalDateTime createdAt;
    
    /**
     * Date and time when the bed information was last updated
     * Automatically updated whenever bed information changes
     */
    private java.time.LocalDateTime updatedAt;
    
    /**
     * Date and time when the bed was allocated to the current user
     * This will be null if the bed is not occupied
     * Helps in tracking allocation history
     */
    private java.time.LocalDateTime allocatedAt;
    
    /**
     * Helper method to check if bed is available for allocation
     * @return true if bed is available, false otherwise
     */
    public boolean isAvailable() {
        return !isOccupied && isActive;
    }
    
    /**
     * Helper method to allocate bed to a user
     * @param userId ID of the user to allocate this bed to
     */
    public void allocateToUser(String userId) {
        this.allocatedUserId = userId;
        this.isOccupied = true;
        this.allocatedAt = java.time.LocalDateTime.now();
    }
    
    /**
     * Helper method to deallocate bed from current user
     */
    public void deallocate() {
        this.allocatedUserId = null;
        this.allocatedUser = null;
        this.isOccupied = false;
        this.allocatedAt = null;
    }
}
