[![Java CI with Gradle](https://github.com/Amits64/auth_app/actions/workflows/gradle.yml/badge.svg)](https://github.com/Amits64/auth_app/actions/workflows/gradle.yml)
[![CodeQL](https://github.com/Amits64/auth_app/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/Amits64/auth_app/actions/workflows/github-code-scanning/codeql)
[![AWS_Setup_Deployment](https://github.com/Amits64/auth_app/actions/workflows/aws_setup.yaml/badge.svg)](https://github.com/Amits64/auth_app/actions/workflows/aws_setup.yaml)

# Auth App

Auth App is a Spring Boot application designed to handle authentication and authorization. This application is built with Java 17 and utilizes Spring Boot, Spring Data JPA, Hibernate, and MySQL.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Architectural Design](#architectural-design)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Deployment](#deployment)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Features
- User registration and login
- JWT-based authentication
- Role-based authorization
- Secure password storage using bcrypt
- Spring Data JPA for database operations

## Prerequisites
- Java 17
- Gradle
- MySQL
- Docker (for containerized deployment)
- Kubernetes (optional, for orchestration)

## Architectural Design
![image](https://github.com/user-attachments/assets/27307442-9d43-4d4b-aa40-c017d2a1132b)


## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/auth_app.git
    cd auth_app
    ```

2. Install the required dependencies:
    ```sh
    gradle clean build
    ```

## Configuration

1. Update the `application.properties` file located in `src/main/resources` with your MySQL database credentials:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/auth_db
    spring.datasource.username=root
    spring.datasource.password=yourpassword
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
    ```

2. Ensure MySQL is running and a database named `auth_db` is created:
    ```sql
    CREATE DATABASE auth_db;
    ```

## Running the Application

1. Run the application using Gradle:
    ```sh
    gradle bootRun
    ```

2. The application will start and be accessible at `http://localhost:8080`.

## Testing

1. Run the tests using Gradle:
    ```sh
    gradle test
    ```

## Deployment

### Docker

1. Build the Docker image:
    ```sh
    docker build -t auth_app .
    ```

2. Run the Docker container:
    ```sh
    docker run -p 8080:8080 -e MYSQL_URL=jdbc:mysql://host.docker.internal:3306/auth_db -e MYSQL_USER=root -e MYSQL_PASSWORD=yourpassword auth_app
    ```

### Kubernetes

1. Create Kubernetes deployment and service files (`auth_app-deployment.yaml`, `auth_app-service.yaml`):
    ```yaml
    # auth_app-deployment.yaml
    apiVersion: apps/v1
    kind: Deployment
    metadata:
      name: auth-app
    spec:
      replicas: 2
      selector:
        matchLabels:
          app: auth-app
      template:
        metadata:
          labels:
            app: auth-app
        spec:
          containers:
            - name: auth-app
              image: auth_app:latest
              ports:
                - containerPort: 8080
              env:
                - name: MYSQL_URL
                  value: "jdbc:mysql://mysql-service:3306/auth_db"
                - name: MYSQL_USER
                  value: "root"
                - name: MYSQL_PASSWORD
                  value: "yourpassword"
    ```

    ```yaml
    # auth_app-service.yaml
    apiVersion: v1
    kind: Service
    metadata:
      name: auth-app
    spec:
      selector:
        app: auth-app
      ports:
        - protocol: TCP
          port: 80
          targetPort: 8080
      type: LoadBalancer
    ```

2. Deploy to Kubernetes:
    ```sh
    kubectl apply -f auth_app-deployment.yaml
    kubectl apply -f auth_app-service.yaml
    ```
## Screenshot:
![image](https://github.com/user-attachments/assets/91b78982-f8ba-4994-a77f-8fb658997a87)

## Usage
- **Register User**: Send a POST request to `/api/auth/register` with JSON payload containing `username` and `password`.
- **Login User**: Send a POST request to `/api/auth/login` with JSON payload containing `username` and `password`.
- **Access Protected Resource**: Send a GET request to `/api/user/profile` with JWT token in the `Authorization` header.

## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
