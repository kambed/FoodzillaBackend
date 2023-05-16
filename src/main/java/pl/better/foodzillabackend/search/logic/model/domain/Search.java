package pl.better.foodzillabackend.search.logic.model.domain;

import jakarta.persistence.*;
import lombok.*;
import pl.better.foodzillabackend.recipe.logic.model.pojo.filter.RecipeFilterPojo;
import pl.better.foodzillabackend.recipe.logic.model.pojo.sort.RecipeSort;
import pl.better.foodzillabackend.utils.SetToBlobConverter;
import java.util.Set;

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

    @Lob
    @Convert(converter = SetToBlobConverter.class)
    private Set<RecipeFilterPojo> filters;

    @Lob
    @Convert(converter = SetToBlobConverter.class)
    private Set<RecipeSort> sort;
}
