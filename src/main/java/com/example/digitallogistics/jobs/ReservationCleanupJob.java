package com.example.digitallogistics.jobs;

import com.example.digitallogistics.service.AdvancedLogisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationCleanupJob {

    private final AdvancedLogisticsService advancedLogisticsService;

    @Scheduled(fixedRate = 300000) // Toutes les 5 minutes
    public void cleanupExpiredReservations() {
        log.debug("Starting cleanup of expired reservations");
        advancedLogisticsService.releaseExpiredReservations();
        log.debug("Completed cleanup of expired reservations");
    }
}