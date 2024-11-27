# TrimbleCars Application

**TrimbleCars** is a car leasing application designed to cater to three types of users: **Admin**, **Customer**, and **Owner**. This application provides robust features for car management, leasing, and tracking lease history.  

## Features
- **Car Management**:
  - Register cars by Admin or Owners.
  - Update car status.
  - View cars owned by a user.
  - Fetch all available or leased cars.
- **Lease Management**:
  - Start and end leases.
  - Fetch lease history for individual users or all users.
  - Export lease history as a PDF.
- **User Management**:
  - View all registered users (Admin-only).

---

## Table of Contents
- [Features](#features)
- [System Roles](#system-roles)
- [API Endpoints](#api-endpoints)
- [Technologies Used](#technologies-used)
- [Setup Instructions](#setup-instructions)

---

## System Roles

1. **Admin**:
   - Full access to manage users, cars, and leases.
   - Ability to register cars on behalf of owners.
   - Ability to start lease on behalf of customer.
   - Can fetch or export all lease history.

2. **Owner**:
   - Manage cars they own.
   - Register new cars under their ownership.
   - View cars they have listed.

3. **Customer**:
   - Lease available cars.
   - View their lease history.
   - Export their lease history as a PDF.

---

## API Endpoints

### **Car Management**
| HTTP Method | Endpoint                       | Roles               | Description                                      |
|-------------|--------------------------------|---------------------|--------------------------------------------------|
| POST        | `/cars/register`              | Admin, Owner        | Register a new car.                             |
| PUT         | `/cars/update`                | Admin, Owner        | Update the status of a car.                     |
| GET         | `/cars/getAllCars`            | Admin               | Fetch a list of all registered cars.            |
| GET         | `/cars/getCarsByOwner`        | Admin, Owner        | Fetch cars owned by the authenticated user.     |
| GET         | `/cars/getAvailableCars`      | All Users           | Fetch all available cars for leasing.           |
| GET         | `/cars/getLeasedCars`         | All Users           | Fetch all currently leased cars.                |

---

### **Lease Management**
| HTTP Method | Endpoint                       | Roles               | Description                                      |
|-------------|--------------------------------|---------------------|--------------------------------------------------|
| POST        | `/lease/startLease`           | Admin, Customer     | Start a lease for a car.                        |
| POST        | `/lease/endLease`             | Admin, Customer     | End a lease for a car.                          |
| GET         | `/lease/getLease`             | Admin               | Fetch lease details by ID.                      |
| GET         | `/lease/getAllHistory`        | Admin, Customer     | Fetch lease history for the user or all users.  |
| GET         | `/lease/getAllHistoryAsPDF`   | Admin, Customer     | Export lease history as a PDF file.             |

---

### **User Management**
| HTTP Method | Endpoint                       | Roles               | Description                                      |
|-------------|--------------------------------|---------------------|--------------------------------------------------|
| GET         | `/user/getAllUsers`            | Admin               | Fetch all registered users.                      |
| PUT         | `/auth/update`                 | User,Customer       | Update your role to customer or user             |
---

## Technologies Used
- **Backend**: Spring Boot 3.4.0 ,Java 21
- **Security**: Spring Security with Role-based Access Control (RBAC)
- **Logging**: Log4j
- **Database**: Hsql (In memory database)
- **Build Tool**: Gradle 8+
- **PDF Generation**: iText (for lease history export)

---

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Gradle 8+

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/trimblecars/TrimbleCars.git
   cd TrimbleCars
2. Uncomment and Configure google and github client id and secret in 
   ```bash
   src/main/resources/security.yml

3. Build using gradle: (Gradle 8+ and JDK 17+)
   ```bash
   gradle -Dorg.gradle.java.home=/path/to/java17+ clean build

4. Run the extracted Jar.
   ```bash
   cd build/libs
   java -jar TrimbleCars-0.0.1-SNAPSHOT.jar 

5. Access the application
   ```bash
   http://localhost:8888/login

6. Login with github or google

7. Test the apis in postman
