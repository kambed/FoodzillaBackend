package pl.better.foodzillabackend.mail.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.mail.repository.RecoveryCodeRepository;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ScheduledRecoveryCodeConfig {

    private final RecoveryCodeRepository repository;

    @Scheduled(cron = "${spring.code.config.taskDelay}")
    @Transactional
    public void clearOldCodes() {
        repository.findAllByDateBefore(LocalDateTime.now().minusDays(1))
                .forEach(code -> repository.removeByCodeAndEmail(code.getCode(), code.getEmail()));
        log.info("Old codes deleted");
    }
}
