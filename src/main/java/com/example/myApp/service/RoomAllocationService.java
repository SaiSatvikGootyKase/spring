package com.example.myApp.service;

import com.example.myApp.entity.User;
import com.example.myApp.entity.Bed;
import com.example.myApp.entity.Room;
import com.example.myApp.repository.UserRepository;
import com.example.myApp.repository.BedRepository;
import com.example.myApp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * RoomAllocationService - Handles bed allocation operations with transaction management
 * This service ensures that both User and Bed entities are updated atomically
 * 
 * @Service annotation indicates that this is a Spring service component
 * @RequiredArgsConstructor generates constructor with required fields (final fields)
 * @Slf4j provides logging capabilities
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomAllocationService {
    
    private final UserRepository userRepository;
    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final UserService userService;
    private final BedService bedService;
    private final RoomService roomService;
    
    /**
     * Allocate a bed to a user with transaction management
     * This method ensures that both User and Bed are updated atomically
     * If any operation fails, all changes are rolled back
     * 
     * @param userId The ID of the user to allocate the bed to
     * @param bedId The ID of the bed to allocate
     * @return AllocationResult containing the updated user and bed
     * @throws IllegalArgumentException if allocation is not possible
     */
    @Transactional
    public AllocationResult allocateBedToUser(String userId, String bedId) {
        log.info("Starting allocation of bed {} to user {}", bedId, userId);
        
        // Validate user exists and is not already allocated
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        if (user.isAllocated()) {
            throw new IllegalArgumentException("User is already allocated to bed: " + user.getAllocatedBedId());
        }
        
        // Validate bed exists and is available
        Bed bed = bedRepository.findById(bedId)
            .orElseThrow(() -> new IllegalArgumentException("Bed not found with ID: " + bedId));
        
        if (!bed.isAvailable()) {
            throw new IllegalArgumentException("Bed is not available for allocation. Status: " + 
                (bed.isOccupied() ? "Occupied" : "Inactive"));
        }
        
        // Validate room exists and is active
        Room room = roomRepository.findById(bed.getRoomId())
            .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + bed.getRoomId()));
        
        if (!room.isActive()) {
            throw new IllegalArgumentException("Room is not active: " + room.getRoomNumber());
        }
        
        try {
            // Update user allocation details
            user.setAllocated(true);
            user.setAllocatedBedId(bedId);
            user.setAllocatedRoomId(bed.getRoomId());
            user.setUpdatedAt(LocalDateTime.now());
            
            // Update bed allocation details
            bed.allocateToUser(userId);
            bed.setUpdatedAt(LocalDateTime.now());
            
            // Save both entities
            User savedUser = userRepository.save(user);
            Bed savedBed = bedRepository.save(bed);
            
            // Update room occupancy statistics
            roomService.updateRoomOccupancy(bed.getRoomId());
            
            log.info("Successfully allocated bed {} to user {}", bedId, userId);
            
            return new AllocationResult(savedUser, savedBed, room, "Bed allocated successfully");
            
        } catch (Exception e) {
            log.error("Failed to allocate bed {} to user {}: {}", bedId, userId, e.getMessage());
            throw new RuntimeException("Allocation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Deallocate a bed from a user with transaction management
     * This method ensures that both User and Bed are updated atomically
     * 
     * @param userId The ID of the user to deallocate
     * @return DeallocationResult containing the updated user and bed
     * @throws IllegalArgumentException if deallocation is not possible
     */
    @Transactional
    public DeallocationResult deallocateBedFromUser(String userId) {
        log.info("Starting deallocation for user {}", userId);
        
        // Validate user exists and is allocated
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        if (!user.isAllocated()) {
            throw new IllegalArgumentException("User is not allocated to any bed");
        }
        
        String bedId = user.getAllocatedBedId();
        
        // Validate bed exists and is allocated to this user
        Bed bed = bedRepository.findById(bedId)
            .orElseThrow(() -> new IllegalArgumentException("Bed not found with ID: " + bedId));
        
        if (!bed.isOccupied() || !userId.equals(bed.getAllocatedUserId())) {
            throw new IllegalArgumentException("Bed is not allocated to this user");
        }
        
        try {
            String roomId = bed.getRoomId();
            
            // Update user allocation details
            user.setAllocated(false);
            user.setAllocatedBedId(null);
            user.setAllocatedRoomId(null);
            user.setUpdatedAt(LocalDateTime.now());
            
            // Update bed allocation details
            bed.deallocate();
            bed.setUpdatedAt(LocalDateTime.now());
            
            // Save both entities
            User savedUser = userRepository.save(user);
            Bed savedBed = bedRepository.save(bed);
            
            // Update room occupancy statistics
            roomService.updateRoomOccupancy(roomId);
            
            log.info("Successfully deallocated bed {} from user {}", bedId, userId);
            
            return new DeallocationResult(savedUser, savedBed, "Bed deallocated successfully");
            
        } catch (Exception e) {
            log.error("Failed to deallocate bed from user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Deallocation failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Transfer a user from one bed to another with transaction management
     * This method ensures that the deallocation and allocation happen atomically
     * 
     * @param userId The ID of the user to transfer
     * @param newBedId The ID of the new bed to allocate
     * @return TransferResult containing the updated user and beds
     * @throws IllegalArgumentException if transfer is not possible
     */
    @Transactional
    public TransferResult transferUserToNewBed(String userId, String newBedId) {
        log.info("Starting transfer of user {} to new bed {}", userId, newBedId);
        
        // Validate user exists and is currently allocated
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        if (!user.isAllocated()) {
            throw new IllegalArgumentException("User is not currently allocated to any bed");
        }
        
        String oldBedId = user.getAllocatedBedId();
        
        // Validate new bed exists and is available
        Bed newBed = bedRepository.findById(newBedId)
            .orElseThrow(() -> new IllegalArgumentException("New bed not found with ID: " + newBedId));
        
        if (!newBed.isAvailable()) {
            throw new IllegalArgumentException("New bed is not available for allocation");
        }
        
        try {
            // Get old bed for deallocation
            Bed oldBed = bedRepository.findById(oldBedId)
                .orElseThrow(() -> new IllegalArgumentException("Current bed not found with ID: " + oldBedId));
            
            String oldRoomId = oldBed.getRoomId();
            String newRoomId = newBed.getRoomId();
            
            // Deallocate from old bed
            oldBed.deallocate();
            oldBed.setUpdatedAt(LocalDateTime.now());
            
            // Allocate to new bed
            newBed.allocateToUser(userId);
            newBed.setUpdatedAt(LocalDateTime.now());
            
            // Update user allocation details
            user.setAllocatedBedId(newBedId);
            user.setAllocatedRoomId(newRoomId);
            user.setUpdatedAt(LocalDateTime.now());
            
            // Save all entities
            User savedUser = userRepository.save(user);
            Bed savedOldBed = bedRepository.save(oldBed);
            Bed savedNewBed = bedRepository.save(newBed);
            
            // Update room occupancy statistics for both rooms
            roomService.updateRoomOccupancy(oldRoomId);
            roomService.updateRoomOccupancy(newRoomId);
            
            log.info("Successfully transferred user {} from bed {} to bed {}", userId, oldBedId, newBedId);
            
            return new TransferResult(savedUser, savedOldBed, savedNewBed, "User transferred successfully");
            
        } catch (Exception e) {
            log.error("Failed to transfer user {} to new bed {}: {}", userId, newBedId, e.getMessage());
            throw new RuntimeException("Transfer failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get allocation history for a user
     * 
     * @param userId The user ID to get history for
     * @return AllocationHistory containing current allocation details
     */
    public AllocationHistory getUserAllocationHistory(String userId) {
        log.debug("Getting allocation history for user {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        if (user.isAllocated()) {
            Bed bed = bedRepository.findById(user.getAllocatedBedId())
                .orElseThrow(() -> new IllegalArgumentException("Allocated bed not found"));
            
            Room room = roomRepository.findById(bed.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Allocated room not found"));
            
            return new AllocationHistory(user, bed, room, bed.getAllocatedAt());
        } else {
            return new AllocationHistory(user, null, null, null);
        }
    }
    
    /**
     * Get all allocations in the system
     * 
     * @return List of AllocationInfo containing all current allocations
     */
    public List<AllocationInfo> getAllAllocations() {
        log.debug("Getting all allocations in the system");
        
        List<User> allocatedUsers = userRepository.findByIsAllocatedTrue();
        
        return allocatedUsers.stream()
            .map(user -> {
                Bed bed = bedRepository.findById(user.getAllocatedBedId()).orElse(null);
                Room room = bed != null ? 
                    roomRepository.findById(bed.getRoomId()).orElse(null) : null;
                return new AllocationInfo(user, bed, room);
            })
            .toList();
    }
    
    /**
     * Result class for allocation operations
     */
    public static class AllocationResult {
        private final User user;
        private final Bed bed;
        private final Room room;
        private final String message;
        
        public AllocationResult(User user, Bed bed, Room room, String message) {
            this.user = user;
            this.bed = bed;
            this.room = room;
            this.message = message;
        }
        
        public User getUser() { return user; }
        public Bed getBed() { return bed; }
        public Room getRoom() { return room; }
        public String getMessage() { return message; }
    }
    
    /**
     * Result class for deallocation operations
     */
    public static class DeallocationResult {
        private final User user;
        private final Bed bed;
        private final String message;
        
        public DeallocationResult(User user, Bed bed, String message) {
            this.user = user;
            this.bed = bed;
            this.message = message;
        }
        
        public User getUser() { return user; }
        public Bed getBed() { return bed; }
        public String getMessage() { return message; }
    }
    
    /**
     * Result class for transfer operations
     */
    public static class TransferResult {
        private final User user;
        private final Bed oldBed;
        private final Bed newBed;
        private final String message;
        
        public TransferResult(User user, Bed oldBed, Bed newBed, String message) {
            this.user = user;
            this.oldBed = oldBed;
            this.newBed = newBed;
            this.message = message;
        }
        
        public User getUser() { return user; }
        public Bed getOldBed() { return oldBed; }
        public Bed getNewBed() { return newBed; }
        public String getMessage() { return message; }
    }
    
    /**
     * Class for allocation history
     */
    public static class AllocationHistory {
        private final User user;
        private final Bed bed;
        private final Room room;
        private final LocalDateTime allocatedAt;
        
        public AllocationHistory(User user, Bed bed, Room room, LocalDateTime allocatedAt) {
            this.user = user;
            this.bed = bed;
            this.room = room;
            this.allocatedAt = allocatedAt;
        }
        
        public User getUser() { return user; }
        public Bed getBed() { return bed; }
        public Room getRoom() { return room; }
        public LocalDateTime getAllocatedAt() { return allocatedAt; }
    }
    
    /**
     * Class for allocation information
     */
    public static class AllocationInfo {
        private final User user;
        private final Bed bed;
        private final Room room;
        
        public AllocationInfo(User user, Bed bed, Room room) {
            this.user = user;
            this.bed = bed;
            this.room = room;
        }
        
        public User getUser() { return user; }
        public Bed getBed() { return bed; }
        public Room getRoom() { return room; }
    }
}
