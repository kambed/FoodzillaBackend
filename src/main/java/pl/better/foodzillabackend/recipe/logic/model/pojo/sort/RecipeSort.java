package pl.better.foodzillabackend.recipe.logic.model.pojo.sort;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeSort implements Serializable {
    private String attribute;
    private SortDirectionPojo direction;
}
