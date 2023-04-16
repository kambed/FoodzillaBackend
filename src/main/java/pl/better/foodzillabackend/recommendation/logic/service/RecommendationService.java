package pl.better.foodzillabackend.recommendation.logic.service;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import pl.better.foodzillabackend.recommendation.logic.mapper.RecommendationDtoMapper;
import pl.better.foodzillabackend.recommendation.logic.model.dto.RecommendationDto;

import java.util.Collections;
import java.util.List;

@Service
public class RecommendationService {

    public List<RecommendationDto> recommend(long id, int numOfRecommendations) {
        //TODO: CONNECT TO PYTHON MODULE
    }
}
