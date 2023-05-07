package pl.better.foodzillabackend.search.logic.model.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchFilters {

    private String filterAttribute;
    private String filterEquals;
}
