package pl.better.foodzillabackend.recipe.logic.model.pojo.filter;

public record FilterIntTypePojo(
        Integer equals,
        Integer from,
        Integer to,
        Integer has
) {
}
