package com.example.myApp.controller;

import com.example.myApp.entity.Bed;
import com.example.myApp.service.BedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * BedController - REST API endpoints for Bed operations
 * This controller provides HTTP endpoints for managing beds in the room allocation system
 * 
 * @RestController annotation combines @Controller and @ResponseBody
 * @RequestMapping specifies the base path for all endpoints in this controller
 * @RequiredArgsConstructor generates constructor with required fields (final fields)
 * @Slf4j provides logging capabilities
 */
@RestController
@RequestMapping("/api/beds")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend
@Slf4j
public class BedController {
    
    private final BedService bedService;
    
    /**
     * Create a new bed
     * HTTP POST: /api/beds
     * 
     * @param bed The bed object to create (request body)
     * @return ResponseEntity containing the created bed and HTTP status 201 (CREATED)
     */
    @PostMapping
    public ResponseEntity<Bed> createBed(@RequestBody Bed bed) {
        log.info("REST request to create bed: {} in room: {}", bed.getBedNumber(), bed.getRoomId());
        try {
            Bed createdBed = bedService.createBed(bed);
            return new ResponseEntity<>(createdBed, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create bed: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Get all beds
     * HTTP GET: /api/beds
     * 
     * @return ResponseEntity containing list of all beds and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<Bed>> getAllBeds() {
        log.debug("REST request to get all beds");
        List<Bed> beds = bedService.getAllBeds();
        return ResponseEntity.ok(beds);
    }
    
    /**
     * Get a bed by ID
     * HTTP GET: /api/beds/{id}
     * 
     * @param id The bed ID to search for
     * @return ResponseEntity containing the bed if found, HTTP status 404 (NOT_FOUND) otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Bed> getBedById(@PathVariable String id) {
        log.debug("REST request to get bed with ID: {}", id);
        Optional<Bed> bed = bedService.getBedById(id);
        return bed.map(ResponseEntity::ok)
                  .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Get all beds in a specific room
     * HTTP GET: /api/beds/room/{roomId}
     * 
     * @param roomId The room ID to search for
     * @return ResponseEntity containing list of beds in the specified room and HTTP status 200 (OK)
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Bed>> getBedsByRoomId(@PathVariable String roomId) {
        log.debug("REST request to get beds in room with ID: {}", roomId);
        List<Bed> beds = bedService.getBedsByRoomId(roomId);
        return ResponseEntity.ok(beds);
    }
    
    /**
     * Update an existing bed
     * HTTP PUT: /api/beds/{id}
     * 
     * @param id The ID of the bed to update
     * @param bedDetails The updated bed details (request body)
     * @return ResponseEntity containing the updated bed and HTTP status 200 (OK),
     *         or HTTP status 404 (NOT_FOUND) if bed not found,
     *         or HTTP status 400 (BAD_REQUEST) if validation fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<Bed> updateBed(@PathVariable String id, @RequestBody Bed bedDetails) {
        log.info("REST request to update bed with ID: {}", id);
        try {
            Bed updatedBed = bedService.updateBed(id, bedDetails);
            return ResponseEntity.ok(updatedBed);
        } catch (IllegalArgumentException e) {
            log.error("Failed to update bed: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Delete a bed
     * HTTP DELETE: /api/beds/{id}
     * 
     * @param id The ID of the bed to delete
     * @return ResponseEntity with HTTP status 204 (NO_CONTENT) if successful,
     *         or HTTP status 404 (NOT_FOUND) if bed not found,
     *         or HTTP status 400 (BAD_REQUEST) if bed is occupied
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBed(@PathVariable String id) {
        log.info("REST request to delete bed with ID: {}", id);
        try {
            bedService.deleteBed(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete bed: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Get all available beds
     * HTTP GET: /api/beds/available
     * 
     * @return ResponseEntity containing list of available beds and HTTP status 200 (OK)
     */
    @GetMapping("/available")
    public ResponseEntity<List<Bed>> getAvailableBeds() {
        log.debug("REST request to get all available beds");
        List<Bed> beds = bedService.getAvailableBeds();
        return ResponseEntity.ok(beds);
    }
    
    /**
     * Get available beds in a specific room
     * HTTP GET: /api/beds/available/room/{roomId}
     * 
     * @param roomId The room ID to search within
     * @return ResponseEntity containing list of available beds in the specified room and HTTP status 200 (OK)
     */
    @GetMapping("/available/room/{roomId}")
    public ResponseEntity<List<Bed>> getAvailableBedsInRoom(@PathVariable String roomId) {
        log.debug("REST request to get available beds in room with ID: {}", roomId);
        List<Bed> beds = bedService.getAvailableBedsInRoom(roomId);
        return ResponseEntity.ok(beds);
    }
    
    /**
     * Get all occupied beds
     * HTTP GET: /api/beds/occupied
     * 
     * @return ResponseEntity containing list of occupied beds and HTTP status 200 (OK)
     */
    @GetMapping("/occupied")
    public ResponseEntity<List<Bed>> getOccupiedBeds() {
        log.debug("REST request to get all occupied beds");
        List<Bed> beds = bedService.getOccupiedBeds();
        return ResponseEntity.ok(beds);
    }
    
    /**
     * Get the bed allocated to a specific user
     * HTTP GET: /api/beds/user/{userId}
     * 
     * @param userId The user ID to search for
     * @return ResponseEntity containing the bed if found, HTTP status 404 (NOT_FOUND) otherwise
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Bed> getBedByUserId(@PathVariable String userId) {
        log.debug("REST request to get bed allocated to user with ID: {}", userId);
        Optional<Bed> bed = bedService.getBedByUserId(userId);
        return bed.map(ResponseEntity::ok)
                  .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Get beds by their type
     * HTTP GET: /api/beds/type/{bedType}
     * 
     * @param bedType The bed type to search for
     * @return ResponseEntity containing list of beds with the specified type and HTTP status 200 (OK)
     */
    @GetMapping("/type/{bedType}")
    public ResponseEntity<List<Bed>> getBedsByType(@PathVariable String bedType) {
        log.debug("REST request to get beds of type: {}", bedType);
        List<Bed> beds = bedService.getBedsByType(bedType);
        return ResponseEntity.ok(beds);
    }
    
    /**
     * Search beds by bed number or type
     * HTTP GET: /api/beds/search?term={searchTerm}
     * 
     * @param searchTerm The search term (query parameter)
     * @return ResponseEntity containing list of matching beds and HTTP status 200 (OK)
     */
    @GetMapping("/search")
    public ResponseEntity<List<Bed>> searchBeds(@RequestParam String term) {
        log.debug("REST request to search beds with term: {}", term);
        List<Bed> beds = bedService.searchBeds(term);
        return ResponseEntity.ok(beds);
    }
    
    /**
     * Get bed statistics
     * HTTP GET: /api/beds/statistics
     * 
     * @return ResponseEntity containing bed statistics and HTTP status 200 (OK)
     */
    @GetMapping("/statistics")
    public ResponseEntity<BedService.BedStatistics> getBedStatistics() {
        log.debug("REST request to get bed statistics");
        BedService.BedStatistics statistics = bedService.getBedStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Get system health check for beds
     * HTTP GET: /api/beds/health
     * 
     * @return ResponseEntity containing health status and HTTP status 200 (OK)
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.debug("REST request to check bed service health");
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "BedService",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }
}
