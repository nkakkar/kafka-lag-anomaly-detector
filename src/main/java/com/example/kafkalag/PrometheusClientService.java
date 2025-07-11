package com.example.kafkalag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PrometheusClientService {

    private final WebClient.Builder webBuilder = WebClient.builder();

    @Value("${prometheus.base-url}")
    private String baseUrl;

    public Mono<Double> fetchCurrentLag(String promQlQuery) {
        String url = String.format("%s/api/v1/query?query=%s", baseUrl, promQlQuery);
        return webBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractValue);
    }

    private double extractValue(String json) {
        // Very naive parsing. Replace with proper JSON handling in prod.
        String marker = ""value":["";
        int idx = json.indexOf(marker);
        if (idx == -1) return 0;
        int start = idx + marker.length();
        int end = json.indexOf(""", start);
        String num = json.substring(start, end).split(",")[0];
        return Double.parseDouble(num);
    }
}
