package pl.better.foodzillabackend.auth.model.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    String token;
    String date;
}
