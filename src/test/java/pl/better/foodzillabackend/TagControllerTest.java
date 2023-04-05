package pl.better.foodzillabackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import pl.better.foodzillabackend.tag.logic.model.domain.Tag;
import pl.better.foodzillabackend.tag.logic.model.dto.TagDto;

import static graphql.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TagControllerTest extends TestBase {

    @BeforeEach
    public void setUp() {
        super.resetDb();
        Tag tag1 = new Tag("Breakfast");
        Tag tag2 = new Tag("Lunch");
        tagRepository.save(tag1);
        tagRepository.save(tag2);
    }

    @Test
    void shouldReturnAllIngredients() {
        GraphQlTester.Response response = graphQlTester.documentName("tags").execute();
        response.errors().verify();
        response.path("tags").entityList(TagDto.class).satisfies(tags -> {
            assertEquals(2, tags.size());
            assertTrue(tags.stream().anyMatch(tag -> tag.name().equals("Breakfast")));
            assertTrue(tags.stream().anyMatch(tag -> tag.name().equals("Lunch")));
        });
    }
}
