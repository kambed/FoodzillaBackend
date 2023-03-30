package pl.better.foodzillabackend.user.logic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.user.logic.model.command.CreateCustomerCommand;
import pl.better.foodzillabackend.user.logic.model.command.UpdateCustomerCommand;
import pl.better.foodzillabackend.user.logic.model.dto.CustomerDto;
import pl.better.foodzillabackend.user.logic.service.CustomerService;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @MutationMapping
    public CustomerDto createCustomer(@Argument @Valid CreateCustomerCommand customer) {
        return customerService.createNewUserAndSaveInDb(customer);
    }

    @MutationMapping
    public CustomerDto editCustomer(@Argument @Valid UpdateCustomerCommand customer) {
        return customerService.editCustomer(customer);
    }
}
