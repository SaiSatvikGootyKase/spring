package com.example.myApp.service;

import com.example.myApp.entity.Room;
import com.example.myApp.entity.Bed;
import com.example.myApp.repository.RoomRepository;
import com.example.myApp.repository.BedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * RoomService - Business logic layer for Room operations
 * This service handles all CRUD operations and business rules for Room entities
 * 
 * @Service annotation indicates that this is a Spring service component
 * @RequiredArgsConstructor generates constructor with required fields (final fields)
 * @Slf4j provides logging capabilities
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    
    /**
     * Create a new room in the system
     * 
     * @param room The room object to create
     * @return The created room with generated ID and timestamps
     * @throws IllegalArgumentException if room with same number already exists
     */
    public Room createRoom(Room room) {
        log.info("Creating new room with number: {}", room.getRoomNumber());
        
        // Check if room with same number already exists
        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new IllegalArgumentException("Room with number " + room.getRoomNumber() + " already exists");
        }
        
        // Validate room capacity
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Room capacity must be greater than 0");
        }
        
        // Set timestamps and initial values
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());
        room.setOccupiedBeds(0);
        room.setActive(true);
        room.setFull(false);
        
        Room savedRoom = roomRepository.save(room);
        log.info("Successfully created room with ID: {}", savedRoom.getId());
        
        // Automatically create beds for the room based on capacity
        createBedsForRoom(savedRoom);
        
        return savedRoom;
    }
    
    /**
     * Get all rooms from the system
     * 
     * @return List of all rooms
     */
    public List<Room> getAllRooms() {
        log.debug("Fetching all rooms");
        return roomRepository.findAll();
    }
    
    /**
     * Get a room by its ID
     * 
     * @param id The room ID to search for
     * @return Optional containing the room if found, empty otherwise
     */
    public Optional<Room> getRoomById(String id) {
        log.debug("Fetching room with ID: {}", id);
        return roomRepository.findById(id);
    }
    
    /**
     * Get a room by its room number
     * 
     * @param roomNumber The room number to search for
     * @return Optional containing the room if found, empty otherwise
     */
    public Optional<Room> getRoomByRoomNumber(String roomNumber) {
        log.debug("Fetching room with number: {}", roomNumber);
        return roomRepository.findByRoomNumber(roomNumber);
    }
    
    /**
     * Update an existing room
     * 
     * @param id The ID of the room to update
     * @param roomDetails The updated room details
     * @return The updated room
     * @throws IllegalArgumentException if room not found or validation fails
     */
    public Room updateRoom(String id, Room roomDetails) {
        log.info("Updating room with ID: {}", id);
        
        Room existingRoom = roomRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + id));
        
        // Check if room number is being changed and if new number already exists
        if (!existingRoom.getRoomNumber().equals(roomDetails.getRoomNumber()) && 
            roomRepository.existsByRoomNumber(roomDetails.getRoomNumber())) {
            throw new IllegalArgumentException("Room with number " + roomDetails.getRoomNumber() + " already exists");
        }
        
        // Validate new capacity
        if (roomDetails.getCapacity() <= 0) {
            throw new IllegalArgumentException("Room capacity must be greater than 0");
        }
        
        // Check if new capacity is less than currently occupied beds
        if (roomDetails.getCapacity() < existingRoom.getOccupiedBeds()) {
            throw new IllegalArgumentException("Cannot reduce room capacity below currently occupied beds (" + 
                existingRoom.getOccupiedBeds() + ")");
        }
        
        // Update room details
        existingRoom.setRoomNumber(roomDetails.getRoomNumber());
        existingRoom.setRoomType(roomDetails.getRoomType());
        existingRoom.setCapacity(roomDetails.getCapacity());
        existingRoom.setFloorNumber(roomDetails.getFloorNumber());
        existingRoom.setDescription(roomDetails.getDescription());
        existingRoom.setActive(roomDetails.isActive());
        existingRoom.setUpdatedAt(LocalDateTime.now());
        
        // Update full status based on new capacity and occupied beds
        existingRoom.setFull(existingRoom.getOccupiedBeds() >= existingRoom.getCapacity());
        
        Room updatedRoom = roomRepository.save(existingRoom);
        log.info("Successfully updated room with ID: {}", updatedRoom.getId());
        
        return updatedRoom;
    }
    
    /**
     * Delete a room by its ID
     * 
     * @param id The ID of the room to delete
     * @throws IllegalArgumentException if room not found or has occupied beds
     */
    public void deleteRoom(String id) {
        log.info("Deleting room with ID: {}", id);
        
        Room room = roomRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + id));
        
        // Check if room has occupied beds
        if (room.getOccupiedBeds() > 0) {
            throw new IllegalArgumentException("Cannot delete room with occupied beds. Please deallocate all beds first.");
        }
        
        // Delete all beds in the room first
        bedRepository.deleteByRoomId(id);
        
        roomRepository.deleteById(id);
        log.info("Successfully deleted room with ID: {}", id);
    }
    
    /**
     * Get all active rooms
     * 
     * @return List of active rooms
     */
    public List<Room> getActiveRooms() {
        log.debug("Fetching all active rooms");
        return roomRepository.findByIsActiveTrue();
    }
    
    /**
     * Get all inactive rooms
     * 
     * @return List of inactive rooms
     */
    public List<Room> getInactiveRooms() {
        log.debug("Fetching all inactive rooms");
        return roomRepository.findByIsActiveFalse();
    }
    
    /**
     * Get rooms with available beds
     * 
     * @return List of rooms that have available beds
     */
    public List<Room> getRoomsWithAvailableBeds() {
        log.debug("Fetching rooms with available beds");
        return roomRepository.findByIsActiveTrueAndIsFullFalse();
    }
    
    /**
     * Get rooms on a specific floor
     * 
     * @param floorNumber The floor number to search for
     * @return List of rooms on the specified floor
     */
    public List<Room> getRoomsByFloor(int floorNumber) {
        log.debug("Fetching rooms on floor: {}", floorNumber);
        return roomRepository.findByFloorNumber(floorNumber);
    }
    
    /**
     * Search rooms by room number or type (case-insensitive)
     * 
     * @param searchTerm The search term
     * @return List of rooms matching the search criteria
     */
    public List<Room> searchRooms(String searchTerm) {
        log.debug("Searching rooms with term: {}", searchTerm);
        return roomRepository.findByRoomNumberOrRoomTypeIgnoreCase(searchTerm);
    }
    
    /**
     * Update room occupancy statistics
     * This method should be called when beds are allocated or deallocated
     * 
     * @param roomId The room ID to update
     */
    public void updateRoomOccupancy(String roomId) {
        log.debug("Updating occupancy for room with ID: {}", roomId);
        
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));
        
        // Count occupied beds in the room
        long occupiedBedCount = bedRepository.countByRoomIdAndIsOccupiedTrue(roomId);
        
        // Update room occupancy
        room.setOccupiedBeds((int) occupiedBedCount);
        room.setFull(occupiedBedCount >= room.getCapacity());
        room.setUpdatedAt(LocalDateTime.now());
        
        roomRepository.save(room);
        log.debug("Updated occupancy for room {}: {}/{} beds occupied", 
            room.getRoomNumber(), occupiedBedCount, room.getCapacity());
    }
    
    /**
     * Get statistics about room utilization
     * 
     * @return RoomStatistics object containing utilization statistics
     */
    public RoomStatistics getRoomStatistics() {
        log.debug("Calculating room statistics");
        
        long totalRooms = roomRepository.count();
        long activeRooms = roomRepository.countByIsActiveTrue();
        long inactiveRooms = roomRepository.countByIsActiveFalse();
        long fullRooms = roomRepository.countByIsFullTrue();
        long roomsWithAvailableBeds = roomRepository.countByIsFullFalse();
        
        return new RoomStatistics(totalRooms, activeRooms, inactiveRooms, fullRooms, roomsWithAvailableBeds);
    }
    
    /**
     * Inner class to hold room statistics
     */
    public static class RoomStatistics {
        private final long totalRooms;
        private final long activeRooms;
        private final long inactiveRooms;
        private final long fullRooms;
        private final long roomsWithAvailableBeds;
        
        public RoomStatistics(long totalRooms, long activeRooms, long inactiveRooms, 
                             long fullRooms, long roomsWithAvailableBeds) {
            this.totalRooms = totalRooms;
            this.activeRooms = activeRooms;
            this.inactiveRooms = inactiveRooms;
            this.fullRooms = fullRooms;
            this.roomsWithAvailableBeds = roomsWithAvailableBeds;
        }
        
        public long getTotalRooms() { return totalRooms; }
        public long getActiveRooms() { return activeRooms; }
        public long getInactiveRooms() { return inactiveRooms; }
        public long getFullRooms() { return fullRooms; }
        public long getRoomsWithAvailableBeds() { return roomsWithAvailableBeds; }
        
        public double getActivePercentage() {
            return totalRooms > 0 ? (double) activeRooms / totalRooms * 100 : 0;
        }
        
        public double getFullPercentage() {
            return activeRooms > 0 ? (double) fullRooms / activeRooms * 100 : 0;
        }
    }
    
    /**
     * Create beds for a room based on its capacity
     * This method is called automatically when a new room is created
     * 
     * @param room The room for which to create beds
     */
    private void createBedsForRoom(Room room) {
        log.info("Creating {} beds for room with ID: {}", room.getCapacity(), room.getId());
        
        for (int i = 1; i <= room.getCapacity(); i++) {
            Bed bed = new Bed();
            bed.setBedNumber(room.getRoomNumber() + "-B" + String.format("%02d", i));
            bed.setRoomId(room.getId());
            bed.setBedType(room.getRoomType());
            bed.setPosition("Bed " + i);
            bed.setOccupied(false);
            bed.setActive(true);
            bed.setCreatedAt(LocalDateTime.now());
            bed.setUpdatedAt(LocalDateTime.now());
            
            bedRepository.save(bed);
            log.debug("Created bed {} for room {}", bed.getBedNumber(), room.getRoomNumber());
        }
        
        log.info("Successfully created {} beds for room {}", room.getCapacity(), room.getRoomNumber());
    }
}
