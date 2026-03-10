package com.example.myApp.service;

import com.example.myApp.entity.User;
import com.example.myApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * UserService - Business logic layer for User operations
 * This service handles all CRUD operations and business rules for User entities
 * 
 * @Service annotation indicates that this is a Spring service component
 * @RequiredArgsConstructor generates constructor with required fields (final fields)
 * @Slf4j provides logging capabilities
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * Create a new user in the system
     * 
     * @param user The user object to create
     * @return The created user with generated ID and timestamps
     * @throws IllegalArgumentException if user with same email already exists
     */
    public User createUser(User user) {
        log.info("Creating new user with email: {}", user.getEmail());
        
        // Check if user with same email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        
        // Set timestamps and initial values
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setAllocated(false);
        user.setAllocatedBedId(null);
        user.setAllocatedRoomId(null);
        
        User savedUser = userRepository.save(user);
        log.info("Successfully created user with ID: {}", savedUser.getId());
        
        return savedUser;
    }
    
    /**
     * Get all users from the system
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }
    
    /**
     * Get a user by their ID
     * 
     * @param id The user ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<User> getUserById(String id) {
        log.debug("Fetching user with ID: {}", id);
        return userRepository.findById(id);
    }
    
    /**
     * Get a user by their email address
     * 
     * @param email The email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<User> getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        return userRepository.findByEmail(email);
    }
    
    /**
     * Update an existing user
     * 
     * @param id The ID of the user to update
     * @param userDetails The updated user details
     * @return The updated user
     * @throws IllegalArgumentException if user not found or email already exists
     */
    public User updateUser(String id, User userDetails) {
        log.info("Updating user with ID: {}", id);
        
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        
        // Check if email is being changed and if new email already exists
        if (!existingUser.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDetails.getEmail() + " already exists");
        }
        
        // Update user details (excluding allocation-related fields)
        existingUser.setName(userDetails.getName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated user with ID: {}", updatedUser.getId());
        
        return updatedUser;
    }
    
    /**
     * Delete a user by their ID
     * 
     * @param id The ID of the user to delete
     * @throws IllegalArgumentException if user not found or is allocated to a bed
     */
    public void deleteUser(String id) {
        log.info("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
        
        // Prevent deletion if user is allocated to a bed
        if (user.isAllocated()) {
            throw new IllegalArgumentException("Cannot delete user who is allocated to a bed. Please deallocate first.");
        }
        
        userRepository.deleteById(id);
        log.info("Successfully deleted user with ID: {}", id);
    }
    
    /**
     * Get all users who are currently allocated to beds
     * 
     * @return List of allocated users
     */
    public List<User> getAllocatedUsers() {
        log.debug("Fetching all allocated users");
        return userRepository.findByIsAllocatedTrue();
    }
    
    /**
     * Get all users who are not allocated to any bed
     * 
     * @return List of unallocated users
     */
    public List<User> getUnallocatedUsers() {
        log.debug("Fetching all unallocated users");
        return userRepository.findByIsAllocatedFalse();
    }
    
    /**
     * Search users by name or email (case-insensitive)
     * 
     * @param searchTerm The search term
     * @return List of users matching the search criteria
     */
    public List<User> searchUsers(String searchTerm) {
        log.debug("Searching users with term: {}", searchTerm);
        return userRepository.findByNameOrEmailIgnoreCase(searchTerm);
    }
    
    /**
     * Get users allocated to a specific room
     * 
     * @param roomId The room ID to search for
     * @return List of users allocated to the specified room
     */
    public List<User> getUsersInRoom(String roomId) {
        log.debug("Fetching users in room with ID: {}", roomId);
        return userRepository.findByAllocatedRoomId(roomId);
    }
    
    /**
     * Get statistics about user allocation
     * 
     * @return UserStatistics object containing allocation statistics
     */
    public UserStatistics getUserStatistics() {
        log.debug("Calculating user statistics");
        
        long totalUsers = userRepository.count();
        long allocatedUsers = userRepository.countByIsAllocatedTrue();
        long unallocatedUsers = userRepository.countByIsAllocatedFalse();
        
        return new UserStatistics(totalUsers, allocatedUsers, unallocatedUsers);
    }
    
    /**
     * Inner class to hold user statistics
     */
    public static class UserStatistics {
        private final long totalUsers;
        private final long allocatedUsers;
        private final long unallocatedUsers;
        
        public UserStatistics(long totalUsers, long allocatedUsers, long unallocatedUsers) {
            this.totalUsers = totalUsers;
            this.allocatedUsers = allocatedUsers;
            this.unallocatedUsers = unallocatedUsers;
        }
        
        public long getTotalUsers() { return totalUsers; }
        public long getAllocatedUsers() { return allocatedUsers; }
        public long getUnallocatedUsers() { return unallocatedUsers; }
        
        public double getAllocationPercentage() {
            return totalUsers > 0 ? (double) allocatedUsers / totalUsers * 100 : 0;
        }
    }
}
