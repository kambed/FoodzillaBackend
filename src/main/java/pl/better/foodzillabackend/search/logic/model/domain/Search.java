package pl.better.foodzillabackend.search.logic.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phrase;
    private String filterAttribute;
    private String filterEquals;
    private String sortAttribute;
    private Boolean isSortAscending;

}
