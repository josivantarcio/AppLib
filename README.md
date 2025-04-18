# Library Management System

A library management system developed with Spring Boot, PostgreSQL, and Redis.

## Requirements

- Java 21
- Docker
- Docker Compose
- Maven 3.9+

## Environment Setup

1. Clone the repository:
```bash
git clone https://github.com/your-username/library.git
cd library
```

2. Configure environment variables:
```bash
cp .env.example .env
# Edit the .env file with your configurations
```

3. Start the containers:
```bash
docker-compose up -d
```

4. Access the application:
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- Health Check: http://localhost:8080/api/actuator/health

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── library/
│   │           ├── config/
│   │           ├── controller/
│   │           ├── dto/
│   │           ├── model/
│   │           ├── repository/
│   │           ├── service/
│   │           └── util/
│   └── resources/
│       └── application.properties
├── test/
│   └── java/
│       └── com/
│           └── library/
└── pom.xml
```

## Development

1. To run locally:
```bash
mvn spring-boot:run
```

2. To run tests:
```bash
mvn test
```

3. To generate coverage report:
```bash
mvn jacoco:report
```

## API Documentation

API documentation is available through Swagger UI at:
http://localhost:8080/api/swagger-ui.html

## Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Contact

Josevan Oliveira - josivantarcio@msn.com
https://www.linkedin.com/in/josevanoliveira/

Project Link: [https://github.com/your-username/library](https://github.com/your-username/library)
