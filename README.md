# Microservices

Учебный проект микросервисной архитектуры на Spring Boot и Spring Cloud.

## Запуск

Запускать сервисы в следующем порядке:

1. Config Server — `8888`
2. Discovery Server — `8761`
3. User Service — `8080`
4. API Gateway — `8081`

Основные запросы выполняются через API Gateway:

`GET http://localhost:8081/users`

Eureka Dashboard:

`http://localhost:8761`

## Используемые технологии

- Java 25
- Spring Boot
- Spring Cloud
- Spring Cloud Gateway
- Spring Cloud Config
- Netflix Eureka
- Resilience4j
- PostgreSQL