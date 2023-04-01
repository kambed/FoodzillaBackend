package pl.better.foodzillabackend.customer.logic.controller;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.service.accesstype.LoggedInUser;
import pl.better.foodzillabackend.customer.logic.model.command.CreateCustomerCommand;
import pl.better.foodzillabackend.customer.logic.model.command.UpdateCustomerCommand;
import pl.better.foodzillabackend.customer.logic.model.dto.CustomerDto;
import pl.better.foodzillabackend.customer.logic.service.CustomerService;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public CustomerDto editCustomer(@Argument @Valid UpdateCustomerCommand customer, DataFetchingEnvironment env) {
        SecurityContext context = (SecurityContext) env.getGraphQlContext()
                .stream()
                .toList()
                .get(0)
                .getValue();
        String principal = (String) context.getAuthentication().getPrincipal();
        return customerService.editCustomer(principal, customer);
    }
}
