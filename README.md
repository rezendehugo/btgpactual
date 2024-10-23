# Order Management Service

A microservice built with Spring Boot for handling customer orders, featuring Spring Boot, Docker, MongoDB persistence and RabbitMQ integration.

## Features

- Create new orders via REST API
- List customer orders with pagination
- Calculate order totals
- Async order processing with RabbitMQ
- MongoDB document storage

## Tech Stack

- Java
- Spring Boot
- MongoDB
- RabbitMQ
- Maven
- Docker

## API Endpoints


### Create Order
```http
POST /orders
```
### Get Orders from ID
```http
GET /customers/{customerId}/orders?Page=0&PageSize=10
```
## Getting Started

- Clone the repository
- Docker compose up on /local
- mvn clean install
- mvn spring-boot:run
