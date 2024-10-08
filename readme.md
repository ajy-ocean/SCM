# SCM - Smart Contact Manager

## Project Overview

**SCM** (Smart Contact Manager) is a learning project aimed at creating an efficient contact management application. The main objective of this project is to provide a smarter way of handling and storing contacts with full authentication and authorization capabilities.

This project is built using **Spring Boot** and follows the **MVC architecture**. For authentication and authorization, we have implemented **Spring Security**. It is designed to perform CRUD operations efficiently on contacts with a user-friendly front-end.

## Technologies Used

### Backend:

- **Spring Boot**: Used for developing the backend of the application.
- **Spring Data JPA**: Used for performing database CRUD operations.
- **Spring Security**: Used for authentication and authorization mechanisms.

### Frontend:

- **HTML5**, **CSS3**, **Bootstrap**: Used for designing the user interface and creating responsive layouts.
- **Thymeleaf**: Used as the template engine for server-side rendering of views.

### Database:

- **MySQL**: Used for storing contact data.

## Features

- **Contact Management**: Create, Read, Update, and Delete contacts.
- **Authentication and Authorization**: Only authorized users can access and manage contacts.
- **Responsive UI**: Frontend is built using Bootstrap, ensuring the application is accessible on different devices.
- **Efficient Contact Storage**: Contacts are stored in a structured manner for easy access and management.

## Prerequisites

- Java 17+
- MySQL 8.x (or higher)
- Maven
- IDE (IntelliJ IDEA, Eclipse, etc.)
- Browser for viewing the application (Chrome, Firefox, etc.)

## Setup Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/smart-contact-manager.git
   ```

2. Navigate to the project directory:

   ```bash
   cd smart-contact-manager
   ```

3. Configure your MySQL database in `application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/your-database-name
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   ```

4. Run the project using Maven:

   ```bash
   mvn spring-boot:run
   ```

5. Access the application by navigating to `http://localhost:8080` in your browser.

## License

This project is for learning purposes and does not have a formal license.
