package com.example.kafkalag.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LagAnomalyDetectorTest {

    @Test
    void basicStats() {
        // Simple sanity test for statistics
        java.util.DoubleSummaryStatistics stats = new java.util.DoubleSummaryStatistics();
        stats.accept(1); stats.accept(3);
        assertEquals(2.0, stats.getAverage());
    }
}
