package com.example.myApp.controller;

import com.example.myApp.service.RoomAllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * RoomAllocationController - REST API endpoints for Room Allocation operations
 * This controller provides HTTP endpoints for managing bed allocations with transaction support
 * 
 * @RestController annotation combines @Controller and @ResponseBody
 * @RequestMapping specifies the base path for all endpoints in this controller
 * @RequiredArgsConstructor generates constructor with required fields (final fields)
 * @Slf4j provides logging capabilities
 */
@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend
@Slf4j
public class RoomAllocationController {
    
    private final RoomAllocationService roomAllocationService;
    
    /**
     * Allocate a bed to a user
     * HTTP POST: /api/allocations/allocate
     * 
     * @param allocationRequest Map containing userId and bedId
     * @return ResponseEntity containing allocation result and HTTP status 200 (OK),
     *         or HTTP status 400 (BAD_REQUEST) if allocation fails
     */
    @PostMapping("/allocate")
    public ResponseEntity<RoomAllocationService.AllocationResult> allocateBedToUser(@RequestBody Map<String, String> allocationRequest) {
        log.info("REST request to allocate bed {} to user {}", 
            allocationRequest.get("bedId"), allocationRequest.get("userId"));
        
        try {
            String userId = allocationRequest.get("userId");
            String bedId = allocationRequest.get("bedId");
            
            RoomAllocationService.AllocationResult result = roomAllocationService.allocateBedToUser(userId, bedId);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            log.error("Failed to allocate bed: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Deallocate a bed from a user
     * HTTP POST: /api/allocations/deallocate/{userId}
     * 
     * @param userId The ID of the user to deallocate
     * @return ResponseEntity containing deallocation result and HTTP status 200 (OK),
     *         or HTTP status 400 (BAD_REQUEST) if deallocation fails
     */
    @PostMapping("/deallocate/{userId}")
    public ResponseEntity<RoomAllocationService.DeallocationResult> deallocateBedFromUser(@PathVariable String userId) {
        log.info("REST request to deallocate bed from user {}", userId);
        
        try {
            RoomAllocationService.DeallocationResult result = roomAllocationService.deallocateBedFromUser(userId);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            log.error("Failed to deallocate bed: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Transfer a user from one bed to another
     * HTTP POST: /api/allocations/transfer
     * 
     * @param transferRequest Map containing userId and newBedId
     * @return ResponseEntity containing transfer result and HTTP status 200 (OK),
     *         or HTTP status 400 (BAD_REQUEST) if transfer fails
     */
    @PostMapping("/transfer")
    public ResponseEntity<RoomAllocationService.TransferResult> transferUserToNewBed(@RequestBody Map<String, String> transferRequest) {
        log.info("REST request to transfer user {} to new bed {}", 
            transferRequest.get("userId"), transferRequest.get("newBedId"));
        
        try {
            String userId = transferRequest.get("userId");
            String newBedId = transferRequest.get("newBedId");
            
            RoomAllocationService.TransferResult result = roomAllocationService.transferUserToNewBed(userId, newBedId);
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            log.error("Failed to transfer user: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Get allocation history for a user
     * HTTP GET: /api/allocations/user/{userId}/history
     * 
     * @param userId The user ID to get history for
     * @return ResponseEntity containing allocation history and HTTP status 200 (OK),
     *         or HTTP status 404 (NOT_FOUND) if user not found
     */
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<RoomAllocationService.AllocationHistory> getUserAllocationHistory(@PathVariable String userId) {
        log.debug("REST request to get allocation history for user {}", userId);
        
        try {
            RoomAllocationService.AllocationHistory history = roomAllocationService.getUserAllocationHistory(userId);
            return ResponseEntity.ok(history);
            
        } catch (IllegalArgumentException e) {
            log.error("Failed to get allocation history: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Get all allocations in the system
     * HTTP GET: /api/allocations
     * 
     * @return ResponseEntity containing list of all allocations and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<RoomAllocationService.AllocationInfo>> getAllAllocations() {
        log.debug("REST request to get all allocations");
        
        List<RoomAllocationService.AllocationInfo> allocations = roomAllocationService.getAllAllocations();
        return ResponseEntity.ok(allocations);
    }
    
    /**
     * Get allocation statistics
     * HTTP GET: /api/allocations/statistics
     * 
     * @return ResponseEntity containing allocation statistics and HTTP status 200 (OK)
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getAllocationStatistics() {
        log.debug("REST request to get allocation statistics");
        
        // Get all allocations to calculate statistics
        List<RoomAllocationService.AllocationInfo> allocations = roomAllocationService.getAllAllocations();
        
        Map<String, Object> statistics = Map.of(
            "totalAllocations", allocations.size(),
            "timestamp", System.currentTimeMillis(),
            "status", "UP"
        );
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Validate if a bed can be allocated to a user
     * HTTP GET: /api/allocations/validate?userId={userId}&bedId={bedId}
     * 
     * @param userId The user ID to validate
     * @param bedId The bed ID to validate
     * @return ResponseEntity containing validation result and HTTP status 200 (OK)
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateAllocation(
            @RequestParam String userId, 
            @RequestParam String bedId) {
        log.debug("REST request to validate allocation of bed {} to user {}", bedId, userId);
        
        try {
            // Try to get user allocation history to check if user is already allocated
            RoomAllocationService.AllocationHistory history = roomAllocationService.getUserAllocationHistory(userId);
            
            boolean userAlreadyAllocated = history.getUser().isAllocated();
            boolean canAllocate = !userAlreadyAllocated;
            
            Map<String, Object> validation = Map.of(
                "userId", userId,
                "bedId", bedId,
                "canAllocate", canAllocate,
                "userAlreadyAllocated", userAlreadyAllocated,
                "message", canAllocate ? "Allocation is valid" : "User is already allocated to a bed"
            );
            
            return ResponseEntity.ok(validation);
            
        } catch (IllegalArgumentException e) {
            log.error("Validation failed: {}", e.getMessage());
            Map<String, Object> validation = Map.of(
                "userId", userId,
                "bedId", bedId,
                "canAllocate", false,
                "error", e.getMessage()
            );
            return ResponseEntity.ok(validation);
        }
    }
    
    /**
     * Get system health check for allocations
     * HTTP GET: /api/allocations/health
     * 
     * @return ResponseEntity containing health status and HTTP status 200 (OK)
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.debug("REST request to check allocation service health");
        
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "RoomAllocationService",
            "timestamp", System.currentTimeMillis(),
            "transactionSupport", true
        );
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Get allocation summary by room
     * HTTP GET: /api/allocations/summary/room/{roomId}
     * 
     * @param roomId The room ID to get summary for
     * @return ResponseEntity containing room allocation summary and HTTP status 200 (OK)
     */
    @GetMapping("/summary/room/{roomId}")
    public ResponseEntity<Map<String, Object>> getRoomAllocationSummary(@PathVariable String roomId) {
        log.debug("REST request to get allocation summary for room {}", roomId);
        
        try {
            List<RoomAllocationService.AllocationInfo> allAllocations = roomAllocationService.getAllAllocations();
            
            // Filter allocations for the specified room
            List<RoomAllocationService.AllocationInfo> roomAllocations = allAllocations.stream()
                .filter(allocation -> allocation.getRoom() != null && roomId.equals(allocation.getRoom().getId()))
                .toList();
            
            Map<String, Object> summary = Map.of(
                "roomId", roomId,
                "allocatedUsers", roomAllocations.size(),
                "allocations", roomAllocations,
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            log.error("Failed to get room allocation summary: {}", e.getMessage());
            Map<String, Object> error = Map.of(
                "roomId", roomId,
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
