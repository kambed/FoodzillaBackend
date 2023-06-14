package pl.better.foodzillabackend.mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.mail.model.domain.RecoveryCode;

@Repository
public interface RecoveryCodeRepository extends JpaRepository<RecoveryCode, Long> {
    boolean existsByCodeAndEmail(String code, String email);
    boolean existsByEmail(String email);
    void removeByCodeAndEmail(String code, String email);
    void removeByEmail(String email);
}
