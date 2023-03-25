package pl.better.foodzillabackend.tag.logic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.tag.logic.mapper.TagDtoMapper;
import pl.better.foodzillabackend.tag.logic.model.dto.TagDto;
import pl.better.foodzillabackend.tag.logic.repository.TagRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagDtoMapper tagDtoMapper;

    public Set<TagDto> getAllTags() {
        return tagRepository.findAll()
                .stream()
                .map(tagDtoMapper)
                .collect(Collectors.toSet());
    }
}
