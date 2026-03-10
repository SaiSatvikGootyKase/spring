import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { 
  HomeIcon, 
  UserGroupIcon, 
  BuildingOfficeIcon, 
  ClipboardDocumentListIcon 
} from '@heroicons/react/24/outline';

/**
 * Navigation Component
 * Provides navigation menu for the Room Allocation System
 * Shows active page highlighting and responsive design
 * 
 * @component
 * @returns {JSX.Element} Navigation component with menu items
 */
export const Navigation: React.FC = () => {
  const location = useLocation();
  
  /**
   * Check if a navigation link is currently active
   * @param path - The path to check
   * @returns boolean - True if the path is active
   */
  const isActive = (path: string): boolean => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };
  
  /**
   * CSS classes for navigation links
   * @param path - The path to generate classes for
   * @returns string - CSS classes with active state
   */
  const navLinkClasses = (path: string): string => {
    const baseClasses = "flex items-center px-3 py-2 rounded-md text-sm font-medium transition-colors duration-200";
    const activeClasses = "bg-blue-100 text-blue-700";
    const inactiveClasses = "text-gray-600 hover:bg-gray-50 hover:text-gray-900";
    
    return `${baseClasses} ${isActive(path) ? activeClasses : inactiveClasses}`;
  };
  
  return (
    <nav className="bg-white shadow-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          {/* Logo/Brand */}
          <div className="flex items-center">
            <BuildingOfficeIcon className="h-8 w-8 text-blue-600" />
            <span className="ml-2 text-xl font-bold text-gray-900">
              Room Allocation System
            </span>
          </div>
          
          {/* Navigation Menu */}
          <div className="flex items-center space-x-4">
            {/* Dashboard Link */}
            <Link
              to="/"
              className={navLinkClasses('/')}
              title="Dashboard - View system overview and statistics"
            >
              <HomeIcon className="h-5 w-5 mr-2" />
              Dashboard
            </Link>
            
            {/* Users Management Link */}
            <Link
              to="/users"
              className={navLinkClasses('/users')}
              title="Manage Users - Add, edit, and delete users"
            >
              <UserGroupIcon className="h-5 w-5 mr-2" />
              Users
            </Link>
            
            {/* Rooms Management Link */}
            <Link
              to="/rooms"
              className={navLinkClasses('/rooms')}
              title="Manage Rooms - Add, edit, and delete rooms"
            >
              <BuildingOfficeIcon className="h-5 w-5 mr-2" />
              Rooms
            </Link>
            
            {/* Bed Allocation Link */}
            <Link
              to="/allocations"
              className={navLinkClasses('/allocations')}
              title="Bed Allocation - Allocate and deallocate beds to users"
            >
              <ClipboardDocumentListIcon className="h-5 w-5 mr-2" />
              Allocations
            </Link>
          </div>
        </div>
      </div>
      
      {/* Mobile responsive menu (hidden on desktop) */}
      <div className="sm:hidden">
        <div className="px-2 pt-2 pb-3 space-y-1">
          {/* Mobile Dashboard Link */}
          <Link
            to="/"
            className={navLinkClasses('/')}
            title="Dashboard - View system overview and statistics"
          >
            <HomeIcon className="h-5 w-5 mr-2" />
            Dashboard
          </Link>
          
          {/* Mobile Users Management Link */}
          <Link
            to="/users"
            className={navLinkClasses('/users')}
            title="Manage Users - Add, edit, and delete users"
          >
            <UserGroupIcon className="h-5 w-5 mr-2" />
            Users
          </Link>
          
          {/* Mobile Rooms Management Link */}
          <Link
            to="/rooms"
            className={navLinkClasses('/rooms')}
            title="Manage Rooms - Add, edit, and delete rooms"
          >
            <BuildingOfficeIcon className="h-5 w-5 mr-2" />
            Rooms
          </Link>
          
          {/* Mobile Bed Allocation Link */}
          <Link
            to="/allocations"
            className={navLinkClasses('/allocations')}
            title="Bed Allocation - Allocate and deallocate beds to users"
          >
            <ClipboardDocumentListIcon className="h-5 w-5 mr-2" />
            Allocations
          </Link>
        </div>
      </div>
    </nav>
  );
};
