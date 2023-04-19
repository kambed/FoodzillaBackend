package pl.better.foodzillabackend.utils.retrofit.completions.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAiCompletionsRequestDto(String model, String prompt, @JsonProperty("max_tokens") int maxTokens) {
}
