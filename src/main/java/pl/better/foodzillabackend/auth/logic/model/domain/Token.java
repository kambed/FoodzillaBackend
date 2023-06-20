package pl.better.foodzillabackend.auth.logic.model.domain;

import lombok.*;
import pl.better.foodzillabackend.customer.logic.model.dto.CustomerDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    String token;
    String refreshToken;
    CustomerDto customer;
}
