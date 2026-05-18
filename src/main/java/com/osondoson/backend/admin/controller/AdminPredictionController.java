package com.osondoson.backend.admin.controller;

import com.osondoson.backend.admin.dto.request.PredictionBatchRequest;
import com.osondoson.backend.admin.service.AdminPredictionBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/predictions")
@RequiredArgsConstructor
public class AdminPredictionController implements AdminPredictionControllerSwagger {

    private final AdminPredictionBatchService adminPredictionBatchService;

    @PostMapping("/pipeline")
    public ResponseEntity<Void> triggerPipeline(@RequestBody PredictionBatchRequest predictionBatchRequest) {
        adminPredictionBatchService.executePipeline(predictionBatchRequest.destinationLeagueAsEnum());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/performance")
    public ResponseEntity<Void> triggerPerformance(@RequestBody PredictionBatchRequest predictionBatchRequest) {
        adminPredictionBatchService.executePerformance(predictionBatchRequest.destinationLeagueAsEnum());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/market-value")
    public ResponseEntity<Void> triggerMarketValue(@RequestBody PredictionBatchRequest predictionBatchRequest) {
        adminPredictionBatchService.executeMarketValue(predictionBatchRequest.destinationLeagueAsEnum());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/similar-players")
    public ResponseEntity<Void> triggerSimilarPlayers(@RequestBody PredictionBatchRequest predictionBatchRequest) {
        adminPredictionBatchService.executeSimilarPlayers(predictionBatchRequest.destinationLeagueAsEnum());
        return ResponseEntity.accepted().build();
    }
}
