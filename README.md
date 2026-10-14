# Ledger API

[Read in English](README.en.md)

API REST de um banco simplificado (contas e transferências), feita com Java 21 e Spring Boot 4.

**Status:** em desenvolvimento. Nada abaixo é dado como pronto até estar implementado, testado e mergeado.

## O que é

Um backend bancário pequeno, mas correto. O foco não é ter muitas features, e sim demonstrar fundamentos que o mercado financeiro avalia:

- **Transferências ACID:** débito, crédito, lançamentos no ledger e o registro da transferência acontecem na mesma transação. Qualquer falha desfaz tudo.
- **Idempotência:** repetir uma requisição com a mesma `Idempotency-Key` não duplica a transferência.
- **Concorrência:** bloqueio otimista evita perda de atualização quando transferências ocorrem ao mesmo tempo.
- **Testes:** unitários, de integração com PostgreSQL real (Testcontainers) e de concorrência.
- **Operação:** subir tudo com um comando via Docker Compose e pipeline de CI.

## Escopo

**Dentro:** cadastro e login (JWT) com perfis `CUSTOMER` e `ADMIN`; abertura e consulta de contas; transferências entre contas; extrato paginado; trilha de auditoria (somente `ADMIN`).

**Fora:** front-end, microsserviços, Kafka/RabbitMQ, Kubernetes, múltiplas moedas, juros, PIX/boleto reais.

## Stack (planejada)

Java 21, Spring Boot 4.x, Maven, PostgreSQL, Flyway, Spring Data JPA, Spring Security + JWT, Bean Validation, ProblemDetail, springdoc-openapi, JUnit 5, Mockito, Testcontainers, JaCoCo, Docker e GitHub Actions.

## Como rodar

Ainda não disponível. Esta seção será preenchida quando o `docker compose up` funcionar a partir de um clone limpo (meta: 3 comandos).

## Decisões técnicas

Em breve, em `docs/decisions.md` (por que bloqueio otimista, por que ledger, por que idempotência).

## O que eu faria diferente

Em breve, ao final do projeto.

## Modelo de dados, endpoints e roadmap

Detalhados em [README.en.md](README.en.md).

## Licença

MIT
