package com.example.kafkalag.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LagAnomalyDetector {

    private final PrometheusClientService promService;
    private final GrafanaAlertNotifier notifier;

    @Value("${prometheus.query}")
    private String query;

    @Value("${anomaly.multiplier:2.0}")
    private double multiplier;

    private final Map<Integer, DoubleSummaryStatistics> hourlyStats = new ConcurrentHashMap<>();

    public LagAnomalyDetector(PrometheusClientService promService,
                              GrafanaAlertNotifier notifier) {
        this.promService = promService;
        this.notifier = notifier;
    }

    @Scheduled(fixedDelayString = "300000") // 5 minutes
    public void checkLag() {
        promService.fetchCurrentLag(query)
                .onErrorResume(e -> {
                    log.error("Error fetching lag", e);
                    return Mono.empty();
                })
                .subscribe(this::processLag);
    }

    private void processLag(double current) {
        int hour = LocalDateTime.now().getHour();
        hourlyStats.computeIfAbsent(hour, h -> new DoubleSummaryStatistics()).accept(current);

        double avg = hourlyStats.get(hour).getAverage();
        log.info("Hour {} -> avg={}, current={}", hour, avg, current);

        if (avg > 0 && current > avg * multiplier) {
            notifier.sendAlert(current, avg, hour);
        }
    }
}
