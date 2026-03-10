package com.example.myApp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * User Entity - Represents a user in the room allocation system
 * This entity stores user information and their allocated bed details
 * 
 * @Document annotation specifies that this class is a MongoDB document
 * @Data annotation from Lombok generates getters, setters, toString, equals, and hashCode
 * @NoArgsConstructor generates a no-argument constructor
 * @AllArgsConstructor generates a constructor with all arguments
 * @Builder provides a builder pattern for object creation
 */
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    /**
     * Unique identifier for the user
     * MongoDB will automatically generate this ID if not provided
     */
    @Id
    private String id;
    
    /**
     * Full name of the user
     * This is a required field for user identification
     */
    private String name;
    
    /**
     * Email address of the user
     * Should be unique for each user and used for communication
     */
    private String email;
    
    /**
     * Phone number of the user (optional)
     * Can be used for contact purposes
     */
    private String phoneNumber;
    
    /**
     * ID of the bed allocated to this user
     * This will be null if the user is not allocated any bed
     * Establishes the relationship between User and Bed entities
     */
    private String allocatedBedId;
    
    /**
     * ID of the room where the user's bed is located
     * This will be null if the user is not allocated any bed
     * Helps in quickly identifying the user's room without additional queries
     */
    private String allocatedRoomId;
    
    /**
     * Indicates whether the user is currently allocated a bed
     * true - user has a bed allocated
     * false - user is not allocated any bed
     */
    private boolean isAllocated;
    
    /**
     * Date and time when the user was created in the system
     * Automatically set when user is first created
     */
    private java.time.LocalDateTime createdAt;
    
    /**
     * Date and time when the user information was last updated
     * Automatically updated whenever user information changes
     */
    private java.time.LocalDateTime updatedAt;
}
