# Load & Booking Management System

A comprehensive Spring Boot backend system with React frontend for managing cargo loads and transportation bookings efficiently.

## 🚀 Features

- **Load Management**: Create, update, view, and manage cargo loads
- **Booking System**: Handle booking requests with status transitions
- **Business Rules**: Automated status management and validation
- **REST APIs**: Comprehensive RESTful API with pagination and filtering
- **Data Validation**: Input validation with detailed error messages
- **Exception Handling**: Global exception handling with standardized responses
- **Database Integration**: PostgreSQL with JPA/Hibernate
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Frontend Interface**: Modern React application with Ant Design
- **Testing**: Comprehensive unit tests with 60%+ coverage

## 🏗️ Architecture

### Backend (Spring Boot 3+)
- **Controller Layer**: REST endpoints with validation
- **Service Layer**: Business logic and status management
- **Repository Layer**: Data access with custom queries
- **Entity Layer**: JPA entities with proper relationships
- **DTO Layer**: Request/response mapping
- **Exception Handling**: Global error management

### Frontend (React 18)
- **Components**: Modular React components
- **State Management**: React hooks and React Query
- **UI Framework**: Ant Design for modern interface
- **API Integration**: Axios for HTTP communication

### Database (PostgreSQL)
- **Normalized Schema**: Proper foreign key relationships
- **Indexes**: Optimized for performance
- **Constraints**: Data integrity enforcement

## 📊 Database Schema

### Load Entity
```sql
CREATE TABLE loads (
    id UUID PRIMARY KEY,
    shipper_id VARCHAR(255) NOT NULL,
    loading_point VARCHAR(255) NOT NULL,
    unloading_point VARCHAR(255) NOT NULL,
    loading_date TIMESTAMP NOT NULL,
    unloading_date TIMESTAMP NOT NULL,
    product_type VARCHAR(255) NOT NULL,
    truck_type VARCHAR(255) NOT NULL,
    no_of_trucks INTEGER NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    comment TEXT,
    date_posted TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'POSTED'
);
```

### Booking Entity
```sql
CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    load_id UUID NOT NULL,
    transporter_id VARCHAR(255) NOT NULL,
    proposed_rate DOUBLE PRECISION NOT NULL,
    comment TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMP NOT NULL,
    FOREIGN KEY (load_id) REFERENCES loads(id)
);
```

## 🔧 Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Validation**
- **PostgreSQL 15**
- **Maven**
- **JUnit 5 & Mockito**
- **Swagger/OpenAPI 3**

### Frontend
- **React 18**
- **React Router DOM**
- **Ant Design**
- **React Query**
- **Axios**
- **Moment.js**

### Infrastructure
- **Docker & Docker Compose**
- **PostgreSQL Container**
- **PgAdmin Container**

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 16+
- Docker & Docker Compose
- Maven 3.6+

### 1. Clone Repository
```bash
git clone <repository-url>
cd load-booking-system
```

### 2. Start Database
```bash
# Start PostgreSQL and PgAdmin
docker-compose up -d

# Verify containers are running
docker-compose ps
```

**Database Access:**
- **PostgreSQL**: localhost:5432
- **PgAdmin**: http://localhost:5050
  - Email: admin@cargopro.ai
  - Password: admin123

### 3. Start Backend
```bash
# Build and run Spring Boot application
mvn clean install
mvn spring-boot:run

# Application will start on http://localhost:8080
```

**API Documentation:** http://localhost:8080/swagger-ui.html

### 4. Start Frontend
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start

# Application will start on http://localhost:3000
```

## 📚 API Documentation

### Load Management APIs

#### Create Load
```http
POST /api/load
Content-Type: application/json

{
  "shipperId": "SHIPPER001",
  "facility": {
    "loadingPoint": "New York",
    "unloadingPoint": "Los Angeles",
    "loadingDate": "2024-01-15T08:00:00",
    "unloadingDate": "2024-01-18T17:00:00"
  },
  "productType": "Electronics",
  "truckType": "Flatbed",
  "noOfTrucks": 2,
  "weight": 1500.0,
  "comment": "Handle with care"
}
```

#### Get Loads with Filters
```http
GET /api/load?shipperId=SHIPPER001&truckType=Flatbed&status=POSTED&page=1&size=10
```

#### Get Load by ID
```http
GET /api/load/{loadId}
```

#### Update Load
```http
PUT /api/load/{loadId}
Content-Type: application/json

{
  "shipperId": "SHIPPER001",
  "facility": { ... },
  "productType": "Updated Product",
  ...
}
```

#### Delete Load (Cancel)
```http
DELETE /api/load/{loadId}
```

### Booking Management APIs

#### Create Booking
```http
POST /api/booking
Content-Type: application/json

{
  "loadId": "uuid-here",
  "transporterId": "TRANSPORTER001",
  "proposedRate": 2500.0,
  "comment": "Urgent delivery required"
}
```

#### Get Bookings with Filters
```http
GET /api/booking?loadId=uuid&transporterId=TRANSPORTER001&status=PENDING
```

#### Accept/Reject Booking
```http
PUT /api/booking/{bookingId}/accept
PUT /api/booking/{bookingId}/reject
```

## 🔄 Business Rules

### Load Status Transitions
- **POSTED** → **BOOKED** (when booking is created)
- **BOOKED** → **POSTED** (when all bookings are deleted/rejected)
- **POSTED/BOOKED** → **CANCELLED** (when load is deleted)
- **CANCELLED** → No transitions allowed

### Booking Status Transitions
- **PENDING** → **ACCEPTED** (manual action)
- **PENDING** → **REJECTED** (manual action)
- When booking is accepted, all other pending bookings for the same load are rejected

### Validation Rules
- Load cannot be updated if status is CANCELLED
- Booking cannot be created for CANCELLED loads
- Only PENDING bookings can be accepted/rejected
- All required fields must be provided
- Weight and proposed rate must be greater than 0
- Loading date must be before unloading date

## 🧪 Testing

### Run Backend Tests
```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Coverage
The project maintains 60%+ test coverage with comprehensive unit tests for:
- Service layer business logic
- Controller endpoints
- Repository queries
- Exception handling
- Validation scenarios

### Frontend Testing
```bash
cd frontend
npm test
```

## 🔧 Configuration

### Application Properties
```yaml
# src/main/resources/application.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/load_booking_db
    username: postgres
    password: postgres
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Environment Variables
```bash
# Optional environment variables
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/load_booking_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
```

## 🚀 Deployment

### Production Build

#### Backend
```bash
# Create production JAR
mvn clean package -Dspring.profiles.active=prod

# Run production build
java -jar target/load-booking-system-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd frontend

# Create production build
npm run build

# Serve static files (with nginx or serve)
npx serve -s build
```

### Docker Deployment
```bash
# Build and run all services
docker-compose -f docker-compose.prod.yml up -d
```

## 📋 Sample Data

### Create Sample Load
```bash
curl -X POST http://localhost:8080/api/load \
  -H "Content-Type: application/json" \
  -d '{
    "shipperId": "SHIPPER001",
    "facility": {
      "loadingPoint": "New York",
      "unloadingPoint": "Los Angeles",
      "loadingDate": "2024-01-15T08:00:00",
      "unloadingDate": "2024-01-18T17:00:00"
    },
    "productType": "Electronics",
    "truckType": "Flatbed",
    "noOfTrucks": 2,
    "weight": 1500.0,
    "comment": "Handle with care"
  }'
```

### Create Sample Booking
```bash
# First get the load ID from the previous response
curl -X POST http://localhost:8080/api/booking \
  -H "Content-Type: application/json" \
  -d '{
    "loadId": "your-load-id-here",
    "transporterId": "TRANSPORTER001",
    "proposedRate": 2500.0,
    "comment": "Urgent delivery required"
  }'
```

## 🔍 Monitoring & Debugging

### Application Logs
```bash
# View backend logs
tail -f logs/application.log

# Enable debug logging
export SPRING_PROFILES_ACTIVE=debug
```

### Database Access
- **PgAdmin**: http://localhost:5050
- **Direct Connection**: 
  ```bash
  psql -h localhost -p 5432 -U postgres -d load_booking_db
  ```

### Health Checks
```bash
# Backend health
curl http://localhost:8080/actuator/health

# Database connection
curl http://localhost:8080/actuator/health/db
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- **Email**: careers@cargopro.ai
- **Documentation**: Check the `/docs` folder for detailed API specs
- **Issues**: Create an issue in the repository

## 🔄 Changelog

### Version 1.0.0
- Initial release
- Load management functionality
- Booking system with status transitions
- React frontend interface
- Comprehensive test coverage
- Docker deployment setup
- API documentation with Swagger

---

**Built with ❤️ for CargoPro Software Development Internship**
