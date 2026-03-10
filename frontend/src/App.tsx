import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Navigation } from './components/Navigation';
import { Dashboard } from './pages/Dashboard';
import { UserManagement } from './pages/UserManagement';
import { RoomManagement } from './pages/RoomManagement';
import { BedAllocation } from './pages/BedAllocation';
import './index.css';

/**
 * Main App Component
 * This is the root component of the Room Allocation System
 * It handles routing and provides the main layout structure
 * 
 * @component
 * @returns {JSX.Element} The main application component with routing
 */
function App(): JSX.Element {
  return (
    <div className="min-h-screen bg-gray-50">
      <Router>
        {/* Navigation component - visible on all pages */}
        <Navigation />
        
        {/* Main content area with routes */}
        <main className="container mx-auto px-4 py-8">
          <Routes>
            {/* Dashboard route - shows overview and statistics */}
            <Route path="/" element={<Dashboard />} />
            
            {/* User management routes */}
            <Route path="/users/*" element={<UserManagement />} />
            
            {/* Room management routes */}
            <Route path="/rooms/*" element={<RoomManagement />} />
            
            {/* Bed allocation routes */}
            <Route path="/allocations/*" element={<BedAllocation />} />
          </Routes>
        </main>
      </Router>
    </div>
  );
}

export default App;
