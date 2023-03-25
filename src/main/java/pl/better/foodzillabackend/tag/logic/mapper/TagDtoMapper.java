package pl.better.foodzillabackend.tag.logic.mapper;

import org.springframework.stereotype.Component;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;
import pl.better.foodzillabackend.tag.logic.model.dto.TagDto;

import java.util.function.Function;

@Component
public class TagDtoMapper implements Function<Tag, TagDto> {
    @Override
    public TagDto apply(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
