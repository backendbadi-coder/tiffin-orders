# Tiffin Orders (Spring Boot 4.2 + RabbitMQ AMQP 1.0)

A small Backend Badi project: the **counter** takes a tiffin order and publishes an event to RabbitMQ over **AMQP 1.0**; the **kitchen** listens to the queue and marks the order **COOKING**.

It started on Spring Boot 4.1 with AMQP 0.9.1 and was upgraded using Spring Boot 4.2's new AMQP 1.0.

```
POST /api/orders ──► OrderService ──save──► H2
                         │
                         └─ RabbitAmqpTemplate ──► exchange "tiffin.orders" ──(order.placed)──► queue "kitchen.orders"
                            (CompletableFuture:                                                       │
                             true = RabbitMQ accepted)                                                │
                         OrderService.startCooking ◄── @RabbitListener KitchenListener ◄──────────────┘
```

| | |
|---|---|
| Spring Boot | 4.2.0 |
| Java | 25 |
| Messaging | `spring-boot-starter-amqp-rabbitmq` (RabbitMQ, **AMQP 1.0**): `RabbitAmqpTemplate`, `@RabbitListener` |
| Broker | RabbitMQ 4.2 (Docker Compose) |
| Database | H2 in-memory |

## What's new with AMQP 1.0

| | Before (Boot 4.1, AMQP 0.9.1) | Now (Boot 4.2, AMQP 1.0) |
|---|---|---|
| Starter | `spring-boot-starter-amqp` | `spring-boot-starter-amqp-rabbitmq` |
| Sending | `RabbitTemplate` (blocking) | `RabbitAmqpTemplate`: async, returns `CompletableFuture<Boolean>` that completes when RabbitMQ **accepts** the message |
| Receiving | `@RabbitListener` | `@RabbitListener` (unchanged) |
| Exchange / queue / binding | Declared by `RabbitAdmin` | Same beans, declared by `RabbitAmqpAdmin` |
| Properties | `spring.rabbitmq.*` | `spring.amqp.rabbitmq.*` |
| Health component | `rabbit` | `amqprabbit` |

> In Spring Boot 4.2, `spring-boot-starter-amqp` means **generic AMQP 1.0** (Qpid Proton). To stay on AMQP 0.9.1, use `spring-boot-starter-rabbitmq` instead.

## Run

Requirements: JDK 25, Docker (see the Backend Badi Docker installation guide).

```bash
docker compose up -d                  # RabbitMQ on localhost:5672, UI on http://localhost:15672 (tiffin / tiffin)
mvn spring-boot:run                   # or run OrdersApplication in IntelliJ
```

Connection settings (`application.properties`):
```properties
spring.amqp.rabbitmq.host=localhost
spring.amqp.rabbitmq.port=5672
spring.amqp.rabbitmq.username=tiffin
spring.amqp.rabbitmq.password=tiffin
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
Counter → kitchen (AMQP 1.0): order 1 accepted by RabbitMQ
Kitchen received order 1: 2 x Masala Dosa for Ravi
```

**Windows PowerShell:** use `curl.exe` instead of `curl`, and `-o file.json` instead of `>`. For the POST, put the JSON in a file and send it with `--data "@order.json"`, or use Postman.

| Endpoint | |
|---|---|
| `POST /api/orders` | Place an order (`customerName`, `item`, `quantity` 1–20) → 201 |
| `GET /api/orders` | All orders |
| `GET /api/orders/{id}` | One order, or 404 ProblemDetail |
| `GET /actuator/health` | Includes the RabbitMQ AMQP 1.0 connection (`amqprabbit`) |

In the RabbitMQ UI:
- **Queues and Streams** tab: the `kitchen.orders` queue and its message rates.
- **Connections** tab: the **Protocol** column shows that the app connects over **AMQP 1.0**.

## Tests

```bash
mvn clean verify
```
- `OrderControllerTest`: web layer with MockMvc (4 tests)
- `OrderServiceTest`, `KitchenListenerTest`: plain unit tests (3 tests)
- `OrderFlowIntegrationTest`: real RabbitMQ with Testcontainers over AMQP 1.0 (1 test, **skipped when Docker isn't running**)

Stop RabbitMQ: `docker compose down`.
