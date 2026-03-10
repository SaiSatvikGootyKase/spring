package com.example.myApp.repository;

import com.example.myApp.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Data access layer for User entity
 * This interface extends MongoRepository to provide CRUD operations for User entities
 * 
 * @Repository annotation indicates that this is a Spring Data repository
 * MongoRepository provides basic CRUD operations and query methods
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Find a user by their email address
     * Email should be unique for each user
     * 
     * @param email The email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find a user by their name
     * This can return multiple users if names are not unique
     * 
     * @param name The name to search for
     * @return List of users with the specified name
     */
    List<User> findByName(String name);
    
    /**
     * Find users who are currently allocated a bed
     * 
     * @return List of users who have allocated beds
     */
    List<User> findByIsAllocatedTrue();
    
    /**
     * Find users who are not currently allocated any bed
     * 
     * @return List of users without allocated beds
     */
    List<User> findByIsAllocatedFalse();
    
    /**
     * Find a user by their allocated bed ID
     * 
     * @param allocatedBedId The bed ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByAllocatedBedId(String allocatedBedId);
    
    /**
     * Find users allocated to a specific room
     * 
     * @param allocatedRoomId The room ID to search for
     * @return List of users allocated to the specified room
     */
    List<User> findByAllocatedRoomId(String allocatedRoomId);
    
    /**
     * Check if a user exists with the given email
     * 
     * @param email The email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Count the total number of users allocated to beds
     * 
     * @return Number of allocated users
     */
    long countByIsAllocatedTrue();
    
    /**
     * Count the total number of users not allocated to any bed
     * 
     * @return Number of unallocated users
     */
    long countByIsAllocatedFalse();
    
    /**
     * Custom query to find users by name or email (case-insensitive)
     * This uses a regular expression to perform case-insensitive search
     * 
     * @param searchTerm The search term to match against name or email
     * @return List of users matching the search criteria
     */
    @Query("{ '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'email': { '$regex': ?0, '$options': 'i' } } ] }")
    List<User> findByNameOrEmailIgnoreCase(String searchTerm);
    
    /**
     * Find users created after a specific date
     * 
     * @param date The date to compare against
     * @return List of users created after the specified date
     */
    List<User> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Find users updated after a specific date
     * 
     * @param date The date to compare against
     * @return List of users updated after the specified date
     */
    List<User> findByUpdatedAtAfter(java.time.LocalDateTime date);
}
