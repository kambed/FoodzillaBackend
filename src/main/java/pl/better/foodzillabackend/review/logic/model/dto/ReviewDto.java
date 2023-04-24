package pl.better.foodzillabackend.review.logic.model.dto;

import lombok.Builder;

@Builder
public record ReviewDto(Long id,
                        String review,
                        Integer rating) {
}
