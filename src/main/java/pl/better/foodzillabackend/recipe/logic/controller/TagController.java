package pl.better.foodzillabackend.recipe.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.recipe.logic.model.dto.TagDto;
import pl.better.foodzillabackend.recipe.logic.service.TagService;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    @QueryMapping
    public Set<TagDto> tags() {
        return tagService.getAllTags();
    }
}
