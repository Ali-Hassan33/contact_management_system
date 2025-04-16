package com.contact_management_system.scheduler;

import com.contact_management_system.repositories.MimeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.now;

@Component
public class ExpiredTokensScheduler {

    private final MimeRepository mimeRepository;

    public ExpiredTokensScheduler(MimeRepository mimeRepository) {
        this.mimeRepository = mimeRepository;
    }

    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void expiredTokens() {
        mimeRepository.deleteByCreatedAtBefore(valueOf(now().minusMinutes(15)));
    }
}
