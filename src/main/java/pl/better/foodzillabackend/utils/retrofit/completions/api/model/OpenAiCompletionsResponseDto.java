package pl.better.foodzillabackend.utils.retrofit.completions.api.model;

import java.util.List;

public record OpenAiCompletionsResponseDto(
        String id,
        String object,
        String created,
        String model,
        List<ChoicesDto> choices,
        UsageDto usage
) {
}
