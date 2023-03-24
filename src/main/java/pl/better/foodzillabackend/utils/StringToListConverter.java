package pl.better.foodzillabackend.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;

@Converter
public class StringToListConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        return "['" + String.join("', '", strings) + "']";
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        return Arrays.stream(s.substring(2, s.length() - 2).split("', '"))
                        .filter(el -> !el.isBlank()).toList();
    }
}
