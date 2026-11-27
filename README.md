# Tiffin Orders (Spring Boot 4.1 + RabbitMQ AMQP 0.9.1)

A small Backend Badi project: the **counter** takes a tiffin order and publishes an event to RabbitMQ; the **kitchen** listens to the queue and marks the order **COOKING**.


```
POST /api/orders ──► OrderService ──save──► H2
                         │
                         └─ RabbitTemplate ──► exchange "tiffin.orders" ──(order.placed)──► queue "kitchen.orders"
                                                                                                   │
                         OrderService.startCooking ◄── @RabbitListener KitchenListener ◄───────────┘
```

| | |
|---|---|
| Spring Boot | 4.1.3 |
| Java | 25 |
| Messaging | `spring-boot-starter-amqp` (RabbitMQ, AMQP 0.9.1): `RabbitTemplate`, `@RabbitListener` |
| Broker | RabbitMQ 4.2 (Docker Compose) |
| Database | H2 in-memory |

## Run

Requirements: JDK 25, Docker (see the Backend Badi Docker installation guide).

```bash
docker compose up -d                  # RabbitMQ on localhost:5672, UI on http://localhost:15672 (tiffin / tiffin)
mvn spring-boot:run                   # or run OrdersApplication in IntelliJ
```

Place an order:
```bash
curl -i -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"Ravi","item":"Masala Dosa","quantity":2}'
```
The response says `"status":"PLACED"`. A moment later:
```bash
curl http://localhost:8080/api/orders/1     # "status":"COOKING"
```
The log shows both sides:
```
Counter → kitchen: order 1 (2 x Masala Dosa)
Kitchen received order 1: 2 x Masala Dosa for Ravi
```

PowerShell: use `curl.exe`, or Postman.

| Endpoint | |
|---|---|
| `POST /api/orders` | Place an order (`customerName`, `item`, `quantity` 1–20) → 201 |
| `GET /api/orders` | All orders |
| `GET /api/orders/{id}` | One order, or 404 ProblemDetail |
| `GET /actuator/health` | Includes the RabbitMQ connection (`rabbit`) |

In the RabbitMQ UI (**Queues and Streams** tab) you can see the `kitchen.orders` queue and its message rates.

## Tests

```bash
mvn clean verify
```
- `OrderControllerTest`: web layer with MockMvc (4 tests)
- `OrderServiceTest`, `KitchenListenerTest`: plain unit tests (3 tests)
- `OrderFlowIntegrationTest`: real RabbitMQ with Testcontainers (1 test, **skipped when Docker isn't running**)

Stop RabbitMQ: `docker compose down`.
