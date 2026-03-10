# Room Allocation System

A beginner-friendly room allocation system built with React.js frontend and Spring Boot backend with MongoDB database.

## Features

- **User Management**: Complete CRUD operations for users with allocation tracking
- **Room Management**: Manage rooms with capacity and occupancy tracking  
- **Bed Management**: Individual bed management with allocation status
- **Room Allocation**: Transaction-safe bed allocation with real-time updates
- **Professional Dashboard**: Overview with statistics and quick actions
- **Responsive Design**: Mobile-friendly interface using Tailwind CSS

## Technology Stack

### Backend (Spring Boot)
- **Java 21** with Spring Boot 3.5.11
- **MongoDB** database (mongodb://localhost:27017/)
- **Spring Data MongoDB** for database operations
- **Lombok** for reducing boilerplate code
- **Transaction Management** for atomic allocation operations

### Frontend (React)
- **React 18** with TypeScript
- **Tailwind CSS** for professional styling
- **React Router** for navigation
- **Axios** for API communication
- **Heroicons** for beautiful icons

## Database Schema

### Users Collection
```json
{
  "_id": "string",
  "name": "string",
  "email": "string", 
  "phoneNumber": "string",
  "isAllocated": "boolean",
  "allocatedBedId": "string",
  "allocatedRoomId": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### Rooms Collection
```json
{
  "_id": "string",
  "roomNumber": "string",
  "roomType": "string",
  "capacity": "number",
  "occupiedBeds": "number", 
  "floorNumber": "number",
  "description": "string",
  "isActive": "boolean",
  "isFull": "boolean",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### Beds Collection
```json
{
  "_id": "string",
  "bedNumber": "string",
  "roomId": "string",
  "allocatedUserId": "string",
  "isOccupied": "boolean",
  "bedType": "string",
  "position": "string",
  "isActive": "boolean",
  "notes": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime",
  "allocatedAt": "datetime"
}
```

## API Endpoints

### User Management
- `GET /api/users` - Get all users
- `POST /api/users` - Create new user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/allocated` - Get allocated users
- `GET /api/users/unallocated` - Get unallocated users
- `GET /api/users/statistics` - Get user statistics

### Room Management
- `GET /api/rooms` - Get all rooms
- `POST /api/rooms` - Create new room
- `GET /api/rooms/{id}` - Get room by ID
- `PUT /api/rooms/{id}` - Update room
- `DELETE /api/rooms/{id}` - Delete room
- `GET /api/rooms/available` - Get rooms with available beds
- `GET /api/rooms/statistics` - Get room statistics

### Bed Management
- `GET /api/beds` - Get all beds
- `POST /api/beds` - Create new bed
- `GET /api/beds/{id}` - Get bed by ID
- `PUT /api/beds/{id}` - Update bed
- `DELETE /api/beds/{id}` - Delete bed
- `GET /api/beds/available` - Get available beds
- `GET /api/beds/statistics` - Get bed statistics

### Room Allocation
- `POST /api/allocations/allocate` - Allocate bed to user
- `POST /api/allocations/deallocate/{userId}` - Deallocate bed from user
- `POST /api/allocations/transfer` - Transfer user to new bed
- `GET /api/allocations` - Get all allocations
- `GET /api/allocations/validate` - Validate allocation possibility

## Key Features

### Transaction Management
The system ensures **atomic operations** for bed allocation:
- When allocating a bed to a user, both the User and Bed entities are updated in a single transaction
- If any operation fails, all changes are rolled back to maintain data consistency
- Prevents double allocation and ensures data integrity

### Beginner-Friendly Code
- **Comprehensive Comments**: Every method and class is documented with clear explanations
- **Type Safety**: Full TypeScript support with proper interfaces
- **Error Handling**: Proper error messages and validation
- **Logging**: Detailed logging for debugging and monitoring
- **Validation**: Input validation at both frontend and backend

## Getting Started

### Prerequisites
- Java 21 or higher
- Node.js 16 or higher
- MongoDB running on localhost:27017
- npm or yarn package manager

### Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd myApp
   ```

2. Build the project:
   ```bash
   ./gradlew build
   ```

3. Run the Spring Boot application:
   ```bash
   ./gradlew bootRun
   ```

4. The backend will start on `http://localhost:8080`

### Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the React development server:
   ```bash
   npm start
   ```

4. The frontend will start on `http://localhost:3000`

### Database Setup
1. Install MongoDB on your system
2. Start MongoDB service:
   ```bash
   mongod
   ```
3. The application will automatically create the `room_allocation_db` database

## Usage

### 1. Create Rooms
- Navigate to Rooms page
- Click "Add New Room"
- Enter room details (number, capacity, type)
- Save to create the room

### 2. Create Beds
- Navigate to Beds section within a room
- Click "Add New Bed"
- Enter bed details (number, type, position)
- Save to create the bed

### 3. Add Users
- Navigate to Users page
- Click "Add New User"
- Enter user details (name, email, phone)
- Save to create the user

### 4. Allocate Beds
- Navigate to Allocations page
- Select an unallocated user
- Select an available bed
- Click "Allocate Bed" to complete the allocation

### 5. Monitor Dashboard
- View real-time statistics on the Dashboard
- Track user allocation percentages
- Monitor room occupancy rates
- Access quick actions for common tasks

## Security Considerations

- **Input Validation**: All inputs are validated at both frontend and backend
- **Transaction Safety**: Allocation operations use database transactions
- **Error Handling**: Comprehensive error handling prevents data corruption
- **CORS Configuration**: Properly configured for React frontend

## Monitoring

### Health Checks
- Backend: `http://localhost:8080/api/users/health`
- Frontend: Application logs and error boundaries

### Logging
- Backend: SLF4J with detailed operation logging
- Frontend: Console logging for debugging

## Contributing

This project is designed to be beginner-friendly. Feel free to:
1. Study the code structure and comments
2. Modify and enhance features
3. Report issues or suggest improvements
4. Add new functionality following the existing patterns

## License

This project is open source and available under the MIT License.

## Support

For questions or issues:
1. Check the comprehensive code comments
2. Review the API documentation above
3. Examine the database schema
4. Test with the provided examples

The system is designed to be educational and easy to understand, making it perfect for learning Spring Boot, React, and MongoDB integration.
