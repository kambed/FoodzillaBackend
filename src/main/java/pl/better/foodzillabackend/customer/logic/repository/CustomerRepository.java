package pl.better.foodzillabackend.customer.logic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT u FROM Customer u WHERE u.id = :id")
    List<Customer> getUserById(long id);
    Optional<Customer> findByUsername(String username);
    Optional<Customer> findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Customer> findCustomerByEmail(String email);
}
