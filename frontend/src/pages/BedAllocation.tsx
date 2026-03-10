import React, { useState, useEffect } from 'react';

/**
 * Bed Allocation Component
 * Provides comprehensive bed allocation interface with transaction-safe operations
 * Displays allocation interface with user selection, bed selection, and allocation management
 * 
 * @component
 * @returns {JSX.Element} Bed allocation interface
 */
export const BedAllocation: React.FC = () => {
  // State management for allocation data and UI
  const [users, setUsers] = useState<any[]>([]);
  const [beds, setBeds] = useState<any[]>([]);
  const [allocations, setAllocations] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedUser, setSelectedUser] = useState<string>('');
  const [selectedBed, setSelectedBed] = useState<string>('');
  const [allocationMode, setAllocationMode] = useState<'allocate' | 'deallocate' | 'transfer'>('allocate');
  
  /**
   * Fetch users, beds, and allocations from the backend API
   * Loads all data needed for allocation operations
   */
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);
        
        // Fetch all data in parallel
        const [usersResponse, bedsResponse, allocationsResponse] = await Promise.all([
          fetch('http://localhost:8080/api/users/unallocated'),
          fetch('http://localhost:8080/api/beds/available'),
          fetch('http://localhost:8080/api/allocations')
        ]);
        
        if (!usersResponse.ok || !bedsResponse.ok || !allocationsResponse.ok) {
          throw new Error('Failed to fetch allocation data');
        }
        
        const usersData = await usersResponse.json();
        const bedsData = await bedsResponse.json();
        const allocationsData = await allocationsResponse.json();
        
        setUsers(usersData);
        setBeds(bedsData);
        setAllocations(allocationsData);
        
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred while fetching allocation data');
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
  }, []);
  
  /**
   * Handle bed allocation to user
   * Calls allocation API with transaction support
   */
  const handleAllocate = async () => {
    if (!selectedUser || !selectedBed) {
      setError('Please select both a user and a bed for allocation');
      return;
    }
    
    try {
      setError(null);
      setLoading(true);
      
      const response = await fetch('http://localhost:8080/api/allocations/allocate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: selectedUser,
          bedId: selectedBed,
        }),
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to allocate bed');
      }
      
      const result = await response.json();
      alert(`Successfully allocated bed to user! ${result.message}`);
      
      // Reset selections
      setSelectedUser('');
      setSelectedBed('');
      setAllocationMode('allocate');
      
      // Refresh data
      window.location.reload();
      
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to allocate bed');
    } finally {
      setLoading(false);
    }
  };
  
  /**
   * Handle bed deallocation from user
   * Calls deallocation API with transaction support
   */
  const handleDeallocate = async (userId: string) => {
    if (!window.confirm('Are you sure you want to deallocate this user\'s bed?')) {
      return;
    }
    
    try {
      setError(null);
      setLoading(true);
      
      const response = await fetch(`http://localhost:8080/api/allocations/deallocate/${userId}`, {
        method: 'POST',
      });
      
      if (!response.ok) {
        throw new Error('Failed to deallocate bed');
      }
      
      const result = await response.json();
      alert(`Successfully deallocated bed! ${result.message}`);
      
      // Reset selections
      setSelectedUser('');
      setSelectedBed('');
      setAllocationMode('allocate');
      
      // Refresh data
      window.location.reload();
      
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to deallocate bed');
    } finally {
      setLoading(false);
    }
  };
  
  /**
   * Handle bed transfer between users
   * Calls transfer API with transaction support
   */
  const handleTransfer = async () => {
    if (!selectedUser || !selectedBed) {
      setError('Please select both a user and a new bed for transfer');
      return;
    }
    
    // Find current allocation for the selected user
    const currentAllocation = allocations.find(a => a.user.id === selectedUser);
    if (!currentAllocation) {
      setError('Selected user is not currently allocated to any bed');
      return;
    }
    
    try {
      setError(null);
      setLoading(true);
      
      const response = await fetch('http://localhost:8080/api/allocations/transfer', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          userId: selectedUser,
          newBedId: selectedBed,
        }),
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to transfer bed');
      }
      
      const result = await response.json();
      alert(`Successfully transferred user! ${result.message}`);
      
      // Reset selections
      setSelectedUser('');
      setSelectedBed('');
      setAllocationMode('allocate');
      
      // Refresh data
      window.location.reload();
      
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to transfer bed');
    } finally {
      setLoading(false);
    }
  };
  
  /**
   * Filter data based on search term
   * Searches across users, beds, and allocations
   */
  const filteredUsers = users.filter(user =>
    user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  const filteredBeds = beds.filter(bed =>
    bed.bedNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (bed.roomNumber && bed.roomNumber.toLowerCase().includes(searchTerm.toLowerCase()))
  );
  
  const filteredAllocations = allocations.filter(allocation =>
    allocation.user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    allocation.bed?.bedNumber.toLowerCase().includes(searchTerm.toLowerCase())
  );
  
  /**
   * Get user name by ID
   */
  const getUserName = (userId: string) => {
    const user = users.find(u => u.id === userId);
    return user ? user.name : 'Unknown User';
  };
  
  /**
   * Get bed details by ID
   */
  const getBedDetails = (bedId: string) => {
    const bed = beds.find(b => b.id === bedId);
    if (!bed) return 'Unknown Bed';
    return `${bed.bedNumber} (Room: ${bed.roomNumber || 'Unknown'})`;
  };
  
  /**
   * Render loading state
   */
  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="loading-spinner"></div>
        <span className="ml-3 text-gray-600">Loading allocation data...</span>
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
            <h1 className="text-2xl font-bold text-gray-900">Bed Allocation</h1>
            <p className="mt-2 text-gray-600">
              Allocate and manage bed assignments with transaction-safe operations
            </p>
          </div>
          
          <div className="flex space-x-3">
            {/* Search Input */}
            <div className="relative">
              <input
                type="text"
                placeholder="Search users, beds, or allocations..."
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
            
            {/* Mode Selector */}
            <select
              value={allocationMode}
              onChange={(e) => setAllocationMode(e.target.value as 'allocate' | 'deallocate' | 'transfer')}
              className="block border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
            >
              <option value="allocate">Allocate Bed</option>
              <option value="deallocate">Deallocate Bed</option>
              <option value="transfer">Transfer Bed</option>
            </select>
          </div>
        </div>
      </div>
      
      {/* Allocation Interface */}
      <div className="bg-white shadow rounded-lg overflow-hidden">
        <div className="px-4 py-5 sm:p-6">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            
            {/* Users Selection Panel */}
            <div className="bg-gray-50 rounded-lg p-6">
              <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                Available Users ({filteredUsers.length})
              </h3>
              <div className="space-y-2 max-h-64 overflow-y-auto custom-scrollbar">
                {filteredUsers.map((user) => (
                  <div
                    key={user.id}
                    className={`p-3 border rounded cursor-pointer transition-colors duration-200 ${
                      selectedUser === user.id 
                        ? 'bg-blue-100 border-blue-500 text-blue-700' 
                        : 'bg-white border-gray-200 hover:bg-gray-50'
                    }`}
                    onClick={() => setSelectedUser(user.id)}
                  >
                    <div className="flex items-center">
                      <div className="flex-shrink-0">
                        <div className="h-6 w-6 bg-gray-300 rounded-full flex items-center justify-center">
                          <span className="text-xs font-medium text-gray-600">
                            {user.name.charAt(0).toUpperCase()}
                          </span>
                        </div>
                      </div>
                      <div className="ml-3">
                        <p className="text-sm font-medium text-gray-900">{user.name}</p>
                        <p className="text-sm text-gray-500">{user.email}</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
            
            {/* Beds Selection Panel */}
            <div className="bg-gray-50 rounded-lg p-6">
              <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                Available Beds ({filteredBeds.length})
              </h3>
              <div className="space-y-2 max-h-64 overflow-y-auto custom-scrollbar">
                {filteredBeds.map((bed) => (
                  <div
                    key={bed.id}
                    className={`p-3 border rounded cursor-pointer transition-colors duration-200 ${
                      selectedBed === bed.id 
                        ? 'bg-green-100 border-green-500 text-green-700' 
                        : 'bg-white border-gray-200 hover:bg-gray-50'
                    }`}
                    onClick={() => setSelectedBed(bed.id)}
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-sm font-medium text-gray-900">{bed.bedNumber}</p>
                        <p className="text-xs text-gray-500">{getBedDetails(bed.id)}</p>
                      </div>
                      <div className="flex-shrink-0">
                        <span className={`status-badge ${bed.isOccupied ? 'status-occupied' : 'status-available'}`}>
                          {bed.isOccupied ? 'Occupied' : 'Available'}
                        </span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
            
            {/* Current Allocations */}
            <div className="lg:col-span-3 bg-gray-50 rounded-lg p-6">
              <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                Current Allocations ({filteredAllocations.length})
              </h3>
              <div className="space-y-2 max-h-64 overflow-y-auto custom-scrollbar">
                {filteredAllocations.map((allocation) => (
                  <div key={allocation.id} className="bg-white border border-gray-200 rounded-lg p-4">
                    <div className="flex items-center justify-between mb-2">
                      <div>
                        <h4 className="text-lg font-medium text-gray-900">
                          {allocation.user.name}
                        </h4>
                        <p className="text-sm text-gray-500">{allocation.user.email}</p>
                      </div>
                      <div className="flex space-x-2">
                        {/* Deallocate Button */}
                        <button
                          onClick={() => handleDeallocate(allocation.user.id)}
                          className="btn-danger text-sm py-1 px-2"
                        >
                          Deallocate
                        </button>
                      </div>
                    </div>
                    
                    <div className="border-t border-gray-200 pt-4">
                      <div className="text-sm text-gray-600">
                        <strong>Bed:</strong> {allocation.bed?.bedNumber || 'N/A'}<br />
                        <strong>Room:</strong> {allocation.bed?.roomNumber || 'N/A'}<br />
                        <strong>Allocated:</strong> {allocation.allocatedAt ? new Date(allocation.allocatedAt).toLocaleDateString() : 'N/A'}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
          
          {/* Action Buttons */}
          <div className="lg:col-span-3">
            <div className="bg-white shadow rounded-lg p-6">
              <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
                Actions
              </h3>
              
              <div className="space-y-3">
                {allocationMode === 'allocate' && (
                  <div>
                    <p className="text-sm text-gray-600 mb-2">
                      Select a user and a bed to allocate the bed to the user.
                    </p>
                    <div className="grid grid-cols-2 gap-3">
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Selected User
                        </label>
                        <div className="mt-1 text-sm text-gray-900">
                          {selectedUser ? getUserName(selectedUser) : 'No user selected'}
                        </div>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          Selected Bed
                        </label>
                        <div className="mt-1 text-sm text-gray-900">
                          {selectedBed ? getBedDetails(selectedBed) : 'No bed selected'}
                        </div>
                      </div>
                    </div>
                    <button
                      onClick={handleAllocate}
                      disabled={!selectedUser || !selectedBed || loading}
                      className="btn-primary w-full"
                    >
                      {loading ? 'Processing...' : 'Allocate Bed'}
                    </button>
                  </div>
                )}
                
                {allocationMode === 'deallocate' && (
                  <div>
                    <p className="text-sm text-gray-600 mb-2">
                      Click on a user to deallocate their current bed assignment.
                    </p>
                    <div className="text-sm text-gray-500">
                      Users will be listed in the Current Allocations panel above.
                    </div>
                  </div>
                )}
                
                {allocationMode === 'transfer' && (
                  <div>
                    <p className="text-sm text-gray-600 mb-2">
                      Select a currently allocated user and a new available bed to transfer the user.
                    </p>
                    <div className="grid grid-cols-2 gap-3">
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          User to Transfer
                        </label>
                        <div className="mt-1 text-sm text-gray-900">
                          {selectedUser ? getUserName(selectedUser) : 'No user selected'}
                        </div>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700">
                          New Bed
                        </label>
                        <div className="mt-1 text-sm text-gray-900">
                          {selectedBed ? getBedDetails(selectedBed) : 'No bed selected'}
                        </div>
                      </div>
                    </div>
                    <button
                      onClick={handleTransfer}
                      disabled={!selectedUser || !selectedBed || loading}
                      className="btn-primary w-full"
                    >
                      {loading ? 'Processing...' : 'Transfer User'}
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
