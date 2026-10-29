# Saloon Booking Backend  Application
**Saloon Booking System** — A Java Spring Boot microservices-based application for managing saloons, services, users, and bookings. Built with Spring Boot, Spring Cloud, Eureka Service Discovery, API Gateway, Keycloak authentication, Feign Client, and MySQL.

## Run the backend with Docker Compose

From this directory, start the full local stack (Docker Compose builds each Spring service from its source):

```bash
docker compose -f docker-compose/default/docker-compose.yml up --build
```

Optional local settings go in a `.env` file in this directory. Use strong values for the database, RabbitMQ, and Keycloak passwords before exposing these development services beyond your machine. Stripe values are needed only for checkout:

```dotenv
MYSQL_ROOT_PASSWORD=replace-me
RABBITMQ_USERNAME=saloon
RABBITMQ_PASSWORD=replace-me
KEYCLOAK_ADMIN_USERNAME=admin
KEYCLOAK_ADMIN_PASSWORD=replace-me
STRIPE_API_KEY=
STRIPE_WEBHOOK_SECRET=
```

The stack includes MySQL databases for each service, Eureka, the API Gateway, RabbitMQ, and Keycloak with the `saloon` realm imported on first startup. The services and local dashboards are exposed on their usual ports; Keycloak is at `http://localhost:8080`, and RabbitMQ management is at `http://localhost:15672`. To stop the stack, run `docker compose -f docker-compose/default/docker-compose.yml down`.
