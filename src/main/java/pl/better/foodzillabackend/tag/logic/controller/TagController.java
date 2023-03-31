package pl.better.foodzillabackend.tag.logic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.better.foodzillabackend.auth.service.accesstype.LoggedUser;
import pl.better.foodzillabackend.tag.logic.model.dto.TagDto;
import pl.better.foodzillabackend.tag.logic.service.TagService;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    @QueryMapping
    @LoggedUser
    public Set<TagDto> tags() {
        return tagService.getAllTags();
    }
}
