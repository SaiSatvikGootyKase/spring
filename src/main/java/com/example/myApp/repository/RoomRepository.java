package com.example.myApp.repository;

import com.example.myApp.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RoomRepository - Data access layer for Room entity
 * This interface extends MongoRepository to provide CRUD operations for Room entities
 * 
 * @Repository annotation indicates that this is a Spring Data repository
 * MongoRepository provides basic CRUD operations and query methods
 */
@Repository
public interface RoomRepository extends MongoRepository<Room, String> {
    
    /**
     * Find a room by its room number
     * Room numbers should be unique within the building
     * 
     * @param roomNumber The room number to search for
     * @return Optional containing the room if found, empty otherwise
     */
    Optional<Room> findByRoomNumber(String roomNumber);
    
    /**
     * Find rooms by their type/category
     * Examples: "Single", "Double", "Dormitory"
     * 
     * @param roomType The room type to search for
     * @return List of rooms with the specified type
     */
    List<Room> findByRoomType(String roomType);
    
    /**
     * Find rooms on a specific floor
     * 
     * @param floorNumber The floor number to search for
     * @return List of rooms on the specified floor
     */
    List<Room> findByFloorNumber(int floorNumber);
    
    /**
     * Find rooms that are currently active
     * Active rooms are available for bed allocation
     * 
     * @return List of active rooms
     */
    List<Room> findByIsActiveTrue();
    
    /**
     * Find rooms that are currently inactive
     * Inactive rooms are under maintenance or deactivated
     * 
     * @return List of inactive rooms
     */
    List<Room> findByIsActiveFalse();
    
    /**
     * Find rooms that are currently full (all beds occupied)
     * 
     * @return List of full rooms
     */
    List<Room> findByIsFullTrue();
    
    /**
     * Find rooms that have available beds
     * 
     * @return List of rooms with available beds
     */
    List<Room> findByIsFullFalse();
    
    /**
     * Find rooms with capacity greater than or equal to the specified value
     * 
     * @param capacity The minimum capacity to search for
     * @return List of rooms with capacity >= specified value
     */
    List<Room> findByCapacityGreaterThanEqual(int capacity);
    
    /**
     * Find rooms with capacity less than or equal to the specified value
     * 
     * @param capacity The maximum capacity to search for
     * @return List of rooms with capacity <= specified value
     */
    List<Room> findByCapacityLessThanEqual(int capacity);
    
    /**
     * Find active rooms with available beds
     * This is a common query for bed allocation
     * 
     * @return List of active rooms that are not full
     */
    List<Room> findByIsActiveTrueAndIsFullFalse();
    
    /**
     * Check if a room exists with the given room number
     * 
     * @param roomNumber The room number to check
     * @return true if room exists, false otherwise
     */
    boolean existsByRoomNumber(String roomNumber);
    
    /**
     * Count the total number of active rooms
     * 
     * @return Number of active rooms
     */
    long countByIsActiveTrue();
    
    /**
     * Count the total number of inactive rooms
     * 
     * @return Number of inactive rooms
     */
    long countByIsActiveFalse();
    
    /**
     * Count the total number of full rooms
     * 
     * @return Number of full rooms
     */
    long countByIsFullTrue();
    
    /**
     * Count the total number of rooms with available beds
     * 
     * @return Number of rooms with available beds
     */
    long countByIsFullFalse();
    
    /**
     * Custom query to find rooms by room number or type (case-insensitive)
     * This uses a regular expression to perform case-insensitive search
     * 
     * @param searchTerm The search term to match against room number or type
     * @return List of rooms matching the search criteria
     */
    @Query("{ '$or': [ { 'roomNumber': { '$regex': ?0, '$options': 'i' } }, { 'roomType': { '$regex': ?0, '$options': 'i' } } ] }")
    List<Room> findByRoomNumberOrRoomTypeIgnoreCase(String searchTerm);
    
    /**
     * Custom query to find rooms with available capacity
     * Finds rooms where occupiedBeds < capacity and room is active
     * 
     * @return List of rooms with available capacity
     */
    @Query("{ 'isActive': true, 'occupiedBeds': { '$lt': '$capacity' } }")
    List<Room> findRoomsWithAvailableCapacity();
    
    /**
     * Find rooms on a specific floor with available beds
     * 
     * @param floorNumber The floor number to search for
     * @return List of rooms on the specified floor with available beds
     */
    List<Room> findByFloorNumberAndIsActiveTrueAndIsFullFalse(int floorNumber);
    
    /**
     * Find rooms created after a specific date
     * 
     * @param date The date to compare against
     * @return List of rooms created after the specified date
     */
    List<Room> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Find rooms updated after a specific date
     * 
     * @param date The date to compare against
     * @return List of rooms updated after the specified date
     */
    List<Room> findByUpdatedAtAfter(java.time.LocalDateTime date);
}
