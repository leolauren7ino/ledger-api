# Ledger API

[Leia em português](README.md)

Simplified banking REST API built with Java 21 and Spring Boot 4.

**Status:** work in progress. Nothing below is claimed as done until it is implemented, tested and merged.

## Goal

Provide a small but correct banking backend that demonstrates:

- **ACID transfers:** debit, credit, ledger entries and the transfer record are written in a single transaction. Any failure rolls everything back.
- **Idempotency:** repeating a request with the same `Idempotency-Key` never duplicates a transfer.
- **Concurrency control:** optimistic locking prevents lost updates when transfers run simultaneously.
- **Testing:** unit tests, integration tests with real PostgreSQL (Testcontainers) and a concurrency test.
- **Operations:** one-command startup with Docker Compose and a CI pipeline.

## Scope

**In scope**

- User registration and login (JWT) with roles `CUSTOMER` and `ADMIN`
- Account creation and lookup
- Transfers between accounts
- Paginated account statement built from ledger entries
- Audit trail, readable by `ADMIN` only

**Out of scope**

Front-end, microservices, Kafka/RabbitMQ, Kubernetes, multiple currencies, interest, real PIX/boleto integration.

## Tech stack (planned)

Java 21, Spring Boot 4.x, Maven, PostgreSQL, Flyway, Spring Data JPA, Spring Security + JWT, Bean Validation, ProblemDetail (RFC 7807), springdoc-openapi, JUnit 5, Mockito, Testcontainers, JaCoCo, Docker (multi-stage) and GitHub Actions.

## Data model (draft)

| Table | Columns |
|---|---|
| `users` | `id`, `email` (unique), `password_hash`, `role` (`CUSTOMER` \| `ADMIN`), `created_at` |
| `accounts` | `id`, `user_id`, `number` (unique), `balance` (`NUMERIC(19,2)`), `version` (optimistic lock), `status`, `created_at` |
| `transfers` | `id`, `idempotency_key` (unique per user), `from_account_id`, `to_account_id`, `amount`, `status` (`COMPLETED` \| `FAILED`), `created_at` |
| `ledger_entries` | `id`, `transfer_id`, `account_id`, `type` (`DEBIT` \| `CREDIT`), `amount`, `balance_after`, `created_at` |
| `audit_log` | `id`, `actor_user_id`, `action`, `entity`, `entity_id`, `created_at` |

Rules:

- Money is always `BigDecimal` in Java and `NUMERIC` in PostgreSQL, never `double` or `float`.
- Schema changes go through Flyway migrations only, never `ddl-auto=update`.

## Endpoints (draft)

| Method | Path | Description | Access |
|---|---|---|---|
| POST | `/auth/register` | Create a user | Public |
| POST | `/auth/login` | Obtain a JWT | Public |
| POST | `/accounts` | Open an account | Customer |
| GET | `/accounts/{id}` | Get own account | Customer |
| POST | `/transfers` | Transfer funds (requires `Idempotency-Key` header) | Customer |
| GET | `/accounts/{id}/statement?page=&size=&from=&to=` | Paginated statement | Customer |
| GET | `/admin/audit` | Audit trail | Admin |

## Business rules

- A customer can only access their own accounts.
- A transfer requires: amount > 0, two different accounts, sufficient balance and an active source account.
- Same `Idempotency-Key` and same body returns the original result. Same key with a different body is rejected (409 or 422).
- Concurrent updates are handled with optimistic locking (`@Version`). Accounts are loaded in id order to avoid deadlocks.

## Roadmap

- [ ] Project skeleton, PostgreSQL, Flyway, Docker Compose
- [ ] JWT authentication, roles, validation, error handling
- [ ] Accounts and ACID transfers with ledger
- [ ] Idempotency
- [ ] Optimistic locking and concurrency test
- [ ] Mockito and Testcontainers test suite
- [ ] JaCoCo, GitHub Actions, OpenAPI
- [ ] Architecture diagram and design decisions (`docs/decisions.md`)

## Running locally

Not available yet. This section will be filled in once `docker compose up` works from a clean checkout.

## License

MIT
