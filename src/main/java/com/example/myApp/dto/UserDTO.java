package com.example.myApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UserDTO - Data Transfer Object for User operations
 * This DTO is used for transferring user data between frontend and backend
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
public class UserDTO {
    
    /**
     * Unique identifier for the user
     * This field is optional for create operations (will be generated)
     */
    private String id;
    
    /**
     * Full name of the user
     * This field is required and must not be blank
     */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    /**
     * Email address of the user
     * This field is required and must be a valid email format
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    /**
     * Phone number of the user (optional)
     * This field is optional and can be null
     */
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
    
    /**
     * Indicates whether the user is currently allocated a bed
     * This field is read-only and managed by the system
     */
    private Boolean isAllocated;
    
    /**
     * ID of the bed allocated to this user
     * This field is read-only and managed by the system
     */
    private String allocatedBedId;
    
    /**
     * ID of the room where the user's bed is located
     * This field is read-only and managed by the system
     */
    private String allocatedRoomId;
    
    /**
     * Room number where the user is allocated (for display purposes)
     * This field is read-only and populated for frontend display
     */
    private String allocatedRoomNumber;
    
    /**
     * Bed number where the user is allocated (for display purposes)
     * This field is read-only and populated for frontend display
     */
    private String allocatedBedNumber;
    
    /**
     * Date and time when the user was created
     * This field is read-only and managed by the system
     */
    private java.time.LocalDateTime createdAt;
    
    /**
     * Date and time when the user information was last updated
     * This field is read-only and managed by the system
     */
    private java.time.LocalDateTime updatedAt;
}
