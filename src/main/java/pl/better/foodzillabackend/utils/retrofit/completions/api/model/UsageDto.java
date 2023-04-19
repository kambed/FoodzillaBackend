package pl.better.foodzillabackend.utils.retrofit.completions.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public record UsageDto(
        @JsonProperty("prompt_tokens") int promptTokens,
        @JsonProperty("completion_tokens") int completionTokens,
        @JsonProperty("total_tokens") int totalTokens
) {
}
