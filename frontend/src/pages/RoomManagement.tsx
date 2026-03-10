import React, { useState, useEffect } from 'react';

/**
 * Room Management Component
 * Provides comprehensive room management interface with CRUD operations
 * Displays rooms in a table with actions for add, edit, and delete
 * 
 * @component
 * @returns {JSX.Element} Room management interface
 */
export const RoomManagement: React.FC = () => {
  // State management for rooms and UI
  const [rooms, setRooms] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [showAddForm, setShowAddForm] = useState(false);
  
  // State for new room form
  const [newRoom, setNewRoom] = useState({
    roomNumber: '',
    roomType: '',
    capacity: 1,
    floorNumber: 1,
    description: '',
    isActive: true
  });
  
  /**
   * Fetch rooms from the backend API
   * Loads all rooms with their occupancy status
   */
  useEffect(() => {
    const fetchRooms = async () => {
      try {
        setLoading(true);
        setError(null);
        
        const response = await fetch('http://localhost:8080/api/rooms');
        if (!response.ok) throw new Error('Failed to fetch rooms');
        const data = await response.json();
        setRooms(data);
        
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred while fetching rooms');
      } finally {
        setLoading(false);
      }
    };
    
    fetchRooms();
  }, []);
  
  /**
   * Handle room creation
   * Validates input and creates new room via API
   */
  const handleCreateRoom = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Basic validation
    if (!newRoom.roomNumber.trim() || newRoom.capacity < 1) {
      setError('Room number and valid capacity are required');
      return;
    }
    
    try {
      setError(null);
      
      const response = await fetch('http://localhost:8080/api/rooms', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newRoom),
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to create room');
      }
      
      const createdRoom = await response.json();
      setRooms([...rooms, createdRoom]);
      
      // Reset form
      setNewRoom({
        roomNumber: '',
        roomType: '',
        capacity: 1,
        floorNumber: 1,
        description: '',
        isActive: true
      });
      setShowAddForm(false);
      
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create room');
    }
  };
  
  /**
   * Handle room deletion
   * Validates that room has no occupied beds before deletion
   */
  const handleDeleteRoom = async (roomId: string, roomNumber: string) => {
    const room = rooms.find(r => r.id === roomId);
    
    if (room && room.occupiedBeds > 0) {
      setError('Cannot delete room with occupied beds');
      return;
    }
    
    if (!window.confirm(`Are you sure you want to delete room "${roomNumber}"?`)) {
      return;
    }
    
    try {
      setError(null);
      
      const response = await fetch(`http://localhost:8080/api/rooms/${roomId}`, {
        method: 'DELETE',
      });
      
      if (!response.ok) {
        throw new Error('Failed to delete room');
      }
      
      setRooms(rooms.filter(room => room.id !== roomId));
      
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to delete room');
    }
  };
  
  /**
   * Filter rooms based on search term
   * Searches in room number and type fields
   */
  const filteredRooms = rooms.filter(room =>
    room.roomNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (room.roomType && room.roomType.toLowerCase().includes(searchTerm.toLowerCase()))
  );
  
  /**
   * Get status badge CSS classes
   */
  const getStatusClasses = (room: any) => {
    if (!room.isActive) return 'status-inactive';
    if (room.isFull) return 'status-occupied';
    return 'status-available';
  };
  
  /**
   * Render loading state
   */
  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="loading-spinner"></div>
        <span className="ml-3 text-gray-600">Loading rooms...</span>
      </div>
    );
  }
  
  /**
   * Render error state
   */
  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-md p-4 mb-6">
        <div className="flex">
          <div className="flex-shrink-0">
            <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 00016zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414l3-3z" clipRule="evenodd" />
            </svg>
          </div>
          <div className="ml-3">
            <h3 className="text-sm font-medium text-red-800">Error</h3>
            <div className="mt-2 text-sm text-red-700">{error}</div>
          </div>
        </div>
      </div>
    );
  }
  
  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="bg-white shadow rounded-lg p-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Room Management</h1>
            <p className="mt-2 text-gray-600">
              Manage rooms in the allocation system - Add, edit, and delete rooms
            </p>
          </div>
          
          <div className="flex space-x-3">
            {/* Search Input */}
            <div className="relative">
              <input
                type="text"
                placeholder="Search rooms by number or type..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
              />
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 7 7 0 0021 12.79z" />
                </svg>
              </div>
            </div>
            
            {/* Add Room Button */}
            <button
              onClick={() => setShowAddForm(true)}
              className="btn-primary"
            >
              Add New Room
            </button>
          </div>
        </div>
      </div>
      
      {/* Add Room Form Modal */}
      {showAddForm && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-10">
          <div className="relative min-h-screen flex items-center justify-center">
            <div className="bg-white rounded-lg p-8 max-w-md w-full m-4">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-lg font-medium text-gray-900">Add New Room</h2>
                <button
                  onClick={() => setShowAddForm(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <svg className="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>
              
              <form onSubmit={handleCreateRoom} className="space-y-4">
                {/* Room Number Input */}
                <div>
                  <label htmlFor="roomNumber" className="block text-sm font-medium text-gray-700">
                    Room Number *
                  </label>
                  <input
                    type="text"
                    id="roomNumber"
                    value={newRoom.roomNumber}
                    onChange={(e) => setNewRoom({...newRoom, roomNumber: e.target.value})}
                    required
                    className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                    placeholder="e.g., 101, A-201"
                  />
                </div>
                
                {/* Room Type Input */}
                <div>
                  <label htmlFor="roomType" className="block text-sm font-medium text-gray-700">
                    Room Type
                  </label>
                  <select
                    id="roomType"
                    value={newRoom.roomType}
                    onChange={(e) => setNewRoom({...newRoom, roomType: e.target.value})}
                    className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                  >
                    <option value="">Select room type</option>
                    <option value="Single">Single</option>
                    <option value="Double">Double</option>
                    <option value="Dormitory">Dormitory</option>
                    <option value="Suite">Suite</option>
                  </select>
                </div>
                
                {/* Capacity Input */}
                <div>
                  <label htmlFor="capacity" className="block text-sm font-medium text-gray-700">
                    Capacity *
                  </label>
                  <input
                    type="number"
                    id="capacity"
                    value={newRoom.capacity}
                    onChange={(e) => setNewRoom({...newRoom, capacity: parseInt(e.target.value) || 1})}
                    required
                    min="1"
                    className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                    placeholder="Number of beds this room can accommodate"
                  />
                </div>
                
                {/* Floor Number Input */}
                <div>
                  <label htmlFor="floorNumber" className="block text-sm font-medium text-gray-700">
                    Floor Number
                  </label>
                  <input
                    type="number"
                    id="floorNumber"
                    value={newRoom.floorNumber}
                    onChange={(e) => setNewRoom({...newRoom, floorNumber: parseInt(e.target.value) || 1})}
                    min="0"
                    className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                    placeholder="Floor number (e.g., 1, 2, 3)"
                  />
                </div>
                
                {/* Description Input */}
                <div>
                  <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                    Description
                  </label>
                  <textarea
                    id="description"
                    value={newRoom.description}
                    onChange={(e) => setNewRoom({...newRoom, description: e.target.value})}
                    rows={3}
                    className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                    placeholder="Additional details about the room (optional)"
                  />
                </div>
                
                {/* Form Actions */}
                <div className="flex justify-end space-x-3">
                  <button
                    type="button"
                    onClick={() => setShowAddForm(false)}
                    className="btn-secondary"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn-primary"
                  >
                    Create Room
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
      
      {/* Rooms Table */}
      <div className="bg-white shadow rounded-lg overflow-hidden">
        <div className="px-4 py-5 sm:p-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-lg leading-6 font-medium text-gray-900">
              Rooms ({filteredRooms.length})
            </h3>
            <p className="text-sm text-gray-500">
              {rooms.filter(r => r.isActive && !r.isFull).length} available • {rooms.filter(r => r.isFull).length} full
            </p>
          </div>
          
          {/* Responsive Table Container */}
          <div className="responsive-table">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Room Number
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Type
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Floor
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Capacity
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Occupied
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Available
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="relative px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredRooms.map((room) => (
                  <tr key={room.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {room.roomNumber}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {room.roomType || '-'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {room.floorNumber || '-'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {room.capacity}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {room.occupiedBeds || 0}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {room.capacity - (room.occupiedBeds || 0)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`status-badge ${getStatusClasses(room)}`}>
                        {room.isActive ? (room.isFull ? 'Full' : 'Available') : 'Inactive'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <div className="flex justify-end space-x-2">
                        {/* Edit Button */}
                        <button
                          className="btn-secondary text-xs py-1 px-2"
                        >
                          Edit
                        </button>
                        
                        {/* Delete Button */}
                        <button
                          onClick={() => handleDeleteRoom(room.id, room.roomNumber)}
                          className="btn-danger text-xs py-1 px-2"
                          disabled={room.occupiedBeds > 0}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};
