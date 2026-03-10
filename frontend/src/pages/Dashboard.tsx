import React, { useState, useEffect } from 'react';

/**
 * Dashboard Component
 * Main dashboard showing system overview and statistics
 * Displays key metrics and recent activities for the Room Allocation System
 * 
 * @component
 * @returns {JSX.Element} Dashboard component with statistics and overview
 */
export const Dashboard: React.FC = () => {
  // State for dashboard data
  const [userStats, setUserStats] = useState({
    total: 0,
    allocated: 0,
    unallocated: 0,
    allocationPercentage: 0
  });
  
  const [roomStats, setRoomStats] = useState({
    total: 0,
    active: 0,
    full: 0,
    available: 0,
    activePercentage: 0
  });
  
  const [bedStats, setBedStats] = useState({
    total: 0,
    occupied: 0,
    available: 0,
    occupancyPercentage: 0
  });
  
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  /**
   * Fetch dashboard statistics from the backend API
   * Loads user, room, and bed statistics
   */
  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        setError(null);
        
        // Fetch user statistics
        const userResponse = await fetch('http://localhost:8080/api/users/statistics');
        if (!userResponse.ok) throw new Error('Failed to fetch user statistics');
        const userData = await userResponse.json();
        
        // Fetch room statistics
        const roomResponse = await fetch('http://localhost:8080/api/rooms/statistics');
        if (!roomResponse.ok) throw new Error('Failed to fetch room statistics');
        const roomData = await roomResponse.json();
        
        // Fetch bed statistics
        const bedResponse = await fetch('http://localhost:8080/api/beds/statistics');
        if (!bedResponse.ok) throw new Error('Failed to fetch bed statistics');
        const bedData = await bedResponse.json();
        
        // Update state with fetched data
        setUserStats({
          total: userData.totalUsers,
          allocated: userData.allocatedUsers,
          unallocated: userData.unallocatedUsers,
          allocationPercentage: userData.allocationPercentage || 0
        });
        
        setRoomStats({
          total: roomData.totalRooms,
          active: roomData.activeRooms,
          full: roomData.fullRooms,
          available: roomData.roomsWithAvailableBeds,
          activePercentage: roomData.activePercentage || 0
        });
        
        setBedStats({
          total: bedData.totalBeds,
          occupied: bedData.occupiedBeds,
          available: bedData.availableBeds,
          occupancyPercentage: bedData.occupancyPercentage || 0
        });
        
      } catch (err) {
        setError(err instanceof Error ? err.message : 'An error occurred while fetching dashboard data');
      } finally {
        setLoading(false);
      }
    };
    
    fetchDashboardData();
  }, []);
  
  /**
   * Render loading state
   */
  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="loading-spinner"></div>
        <span className="ml-3 text-gray-600">Loading dashboard...</span>
      </div>
    );
  }
  
  /**
   * Render error state
   */
  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-md p-4">
        <div className="flex">
          <div className="flex-shrink-0">
            <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 00016zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414l3-3z" clipRule="evenodd" />
            </svg>
          </div>
          <div className="ml-3">
            <h3 className="text-sm font-medium text-red-800">Error loading dashboard</h3>
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
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-2 text-gray-600">
          Overview of the Room Allocation System - Monitor users, rooms, and bed allocations
        </p>
      </div>
      
      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* User Statistics Card */}
        <div className="bg-white overflow-hidden shadow rounded-lg card-hover">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                  <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0110-7v-4a6 6 0 00-10-7v4a6 6 0 0010 7v1a2 2 0 002 2h12a2 2 0 002-2v-1a6 6 0 0010-7v-4a6 6 0 00-10-7z" />
                  </svg>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">Total Users</dt>
                  <dd className="text-lg font-medium text-gray-900">{userStats.total}</dd>
                </dl>
              </div>
            </div>
          </div>
          <div className="bg-gray-50 px-5 py-3">
            <div className="text-sm">
              <span className="text-green-600 font-medium">{userStats.allocated} allocated</span>
              <span className="mx-1">•</span>
              <span className="text-gray-500">{userStats.unallocated} available</span>
              <span className="mx-1">•</span>
              <span className="text-blue-600">{userStats.allocationPercentage.toFixed(1)}% allocated</span>
            </div>
          </div>
        </div>
        
        {/* Room Statistics Card */}
        <div className="bg-white overflow-hidden shadow rounded-lg card-hover">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center">
                  <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                  </svg>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">Total Rooms</dt>
                  <dd className="text-lg font-medium text-gray-900">{roomStats.total}</dd>
                </dl>
              </div>
            </div>
          </div>
          <div className="bg-gray-50 px-5 py-3">
            <div className="text-sm">
              <span className="text-green-600 font-medium">{roomStats.active} active</span>
              <span className="mx-1">•</span>
              <span className="text-red-600">{roomStats.full} full</span>
              <span className="mx-1">•</span>
              <span className="text-blue-600">{roomStats.available} with beds</span>
              <span className="mx-1">•</span>
              <span className="text-gray-600">{roomStats.activePercentage.toFixed(1)}% active</span>
            </div>
          </div>
        </div>
        
        {/* Bed Statistics Card */}
        <div className="bg-white overflow-hidden shadow rounded-lg card-hover">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="w-8 h-8 bg-purple-500 rounded-full flex items-center justify-center">
                  <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001 1v3a1 1 0 001 1m0 0h6a1 1 0 001-1v-3a1 1 0 00-1-1m-6 0h6" />
                  </svg>
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">Total Beds</dt>
                  <dd className="text-lg font-medium text-gray-900">{bedStats.total}</dd>
                </dl>
              </div>
            </div>
          </div>
          <div className="bg-gray-50 px-5 py-3">
            <div className="text-sm">
              <span className="text-blue-600 font-medium">{bedStats.occupied} occupied</span>
              <span className="mx-1">•</span>
              <span className="text-green-600">{bedStats.available} available</span>
              <span className="mx-1">•</span>
              <span className="text-purple-600">{bedStats.occupancyPercentage.toFixed(1)}% occupied</span>
            </div>
          </div>
        </div>
      </div>
      
      {/* Quick Actions */}
      <div className="bg-white shadow rounded-lg p-6">
        <h2 className="text-lg font-medium text-gray-900 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <button className="btn-primary">
            Add New User
          </button>
          <button className="btn-secondary">
            Add New Room
          </button>
          <button className="btn-secondary">
            Allocate Bed
          </button>
          <button className="btn-secondary">
            View Reports
          </button>
        </div>
      </div>
    </div>
  );
};
