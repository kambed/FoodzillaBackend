package pl.better.foodzillabackend.search.logic.model.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchSort {

    private String sortAttribute;
    private String sortDirection;
}