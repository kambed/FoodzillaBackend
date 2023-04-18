package pl.better.foodzillabackend.utils.retrofit.completions.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public record ChoicesDto(
        String text,
        int index,
        double logprobs,
        @JsonProperty("finish_reason") String finishReason
) {
}
