package com.example.myApp.service;

import com.example.myApp.entity.Bed;
import com.example.myApp.entity.Room;
import com.example.myApp.repository.BedRepository;
import com.example.myApp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * BedService - Business logic layer for Bed operations
 * This service handles all CRUD operations and business rules for Bed entities
 * 
 * @Service annotation indicates that this is a Spring service component
 * @RequiredArgsConstructor generates constructor with required fields (final fields)
 * @Slf4j provides logging capabilities
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BedService {
    
    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final RoomService roomService;
    
    /**
     * Create a new bed in the system
     * 
     * @param bed The bed object to create
     * @return The created bed with generated ID and timestamps
     * @throws IllegalArgumentException if room not found or bed number already exists in room
     */
    public Bed createBed(Bed bed) {
        log.info("Creating new bed with number: {} in room: {}", bed.getBedNumber(), bed.getRoomId());
        
        // Check if room exists
        Room room = roomRepository.findById(bed.getRoomId())
            .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + bed.getRoomId()));
        
        // Check if bed with same number already exists in the room
        if (bedRepository.existsByBedNumberAndRoomId(bed.getBedNumber(), bed.getRoomId())) {
            throw new IllegalArgumentException("Bed with number " + bed.getBedNumber() + 
                " already exists in room " + room.getRoomNumber());
        }
        
        // Check if room has capacity for more beds
        long currentBedCount = bedRepository.countByRoomId(bed.getRoomId());
        if (currentBedCount >= room.getCapacity()) {
            throw new IllegalArgumentException("Room " + room.getRoomNumber() + 
                " has reached its maximum capacity of " + room.getCapacity() + " beds");
        }
        
        // Set timestamps and initial values
        bed.setCreatedAt(LocalDateTime.now());
        bed.setUpdatedAt(LocalDateTime.now());
        bed.setOccupied(false);
        bed.setActive(true);
        bed.setAllocatedUserId(null);
        bed.setAllocatedAt(null);
        
        Bed savedBed = bedRepository.save(bed);
        log.info("Successfully created bed with ID: {}", savedBed.getId());
        
        // Update room occupancy statistics
        roomService.updateRoomOccupancy(bed.getRoomId());
        
        return savedBed;
    }
    
    /**
     * Get all beds from the system
     * 
     * @return List of all beds
     */
    public List<Bed> getAllBeds() {
        log.debug("Fetching all beds");
        return bedRepository.findAll();
    }
    
    /**
     * Get a bed by its ID
     * 
     * @param id The bed ID to search for
     * @return Optional containing the bed if found, empty otherwise
     */
    public Optional<Bed> getBedById(String id) {
        log.debug("Fetching bed with ID: {}", id);
        return bedRepository.findById(id);
    }
    
    /**
     * Get all beds in a specific room
     * 
     * @param roomId The room ID to search for
     * @return List of beds in the specified room
     */
    public List<Bed> getBedsByRoomId(String roomId) {
        log.debug("Fetching beds in room with ID: {}", roomId);
        return bedRepository.findByRoomId(roomId);
    }
    
    /**
     * Update an existing bed
     * 
     * @param id The ID of the bed to update
     * @param bedDetails The updated bed details
     * @return The updated bed
     * @throws IllegalArgumentException if bed not found or validation fails
     */
    public Bed updateBed(String id, Bed bedDetails) {
        log.info("Updating bed with ID: {}", id);
        
        Bed existingBed = bedRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Bed not found with ID: " + id));
        
        // Check if bed number is being changed and if new number already exists in the room
        if (!existingBed.getBedNumber().equals(bedDetails.getBedNumber()) && 
            bedRepository.existsByBedNumberAndRoomId(bedDetails.getBedNumber(), existingBed.getRoomId())) {
            throw new IllegalArgumentException("Bed with number " + bedDetails.getBedNumber() + 
                " already exists in this room");
        }
        
        // Update bed details (excluding allocation-related fields)
        existingBed.setBedNumber(bedDetails.getBedNumber());
        existingBed.setBedType(bedDetails.getBedType());
        existingBed.setPosition(bedDetails.getPosition());
        existingBed.setActive(bedDetails.isActive());
        existingBed.setNotes(bedDetails.getNotes());
        existingBed.setUpdatedAt(LocalDateTime.now());
        
        Bed updatedBed = bedRepository.save(existingBed);
        log.info("Successfully updated bed with ID: {}", updatedBed.getId());
        
        return updatedBed;
    }
    
    /**
     * Delete a bed by its ID
     * 
     * @param id The ID of the bed to delete
     * @throws IllegalArgumentException if bed not found or is occupied
     */
    public void deleteBed(String id) {
        log.info("Deleting bed with ID: {}", id);
        
        Bed bed = bedRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Bed not found with ID: " + id));
        
        // Prevent deletion if bed is occupied
        if (bed.isOccupied()) {
            throw new IllegalArgumentException("Cannot delete occupied bed. Please deallocate first.");
        }
        
        String roomId = bed.getRoomId();
        bedRepository.deleteById(id);
        log.info("Successfully deleted bed with ID: {}", id);
        
        // Update room occupancy statistics
        roomService.updateRoomOccupancy(roomId);
    }
    
    /**
     * Get all available beds (not occupied and active)
     * 
     * @return List of available beds
     */
    public List<Bed> getAvailableBeds() {
        log.debug("Fetching all available beds");
        return bedRepository.findByIsOccupiedFalseAndIsActiveTrue();
    }
    
    /**
     * Get available beds in a specific room
     * 
     * @param roomId The room ID to search within
     * @return List of available beds in the specified room
     */
    public List<Bed> getAvailableBedsInRoom(String roomId) {
        log.debug("Fetching available beds in room with ID: {}", roomId);
        return bedRepository.findByRoomIdAndIsOccupiedFalseAndIsActiveTrue(roomId);
    }
    
    /**
     * Get all occupied beds
     * 
     * @return List of occupied beds
     */
    public List<Bed> getOccupiedBeds() {
        log.debug("Fetching all occupied beds");
        return bedRepository.findByIsOccupiedTrue();
    }
    
    /**
     * Get the bed allocated to a specific user
     * 
     * @param userId The user ID to search for
     * @return Optional containing the bed if found, empty otherwise
     */
    public Optional<Bed> getBedByUserId(String userId) {
        log.debug("Fetching bed allocated to user with ID: {}", userId);
        return bedRepository.findByAllocatedUserId(userId);
    }
    
    /**
     * Get beds by their type
     * 
     * @param bedType The bed type to search for
     * @return List of beds with the specified type
     */
    public List<Bed> getBedsByType(String bedType) {
        log.debug("Fetching beds of type: {}", bedType);
        return bedRepository.findByBedType(bedType);
    }
    
    /**
     * Search beds by bed number or type (case-insensitive)
     * 
     * @param searchTerm The search term
     * @return List of beds matching the search criteria
     */
    public List<Bed> searchBeds(String searchTerm) {
        log.debug("Searching beds with term: {}", searchTerm);
        return bedRepository.findByBedNumberOrBedTypeIgnoreCase(searchTerm);
    }
    
    /**
     * Allocate a bed to a user
     * This method should only be called by the RoomAllocationService to ensure transaction consistency
     * 
     * @param bedId The ID of the bed to allocate
     * @param userId The ID of the user to allocate the bed to
     * @return The updated bed
     * @throws IllegalArgumentException if bed not found, not available, or already occupied
     */
    public Bed allocateBed(String bedId, String userId) {
        log.info("Allocating bed with ID: {} to user with ID: {}", bedId, userId);
        
        Bed bed = bedRepository.findById(bedId)
            .orElseThrow(() -> new IllegalArgumentException("Bed not found with ID: " + bedId));
        
        if (bed.isOccupied()) {
            throw new IllegalArgumentException("Bed is already occupied");
        }
        
        if (!bed.isActive()) {
            throw new IllegalArgumentException("Bed is not active and cannot be allocated");
        }
        
        // Allocate the bed
        bed.allocateToUser(userId);
        bed.setUpdatedAt(LocalDateTime.now());
        
        Bed savedBed = bedRepository.save(bed);
        log.info("Successfully allocated bed with ID: {} to user with ID: {}", bedId, userId);
        
        // Update room occupancy statistics
        roomService.updateRoomOccupancy(bed.getRoomId());
        
        return savedBed;
    }
    
    /**
     * Deallocate a bed from current user
     * This method should only be called by the RoomAllocationService to ensure transaction consistency
     * 
     * @param bedId The ID of the bed to deallocate
     * @return The updated bed
     * @throws IllegalArgumentException if bed not found
     */
    public Bed deallocateBed(String bedId) {
        log.info("Deallocating bed with ID: {}", bedId);
        
        Bed bed = bedRepository.findById(bedId)
            .orElseThrow(() -> new IllegalArgumentException("Bed not found with ID: " + bedId));
        
        String roomId = bed.getRoomId();
        String userId = bed.getAllocatedUserId();
        
        // Deallocate the bed
        bed.deallocate();
        bed.setUpdatedAt(LocalDateTime.now());
        
        Bed savedBed = bedRepository.save(bed);
        log.info("Successfully deallocated bed with ID: {} from user with ID: {}", bedId, userId);
        
        // Update room occupancy statistics
        roomService.updateRoomOccupancy(roomId);
        
        return savedBed;
    }
    
    /**
     * Get statistics about bed utilization
     * 
     * @return BedStatistics object containing utilization statistics
     */
    public BedStatistics getBedStatistics() {
        log.debug("Calculating bed statistics");
        
        long totalBeds = bedRepository.count();
        long occupiedBeds = bedRepository.countByIsOccupiedTrue();
        long availableBeds = bedRepository.countByIsOccupiedFalseAndIsActiveTrue();
        
        return new BedStatistics(totalBeds, occupiedBeds, availableBeds);
    }
    
    /**
     * Inner class to hold bed statistics
     */
    public static class BedStatistics {
        private final long totalBeds;
        private final long occupiedBeds;
        private final long availableBeds;
        
        public BedStatistics(long totalBeds, long occupiedBeds, long availableBeds) {
            this.totalBeds = totalBeds;
            this.occupiedBeds = occupiedBeds;
            this.availableBeds = availableBeds;
        }
        
        public long getTotalBeds() { return totalBeds; }
        public long getOccupiedBeds() { return occupiedBeds; }
        public long getAvailableBeds() { return availableBeds; }
        
        public double getOccupancyPercentage() {
            return totalBeds > 0 ? (double) occupiedBeds / totalBeds * 100 : 0;
        }
        
        public double getAvailabilityPercentage() {
            return totalBeds > 0 ? (double) availableBeds / totalBeds * 100 : 0;
        }
    }
}
