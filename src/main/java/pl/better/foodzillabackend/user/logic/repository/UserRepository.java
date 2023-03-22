package pl.better.foodzillabackend.user.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.user.logic.model.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
