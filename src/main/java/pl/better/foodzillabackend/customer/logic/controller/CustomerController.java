package pl.better.foodzillabackend.customer.logic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.service.accesstype.LoggedInUser;
import pl.better.foodzillabackend.customer.logic.model.command.CreateCustomerCommand;
import pl.better.foodzillabackend.customer.logic.model.command.UpdateCustomerCommand;
import pl.better.foodzillabackend.customer.logic.model.dto.CustomerDto;
import pl.better.foodzillabackend.customer.logic.service.CustomerService;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @MutationMapping
    public CustomerDto createCustomer(@Argument @Valid CreateCustomerCommand customer) {
        return customerService.createNewUserAndSaveInDb(customer);
    }

    @LoggedInUser
    @MutationMapping
    public CustomerDto editCustomer(@Argument @Valid UpdateCustomerCommand customer) {
        String principal = (String)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customerService.editCustomer(principal, customer);
    }
}
