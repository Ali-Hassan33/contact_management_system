package com.contact_management_system.scheduler;

import com.contact_management_system.entities.Mime;
import com.contact_management_system.repositories.MimeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.now;

@Component
@Slf4j
public class ExpiredTokensScheduler {

    private final MimeRepository mimeRepository;

    public ExpiredTokensScheduler(MimeRepository mimeRepository) {
        this.mimeRepository = mimeRepository;
    }

    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void expiredTokens() {
        List<Mime> deletedTokens = mimeRepository.deleteByCreatedAtBefore(valueOf(now().minusMinutes(15)));
        log.info("Successfully deleted expired tokens {}", deletedTokens.stream().map(Mime::getUuid).toList());
    }
}
