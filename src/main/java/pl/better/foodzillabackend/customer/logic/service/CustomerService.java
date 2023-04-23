package pl.better.foodzillabackend.customer.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.customer.logic.mapper.CustomerDtoMapper;
import pl.better.foodzillabackend.customer.logic.model.command.CreateCustomerCommand;
import pl.better.foodzillabackend.customer.logic.model.command.UpdateCustomerCommand;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.model.dto.CustomerDto;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;
import pl.better.foodzillabackend.exceptions.type.CustomerAlreadyExistsException;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.recommendation.logic.service.RecommendationService;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {
    private final RecommendationService recommendationService;
    private static final String CUSTOMER_NOT_FOUND = "Customer with username %s not found";
    private static final String CUSTOMER_ALREADY_EXIST = "Customer with username %s already exists";
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final CustomerRepository repository;
    private final CustomerDtoMapper mapper;

    @Transactional
    public CustomerDto createNewUserAndSaveInDb(CreateCustomerCommand command) {
        if (!exists(command)) {
            Customer user = Customer.builder()
                    .firstname(command.firstname())
                    .lastname(command.lastname())
                    .username(command.username())
                    .password(passwordEncoder.encode(command.password()))
                    .build();

            repository.saveAndFlush(user);
            recommendationService.recommend(command.username());
            return mapper.apply(user);
        } else {
            throw new CustomerAlreadyExistsException(CUSTOMER_ALREADY_EXIST.formatted(command.username()));
        }
    }

    @Transactional
    public CustomerDto editCustomer(String principal, UpdateCustomerCommand command) {
        Customer user = repository.findByUsername(principal).orElseThrow(() -> new CustomerNotFoundException(
                CUSTOMER_NOT_FOUND.formatted(principal)
        ));
        if (!repository.existsByUsername(command.username()) || principal.equals(command.username())) {
            user.setFirstname(command.firstname());
            user.setLastname(command.lastname());
            user.setUsername(command.username());
            user.setPassword(passwordEncoder.encode(command.password()));
            repository.saveAndFlush(user);
            return mapper.apply(user);
        } else {
            throw new CustomerAlreadyExistsException(CUSTOMER_ALREADY_EXIST.formatted(command.username()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username).orElseThrow(
                () -> new CustomerNotFoundException(String.format(CUSTOMER_NOT_FOUND, username))
        );
    }

    private boolean exists(CreateCustomerCommand command) {
        return repository.existsByUsername(command.username());
    }
}
