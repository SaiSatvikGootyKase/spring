package com.example.myApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * BedDTO - Data Transfer Object for Bed operations
 * This DTO is used for transferring bed data between frontend and backend
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
public class BedDTO {
    
    /**
     * Unique identifier for the bed
     * This field is optional for create operations (will be generated)
     */
    private String id;
    
    /**
     * Bed number for identification within the room
     * This field is required and must not be blank
     */
    @NotBlank(message = "Bed number is required")
    @Size(min = 1, max = 20, message = "Bed number must be between 1 and 20 characters")
    private String bedNumber;
    
    /**
     * ID of the room where this bed is located
     * This field is required for bed creation
     */
    @NotBlank(message = "Room ID is required")
    private String roomId;
    
    /**
     * Room number where this bed is located (for display purposes)
     * This field is read-only and populated for frontend display
     */
    private String roomNumber;
    
    /**
     * ID of the user currently allocated to this bed
     * This field is read-only and managed by the system
     */
    private String allocatedUserId;
    
    /**
     * Name of the user currently allocated to this bed (for display purposes)
     * This field is read-only and populated for frontend display
     */
    private String allocatedUserName;
    
    /**
     * Email of the user currently allocated to this bed (for display purposes)
     * This field is read-only and populated for frontend display
     */
    private String allocatedUserEmail;
    
    /**
     * Indicates whether the bed is currently occupied
     * This field is read-only and managed by the system
     */
    private Boolean isOccupied;
    
    /**
     * Type or category of the bed
     * This field is optional but recommended
     */
    @Size(max = 50, message = "Bed type must not exceed 50 characters")
    private String bedType;
    
    /**
     * Position or location of the bed within the room
     * This field is optional
     */
    @Size(max = 50, message = "Position must not exceed 50 characters")
    private String position;
    
    /**
     * Indicates whether the bed is currently active
     * This field defaults to true for new beds
     */
    private Boolean isActive;
    
    /**
     * Additional notes or comments about the bed
     * This field is optional
     */
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
    
    /**
     * Date and time when the bed was created
     * This field is read-only and managed by the system
     */
    private java.time.LocalDateTime createdAt;
    
    /**
     * Date and time when the bed information was last updated
     * This field is read-only and managed by the system
     */
    private java.time.LocalDateTime updatedAt;
    
    /**
     * Date and time when the bed was allocated to the current user
     * This field is read-only and managed by the system
     */
    private java.time.LocalDateTime allocatedAt;
}
