package com.example.myApp.controller;

import com.example.myApp.entity.Room;
import com.example.myApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * RoomController - REST API endpoints for Room operations
 * This controller provides HTTP endpoints for managing rooms in the room allocation system
 * 
 * @RestController annotation combines @Controller and @ResponseBody
 * @RequestMapping specifies the base path for all endpoints in this controller
 * @RequiredArgsConstructor generates constructor with required fields (final fields)
 * @Slf4j provides logging capabilities
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend
@Slf4j
public class RoomController {
    
    private final RoomService roomService;
    
    /**
     * Create a new room
     * HTTP POST: /api/rooms
     * 
     * @param room The room object to create (request body)
     * @return ResponseEntity containing the created room and HTTP status 201 (CREATED)
     */
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        log.info("REST request to create room: {}", room.getRoomNumber());
        try {
            Room createdRoom = roomService.createRoom(room);
            return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create room: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Get all rooms
     * HTTP GET: /api/rooms
     * 
     * @return ResponseEntity containing list of all rooms and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        log.debug("REST request to get all rooms");
        List<Room> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }
    
    /**
     * Get a room by ID
     * HTTP GET: /api/rooms/{id}
     * 
     * @param id The room ID to search for
     * @return ResponseEntity containing the room if found, HTTP status 404 (NOT_FOUND) otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable String id) {
        log.debug("REST request to get room with ID: {}", id);
        Optional<Room> room = roomService.getRoomById(id);
        return room.map(ResponseEntity::ok)
                  .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Get a room by room number
     * HTTP GET: /api/rooms/number/{roomNumber}
     * 
     * @param roomNumber The room number to search for
     * @return ResponseEntity containing the room if found, HTTP status 404 (NOT_FOUND) otherwise
     */
    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<Room> getRoomByRoomNumber(@PathVariable String roomNumber) {
        log.debug("REST request to get room with number: {}", roomNumber);
        Optional<Room> room = roomService.getRoomByRoomNumber(roomNumber);
        return room.map(ResponseEntity::ok)
                  .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Update an existing room
     * HTTP PUT: /api/rooms/{id}
     * 
     * @param id The ID of the room to update
     * @param roomDetails The updated room details (request body)
     * @return ResponseEntity containing the updated room and HTTP status 200 (OK),
     *         or HTTP status 404 (NOT_FOUND) if room not found,
     *         or HTTP status 400 (BAD_REQUEST) if validation fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable String id, @RequestBody Room roomDetails) {
        log.info("REST request to update room with ID: {}", id);
        try {
            Room updatedRoom = roomService.updateRoom(id, roomDetails);
            return ResponseEntity.ok(updatedRoom);
        } catch (IllegalArgumentException e) {
            log.error("Failed to update room: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Delete a room
     * HTTP DELETE: /api/rooms/{id}
     * 
     * @param id The ID of the room to delete
     * @return ResponseEntity with HTTP status 204 (NO_CONTENT) if successful,
     *         or HTTP status 404 (NOT_FOUND) if room not found,
     *         or HTTP status 400 (BAD_REQUEST) if room has occupied beds
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String id) {
        log.info("REST request to delete room with ID: {}", id);
        try {
            roomService.deleteRoom(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete room: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Get all active rooms
     * HTTP GET: /api/rooms/active
     * 
     * @return ResponseEntity containing list of active rooms and HTTP status 200 (OK)
     */
    @GetMapping("/active")
    public ResponseEntity<List<Room>> getActiveRooms() {
        log.debug("REST request to get all active rooms");
        List<Room> rooms = roomService.getActiveRooms();
        return ResponseEntity.ok(rooms);
    }
    
    /**
     * Get all inactive rooms
     * HTTP GET: /api/rooms/inactive
     * 
     * @return ResponseEntity containing list of inactive rooms and HTTP status 200 (OK)
     */
    @GetMapping("/inactive")
    public ResponseEntity<List<Room>> getInactiveRooms() {
        log.debug("REST request to get all inactive rooms");
        List<Room> rooms = roomService.getInactiveRooms();
        return ResponseEntity.ok(rooms);
    }
    
    /**
     * Get rooms with available beds
     * HTTP GET: /api/rooms/available
     * 
     * @return ResponseEntity containing list of rooms with available beds and HTTP status 200 (OK)
     */
    @GetMapping("/available")
    public ResponseEntity<List<Room>> getRoomsWithAvailableBeds() {
        log.debug("REST request to get rooms with available beds");
        List<Room> rooms = roomService.getRoomsWithAvailableBeds();
        return ResponseEntity.ok(rooms);
    }
    
    /**
     * Get rooms on a specific floor
     * HTTP GET: /api/rooms/floor/{floorNumber}
     * 
     * @param floorNumber The floor number to search for
     * @return ResponseEntity containing list of rooms on the specified floor and HTTP status 200 (OK)
     */
    @GetMapping("/floor/{floorNumber}")
    public ResponseEntity<List<Room>> getRoomsByFloor(@PathVariable int floorNumber) {
        log.debug("REST request to get rooms on floor: {}", floorNumber);
        List<Room> rooms = roomService.getRoomsByFloor(floorNumber);
        return ResponseEntity.ok(rooms);
    }
    
    /**
     * Search rooms by room number or type
     * HTTP GET: /api/rooms/search?term={searchTerm}
     * 
     * @param searchTerm The search term (query parameter)
     * @return ResponseEntity containing list of matching rooms and HTTP status 200 (OK)
     */
    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam String term) {
        log.debug("REST request to search rooms with term: {}", term);
        List<Room> rooms = roomService.searchRooms(term);
        return ResponseEntity.ok(rooms);
    }
    
    /**
     * Get room statistics
     * HTTP GET: /api/rooms/statistics
     * 
     * @return ResponseEntity containing room statistics and HTTP status 200 (OK)
     */
    @GetMapping("/statistics")
    public ResponseEntity<RoomService.RoomStatistics> getRoomStatistics() {
        log.debug("REST request to get room statistics");
        RoomService.RoomStatistics statistics = roomService.getRoomStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Update room occupancy statistics
     * HTTP POST: /api/rooms/{id}/update-occupancy
     * 
     * @param id The room ID to update occupancy for
     * @return ResponseEntity with HTTP status 200 (OK) if successful,
     *         or HTTP status 404 (NOT_FOUND) if room not found
     */
    @PostMapping("/{id}/update-occupancy")
    public ResponseEntity<Void> updateRoomOccupancy(@PathVariable String id) {
        log.info("REST request to update occupancy for room with ID: {}", id);
        try {
            roomService.updateRoomOccupancy(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Failed to update room occupancy: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Get system health check for rooms
     * HTTP GET: /api/rooms/health
     * 
     * @return ResponseEntity containing health status and HTTP status 200 (OK)
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.debug("REST request to check room service health");
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "RoomService",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }
}
