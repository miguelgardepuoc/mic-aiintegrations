# Antharos Artificial Intelligence Service

## Overview

The Artificial Intelligence service is a microservice within the Antharos HR platform ecosystem. It is designed to facilitate seamless integrations with artificial intelligence systems. It provides APIs to interact with AI models. This service is built following a hexagonal architecture pattern.

## Technology Stack

- **Framework**: Spring Boot 3.4
- **Build Tool**: Maven
- **Language**: Java 21
- **Architecture**: Hexagonal Architecture
- **Event Bus**: Azure Service Bus
- **Models**: Stanford Core NLP https://stanfordnlp.github.io/CoreNLP/

## Domain Events

The service publishes the following events:
- `CompleteNameExtractedFromCv`

And consumes the following domain events:
- `CandidateApplied`

## Getting Started

### Prerequisites

- JDK 21+
- Maven 3.9+
- Docker & Docker Compose

### Installation

```bash
# Clone the repository
git clone https://github.com/miguelgardepuoc/mic-aiintegrations.git
cd mic-aiintegrations

# Build the project
./mvnw clean install -U
```

### Running Locally

```bash
# Start all dependencies with Docker Compose
docker-compose up --build -d --remove-orphans
```

```bash
# Run the service
mvn spring-boot:run
```

The service will be available at `http://localhost:8087/aiintegrations`.
APIs documentation will be available at `http://localhost:8087/aiintegrations/swagger-ui/index.html`.

## Format code

Code is formatted using spotless-maven-plugin applying google java format:
```bash
mvn spotless:apply
```

## Code coverage

Code coverage is measured using JaCoCo. To generate the report:
```bash
mvn clean verify
```
This command will execute all tests and generate JaCoCo reports. An aggregated report is generated under the `aggregate-report` module.

To view the full aggregated coverage report, open the following file in your browser:
```pgsql
aggregate-report/target/site/jacoco-aggregate/index.html
```
This report shows consolidated coverage data across the entire repository.
