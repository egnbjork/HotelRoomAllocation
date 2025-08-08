# RoomAllocator

RoomAllocator is a Spring Boot application that optimally assigns hotel rooms (Premium and Economy) to guests based on their willingness to pay. It uses Hexagonal architecture (Ports & Adapters), includes validation, and supports structured testing and deployment.

---

## 📦 Features

- Room allocation logic using domain-driven design
- Hexagonal architecture for testability and scalability
- Request validation via Hibernate Validator (JSR-380)
- Custom error handling via `@ControllerAdvice`
- Fully tested with JUnit and MockMvc
- `.env` support for configuration values
- documentation (requirements, use case and class diagrams) in docs folder

---

## 🧰 Requirements

- Java 21 (Temurin recommended)
- Gradle 8+ (wrapper included)
- Docker (optional for deployment/testing)

---

## 🔧 Build

```bash
./gradlew clean build
