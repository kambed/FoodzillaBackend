package pl.better.foodzillabackend.recommendation.logic.service;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.util.Collections;

import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.recommendation.logic.mapper.RecommendationDtoMapper;
import pl.better.foodzillabackend.recommendation.logic.model.dto.RecommendationDto;

import java.util.List;

@Service
public class RecommendationService {
    private final UserBasedRecommender userBasedRecommender;
    private final RecommendationDtoMapper recommendationDtoMapper;

    public RecommendationService(RecommendationDtoMapper recommendationDtoMapper, Environment environment) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(environment.getProperty("MYSQL_URL"));
        dataSource.setUser(environment.getProperty("MYSQL_USER"));
        dataSource.setPassword(environment.getProperty("MYSQL_PASSWORD"));
        dataSource.setDatabaseName(environment.getProperty("MYSQL_DATABASE"));
        JDBCDataModel model = new MySQLJDBCDataModel(dataSource,
                "preferences", "user_id", "recipe_id", "rating", null);
        CityBlockSimilarity similarity = new CityBlockSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        userBasedRecommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        this.recommendationDtoMapper = recommendationDtoMapper;
    }

    public List<RecommendationDto> recommend(long id, int numOfRecommendations) {
        try {
            return userBasedRecommender.recommend(id, numOfRecommendations)
                    .stream()
                    .map(recommendationDtoMapper)
                    .toList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
