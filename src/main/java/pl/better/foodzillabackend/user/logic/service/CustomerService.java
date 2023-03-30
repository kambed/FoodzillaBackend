package pl.better.foodzillabackend.user.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.better.foodzillabackend.exceptions.type.UserAlreadyExistsException;
import pl.better.foodzillabackend.exceptions.type.UserNotFoundException;
import pl.better.foodzillabackend.user.logic.mapper.UserDtoMapper;
import pl.better.foodzillabackend.user.logic.model.command.CreateCustomerCommand;
import pl.better.foodzillabackend.user.logic.model.command.UpdateCustomerCommand;
import pl.better.foodzillabackend.user.logic.model.domain.Customer;
import pl.better.foodzillabackend.user.logic.model.dto.CustomerDto;
import pl.better.foodzillabackend.user.logic.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final String USER_NOT_FOUND = "User with id: %s not found";
    private static final String USER_ALREADY_EXIST = "User with username: %s already exists";
    private final UserRepository userRepo;
    private final UserDtoMapper mapper;

    @Transactional
    public CustomerDto createNewUserAndSaveInDb(CreateCustomerCommand command) {
        if (!exists(command)) {
            Customer user = Customer.builder()
                    .firstname(command.firstname())
                    .lastname(command.lastname())
                    .username(command.username())
                    //TODO: password encryption, related to security configuration
                    .password(command.password())
                    .build();

            userRepo.saveAndFlush(user);
            return mapper.apply(user);
        } else {
            throw new UserAlreadyExistsException(USER_ALREADY_EXIST.formatted(command.username()));
        }
    }

    private boolean exists(CreateCustomerCommand command) {
        return userRepo.existsByUsername(command.username());
    }

    @Transactional
    public CustomerDto editCustomer(UpdateCustomerCommand command) {
        Customer user = userRepo.findById(command.customerId()).orElseThrow(() -> new UserNotFoundException(
                USER_NOT_FOUND.formatted(command.customerId())
        ));
        if (!userRepo.existsByUsername(command.username())) {
            user.setFirstname(command.firstname());
            user.setLastname(command.lastname());
            user.setUsername(command.username());
            user.setPassword(command.password());
            userRepo.saveAndFlush(user);
            return mapper.apply(user);
        } else {
            throw new UserAlreadyExistsException(USER_ALREADY_EXIST.formatted(command.username()));
        }
    }
}
