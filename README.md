[![Java CI with Gradle](https://github.com/Amits64/auth_app/actions/workflows/gradle.yml/badge.svg)](https://github.com/Amits64/auth_app/actions/workflows/gradle.yml)
[![CodeQL](https://github.com/Amits64/auth_app/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/Amits64/auth_app/actions/workflows/github-code-scanning/codeql)
[![AWS_Setup_Deployment](https://github.com/Amits64/auth_app/actions/workflows/aws_setup.yaml/badge.svg)](https://github.com/Amits64/auth_app/actions/workflows/aws_setup.yaml)

# Auth App

Auth App is a Spring Boot application designed to handle authentication and authorization. It uses Java 17, Spring Boot, Spring Data JPA, Hibernate, and MySQL, and now supports **Prometheus monitoring** out of the box.

---

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Architectural Design](#architectural-design)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Monitoring with Prometheus](#monitoring-with-prometheus)
- [Testing](#testing)
- [Deployment](#deployment)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

---

## Features
- User registration and login
- JWT-based authentication
- Role-based authorization
- Secure password storage using bcrypt
- Spring Data JPA for database operations
- Prometheus-compatible metrics at `/actuator/prometheus`
- Docker-ready for local and cloud deployment

---

## Prerequisites
- Java 17
- Gradle
- MySQL
- Docker (for containerized deployment)
- Prometheus (for monitoring)
- Kubernetes (optional, for orchestration)

---

## Architectural Design
![image](https://github.com/user-attachments/assets/27307442-9d43-4d4b-aa40-c017d2a1132b)

---

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/auth_app.git
    cd auth_app
    ```

2. Build the project:
    ```sh
    gradle clean build
    ```

---

## Configuration

### `application.yaml`

Update your Spring Boot configuration to expose Prometheus metrics:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus,health,metrics,info
      base-path: /actuator
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: auth-app

security:
  filter:
    order: 0
  ignore:
    - /actuator/**
    - /actuator/prometheus
````

### MySQL Setup

Ensure MySQL is running and the `auth_db` database is created:

```sql
CREATE DATABASE auth_db;
```

---

## Running the Application

### Gradle

```sh
gradle bootRun
```

### Docker (Local Monitoring Network)

1. Create a Docker network for container communication:

   ```bash
   docker network create monitoring-net
   ```

2. Run MySQL (if not running already):

   ```bash
   docker run -d \
     --name mysql-docker \
     --network monitoring-net \
     -e MYSQL_ROOT_PASSWORD=yourpassword \
     -e MYSQL_DATABASE=auth_db \
     -p 3306:3306 \
     mysql:oraclelinux9
   ```

3. Run the Auth App:

   ```bash
   docker run -d \
     --name auth-container \
     --network monitoring-net \
     -p 8080:8080 \
     -e MYSQL_URL=jdbc:mysql://mysql-docker:3306/auth_db \
     -e MYSQL_USER=root \
     -e MYSQL_PASSWORD=yourpassword \
     amits64/auth_app:latest
   ```

---

## Monitoring with Prometheus

### `prometheus.yml`

Save the following config as `prometheus.yml`:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'spring-boot-auth-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['auth-container:8080']
        labels:
          instance: 'auth-app'
```

### Run Prometheus

```bash
docker run -d \
  --name prometheus \
  --network monitoring-net \
  -p 9090:9090 \
  -v /absolute/path/to/prometheus.yml:/opt/bitnami/prometheus/conf/prometheus.yml \
  bitnami/prometheus:latest
```

> Visit `http://localhost:9090/targets` to confirm your app is being scraped.

---

## Testing

```sh
gradle test
```

---

## Deployment

### Docker Build

```sh
docker build -t auth_app .
```

### Kubernetes

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

Deploy:

```sh
kubectl apply -f auth_app-deployment.yaml
kubectl apply -f auth_app-service.yaml
```
---
## Screenshots:
![image](https://github.com/user-attachments/assets/1ea11bf7-90e5-4ed2-8f1e-0a0795c5c55d)

---

## Usage

* **Register**: `POST /api/auth/register`
  Payload: `{ "username": "user", "password": "pass" }`

* **Login**: `POST /api/auth/login`
  Payload: `{ "username": "user", "password": "pass" }`

* **Access Protected Route**: `GET /api/user/profile`
  Header: `Authorization: Bearer <JWT>`

* **Metrics Endpoint**:
  Visit `http://localhost:8080/actuator/prometheus` to see Prometheus metrics output.

---

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more information.
