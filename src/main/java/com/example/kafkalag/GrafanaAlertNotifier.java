package com.example.kafkalag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrafanaAlertNotifier {

    @Value("${alertmanager.webhook-url}")
    private String webhook;

    public void sendAlert(double current, double avg, int hour) {
        Map<String,Object> body = Map.of(
                "alerts", new Object[] {
                        Map.of(
                                "status","firing",
                                "labels", Map.of("alertname","ConsumerLagSpike"),
                                "annotations", Map.of(
                                        "summary","Consumer lag anomaly",
                                        "description",String.format("Hour %02d: lag %.2f exceeds avg %.2f", hour, current, avg)
                                )
                        )
                }
        );
        WebClient.create()
                .post()
                .uri(webhook)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("Alert send failed", e))
                .doOnSuccess(r -> log.info("Alert sent"))
                .subscribe();
    }
}
