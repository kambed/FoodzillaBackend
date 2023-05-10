package pl.better.foodzillabackend.search.logic.model.domain;

import lombok.*;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

import java.io.Serializable;

@SchemaMapping
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchFilters implements Serializable {
    private String attribute;
    private String equals;
    private int from;
    private int to;
    private String[] in;
    private String[] hasOnly;
}