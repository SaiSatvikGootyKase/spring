package com.example.myApp.controller;

import com.example.myApp.entity.User;
import com.example.myApp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * UserController - REST API endpoints for User operations
 * This controller provides HTTP endpoints for managing users in the room allocation system
 * 
 * @RestController annotation combines @Controller and @ResponseBody
 * @RequestMapping specifies the base path for all endpoints in this controller
 * @RequiredArgsConstructor generates constructor with required fields (final fields)
 * @Slf4j provides logging capabilities
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Allow React frontend
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    /**
     * Create a new user
     * HTTP POST: /api/users
     * 
     * @param user The user object to create (request body)
     * @return ResponseEntity containing the created user and HTTP status 201 (CREATED)
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("REST request to create user: {}", user.getEmail());
        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create user: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Get all users
     * HTTP GET: /api/users
     * 
     * @return ResponseEntity containing list of all users and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("REST request to get all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get a user by ID
     * HTTP GET: /api/users/{id}
     * 
     * @param id The user ID to search for
     * @return ResponseEntity containing the user if found, HTTP status 404 (NOT_FOUND) otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        log.debug("REST request to get user with ID: {}", id);
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Get a user by email
     * HTTP GET: /api/users/email/{email}
     * 
     * @param email The email address to search for
     * @return ResponseEntity containing the user if found, HTTP status 404 (NOT_FOUND) otherwise
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        log.debug("REST request to get user with email: {}", email);
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                  .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Update an existing user
     * HTTP PUT: /api/users/{id}
     * 
     * @param id The ID of the user to update
     * @param userDetails The updated user details (request body)
     * @return ResponseEntity containing the updated user and HTTP status 200 (OK),
     *         or HTTP status 404 (NOT_FOUND) if user not found,
     *         or HTTP status 400 (BAD_REQUEST) if validation fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails) {
        log.info("REST request to update user with ID: {}", id);
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            log.error("Failed to update user: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Delete a user
     * HTTP DELETE: /api/users/{id}
     * 
     * @param id The ID of the user to delete
     * @return ResponseEntity with HTTP status 204 (NO_CONTENT) if successful,
     *         or HTTP status 404 (NOT_FOUND) if user not found,
     *         or HTTP status 400 (BAD_REQUEST) if user is allocated to a bed
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("REST request to delete user with ID: {}", id);
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            log.error("Failed to delete user: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Get all allocated users
     * HTTP GET: /api/users/allocated
     * 
     * @return ResponseEntity containing list of allocated users and HTTP status 200 (OK)
     */
    @GetMapping("/allocated")
    public ResponseEntity<List<User>> getAllocatedUsers() {
        log.debug("REST request to get all allocated users");
        List<User> users = userService.getAllocatedUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get all unallocated users
     * HTTP GET: /api/users/unallocated
     * 
     * @return ResponseEntity containing list of unallocated users and HTTP status 200 (OK)
     */
    @GetMapping("/unallocated")
    public ResponseEntity<List<User>> getUnallocatedUsers() {
        log.debug("REST request to get all unallocated users");
        List<User> users = userService.getUnallocatedUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Search users by name or email
     * HTTP GET: /api/users/search?term={searchTerm}
     * 
     * @param searchTerm The search term (query parameter)
     * @return ResponseEntity containing list of matching users and HTTP status 200 (OK)
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String term) {
        log.debug("REST request to search users with term: {}", term);
        List<User> users = userService.searchUsers(term);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get users allocated to a specific room
     * HTTP GET: /api/users/room/{roomId}
     * 
     * @param roomId The room ID to search for
     * @return ResponseEntity containing list of users in the specified room and HTTP status 200 (OK)
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<User>> getUsersInRoom(@PathVariable String roomId) {
        log.debug("REST request to get users in room with ID: {}", roomId);
        List<User> users = userService.getUsersInRoom(roomId);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get user statistics
     * HTTP GET: /api/users/statistics
     * 
     * @return ResponseEntity containing user statistics and HTTP status 200 (OK)
     */
    @GetMapping("/statistics")
    public ResponseEntity<UserService.UserStatistics> getUserStatistics() {
        log.debug("REST request to get user statistics");
        UserService.UserStatistics statistics = userService.getUserStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * Get system health check for users
     * HTTP GET: /api/users/health
     * 
     * @return ResponseEntity containing health status and HTTP status 200 (OK)
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.debug("REST request to check user service health");
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "UserService",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }
}
