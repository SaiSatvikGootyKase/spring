package com.example.myApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;

/**
 * AllocationRequestDTO - Data Transfer Object for Bed Allocation operations
 * This DTO is used for transferring allocation request data between frontend and backend
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
public class AllocationRequestDTO {
    
    /**
     * ID of the user to allocate the bed to
     * This field is required and must not be blank
     */
    @NotBlank(message = "User ID is required")
    private String userId;
    
    /**
     * ID of the bed to allocate to the user
     * This field is required and must not be blank
     */
    @NotBlank(message = "Bed ID is required")
    private String bedId;
    
    /**
     * Optional notes or comments about the allocation
     * This field is optional and can be used for audit purposes
     */
    private String notes;
    
    /**
     * Reason for allocation (optional)
     * This field is optional and can be used for reporting purposes
     */
    private String reason;
}
