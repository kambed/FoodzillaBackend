package pl.better.foodzillabackend.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.mail.model.domain.RecoveryCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecoveryCodeRepository extends JpaRepository<RecoveryCode, Long> {
    Optional<RecoveryCode> findByCodeAndEmail(String code, String email);
    boolean existsByEmail(String email);
    void removeByCodeAndEmail(String code, String email);
    void removeByEmail(String email);
    List<RecoveryCode> findAllByDateBefore(LocalDateTime date);
}
