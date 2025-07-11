# Kafka Lag Anomaly Detector (Java, Spring Boot)

Monitors Kafka consumer-group lag via Prometheus, learns normal hourly patterns, and fires Grafana Alertmanager webhooks when lag exceeds normal by a configurable multiplier.

## Quick Start

```bash
git clone <your-repo>
cd kafka-lag-anomaly-detector-full
mvn spring-boot:run
```

### Configuration (`src/main/resources/application.yml`)

- `prometheus.base-url` – Prometheus host
- `prometheus.query` – PromQL query returning total lag
- `alertmanager.webhook-url` – Grafana/Alertmanager webhook
- `anomaly.multiplier` – threshold (`current > avg * multiplier`)
- `ai.*` – optional OpenAI GPT integration (not enabled by default)

## Docker Compose (optional)

```bash
docker compose up
```

Launches Prometheus, Alertmanager, and this service.

## OpenAI Integration

Set `ai.enabled=true` and `OPENAI_API_KEY` env var to let the service call GPT‑4o and enrich alerts with potential root‑cause hints.

## Building

```bash
mvn package
```

## Testing

```bash
mvn test
```
