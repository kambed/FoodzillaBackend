package pl.better.foodzillabackend.customer.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.customer.logic.model.domain.Customer;
import pl.better.foodzillabackend.customer.logic.model.dto.CustomerDto;

import java.util.function.Function;

@Component
public class CustomerDtoMapper implements Function<Customer, CustomerDto> {
    @Override
    public CustomerDto apply(Customer user) {
        return CustomerDto.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .build();
    }
}
