package com.example.myApp.repository;

import com.example.myApp.entity.Bed;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * BedRepository - Data access layer for Bed entity
 * This interface extends MongoRepository to provide CRUD operations for Bed entities
 * 
 * @Repository annotation indicates that this is a Spring Data repository
 * MongoRepository provides basic CRUD operations and query methods
 */
@Repository
public interface BedRepository extends MongoRepository<Bed, String> {
    
    /**
     * Find a bed by its bed number within a specific room
     * Bed numbers should be unique within each room
     * 
     * @param bedNumber The bed number to search for
     * @param roomId The room ID to search within
     * @return Optional containing the bed if found, empty otherwise
     */
    Optional<Bed> findByBedNumberAndRoomId(String bedNumber, String roomId);
    
    /**
     * Find all beds in a specific room
     * 
     * @param roomId The room ID to search for
     * @return List of beds in the specified room
     */
    List<Bed> findByRoomId(String roomId);
    
    /**
     * Find beds by their type/category
     * Examples: "Single", "Double", "Bunk-Bed-Upper", "Bunk-Bed-Lower"
     * 
     * @param bedType The bed type to search for
     * @return List of beds with the specified type
     */
    List<Bed> findByBedType(String bedType);
    
    /**
     * Find beds by their position within the room
     * Examples: "Near-Window", "Near-Door", "Corner", "Center"
     * 
     * @param position The position to search for
     * @return List of beds with the specified position
     */
    List<Bed> findByPosition(String position);
    
    /**
     * Find beds that are currently occupied
     * 
     * @return List of occupied beds
     */
    List<Bed> findByIsOccupiedTrue();
    
    /**
     * Find beds that are currently available (not occupied)
     * 
     * @return List of available beds
     */
    List<Bed> findByIsOccupiedFalse();
    
    /**
     * Find beds that are currently active
     * Active beds are available for allocation
     * 
     * @return List of active beds
     */
    List<Bed> findByIsActiveTrue();
    
    /**
     * Find beds that are currently inactive
     * Inactive beds are under maintenance or deactivated
     * 
     * @return List of inactive beds
     */
    List<Bed> findByIsActiveFalse();
    
    /**
     * Find the bed allocated to a specific user
     * 
     * @param allocatedUserId The user ID to search for
     * @return Optional containing the bed if found, empty otherwise
     */
    Optional<Bed> findByAllocatedUserId(String allocatedUserId);
    
    /**
     * Find available beds in a specific room
     * This combines room ID, occupancy status, and active status
     * 
     * @param roomId The room ID to search within
     * @return List of available beds in the specified room
     */
    List<Bed> findByRoomIdAndIsOccupiedFalseAndIsActiveTrue(String roomId);
    
    /**
     * Find all available beds across all rooms
     * 
     * @return List of all available beds
     */
    List<Bed> findByIsOccupiedFalseAndIsActiveTrue();
    
    /**
     * Find occupied beds in a specific room
     * 
     * @param roomId The room ID to search within
     * @return List of occupied beds in the specified room
     */
    List<Bed> findByRoomIdAndIsOccupiedTrue(String roomId);
    
    /**
     * Count the total number of beds in a specific room
     * 
     * @param roomId The room ID to count beds for
     * @return Number of beds in the specified room
     */
    long countByRoomId(String roomId);
    
    /**
     * Count the number of occupied beds in a specific room
     * 
     * @param roomId The room ID to count occupied beds for
     * @return Number of occupied beds in the specified room
     */
    long countByRoomIdAndIsOccupiedTrue(String roomId);
    
    /**
     * Count the number of available beds in a specific room
     * 
     * @param roomId The room ID to count available beds for
     * @return Number of available beds in the specified room
     */
    long countByRoomIdAndIsOccupiedFalseAndIsActiveTrue(String roomId);
    
    /**
     * Count the total number of occupied beds
     * 
     * @return Number of occupied beds
     */
    long countByIsOccupiedTrue();
    
    /**
     * Count the total number of available beds
     * 
     * @return Number of available beds
     */
    long countByIsOccupiedFalseAndIsActiveTrue();
    
    /**
     * Check if a bed exists with the given bed number in a specific room
     * 
     * @param bedNumber The bed number to check
     * @param roomId The room ID to check within
     * @return true if bed exists, false otherwise
     */
    boolean existsByBedNumberAndRoomId(String bedNumber, String roomId);
    
    /**
     * Delete all beds associated with a specific room
     * This method is used when deleting a room to maintain data consistency
     * 
     * @param roomId The room ID whose beds should be deleted
     */
    void deleteByRoomId(String roomId);
    
    /**
     * Custom query to find beds by bed number or type (case-insensitive)
     * This uses a regular expression to perform case-insensitive search
     * 
     * @param searchTerm The search term to match against bed number or type
     * @return List of beds matching the search criteria
     */
    @Query("{ '$or': [ { 'bedNumber': { '$regex': ?0, '$options': 'i' } }, { 'bedType': { '$regex': ?0, '$options': 'i' } } ] }")
    List<Bed> findByBedNumberOrBedTypeIgnoreCase(String searchTerm);
    
    /**
     * Find beds allocated after a specific date
     * 
     * @param date The date to compare against
     * @return List of beds allocated after the specified date
     */
    List<Bed> findByAllocatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Find beds created after a specific date
     * 
     * @param date The date to compare against
     * @return List of beds created after the specified date
     */
    List<Bed> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Find beds updated after a specific date
     * 
     * @param date The date to compare against
     * @return List of beds updated after the specified date
     */
    List<Bed> findByUpdatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Custom query to find beds with specific criteria
     * Finds beds that are active, not occupied, and in active rooms
     * This is useful for bed allocation
     * 
     * @return List of beds available for allocation
     */
    @Query("{ 'isActive': true, 'isOccupied': false }")
    List<Bed> findBedsAvailableForAllocation();
}
