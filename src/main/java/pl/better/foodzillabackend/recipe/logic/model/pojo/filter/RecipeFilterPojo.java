package pl.better.foodzillabackend.recipe.logic.model.pojo.filter;

import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeFilterPojo implements Serializable {
    private String attribute;
    private String equals;
    private String from;
    private String to;
    private Set<String> in;
    private Set<String> hasOnly;
}
