package com.osondoson.backend.admin.ai;

import com.osondoson.backend.admin.ai.dto.*;
import com.osondoson.backend.enums.league.League;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AiPredictionClient {

    private final RestTemplate restTemplate;

    @Value("${ai.server.base-url}")
    private String baseUrl;

    public List<AiPerformancePrediction> fetchPerformancePredictions(List<Long> playerIds, League destinationLeague) {
        AiPerformanceRequest request = AiPerformanceRequest.of(playerIds, destinationLeague.getValue());
        return post("/predict/performance", request, new ParameterizedTypeReference<>() {});
    }

    public List<AiMarketValuePrediction> fetchMarketValuePredictions(List<Long> playerIds, League destinationLeague) {
        AiMarketValueRequest request = AiMarketValueRequest.of(playerIds, destinationLeague.getValue());
        return post("/predict/market-value", request, new ParameterizedTypeReference<>() {});
    }

    public List<AiSimilarPlayersPrediction> fetchSimilarPlayersPredictions(List<Long> playerIds, League destinationLeague) {
        AiSimilarPlayersRequest request = AiSimilarPlayersRequest.of(playerIds, destinationLeague.getValue());
        return post("/predict/similar-players", request, new ParameterizedTypeReference<>() {});
    }

    private <T> List<T> post(String path, Object body, ParameterizedTypeReference<List<T>> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        ResponseEntity<List<T>> response = restTemplate.exchange(
                baseUrl + path,
                HttpMethod.POST,
                entity,
                responseType
        );
        return response.getBody() != null ? response.getBody() : List.of();
    }
}
