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
import pl.better.foodzillabackend.exceptions.type.CustomerAlreadyExistsException;
import pl.better.foodzillabackend.exceptions.type.CustomerNotFoundException;
import pl.better.foodzillabackend.customer.logic.model.command.CreateCustomerCommand;
import pl.better.foodzillabackend.customer.logic.model.command.UpdateCustomerCommand;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.model.dto.CustomerDto;
import pl.better.foodzillabackend.customer.logic.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private static final String CUSTOMER_NOT_FOUND = "Customer with id: %s not found";
    private static final String CUSTOMER_ALREADY_EXIST = "Customer with username: %s already exists";
    private final CustomerRepository userRepo;
    private final CustomerDtoMapper mapper;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public CustomerDto createNewUserAndSaveInDb(CreateCustomerCommand command) {
        if (!exists(command)) {
            Customer user = Customer.builder()
                    .firstname(command.firstname())
                    .lastname(command.lastname())
                    .username(command.username())
                    .password(passwordEncoder.encode(command.password()))
                    .build();

            userRepo.saveAndFlush(user);
            return mapper.apply(user);
        } else {
            throw new CustomerAlreadyExistsException(CUSTOMER_ALREADY_EXIST.formatted(command.username()));
        }
    }

    private boolean exists(CreateCustomerCommand command) {
        return userRepo.existsByUsername(command.username());
    }

    @Transactional
    public CustomerDto editCustomer(UpdateCustomerCommand command) {
        Customer user = userRepo.findById(command.customerId()).orElseThrow(() -> new CustomerNotFoundException(
                CUSTOMER_NOT_FOUND.formatted(command.customerId())
        ));
        if (!userRepo.existsByUsername(command.username())) {
            user.setFirstname(command.firstname());
            user.setLastname(command.lastname());
            user.setUsername(command.username());
            //TODO: password encryption, related to security configuration
            user.setPassword(command.password());
            userRepo.saveAndFlush(user);
            return mapper.apply(user);
        } else {
            throw new CustomerAlreadyExistsException(CUSTOMER_ALREADY_EXIST.formatted(command.username()));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).get();
    }
}
