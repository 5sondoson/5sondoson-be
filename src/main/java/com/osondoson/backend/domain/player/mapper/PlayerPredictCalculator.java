package com.osondoson.backend.domain.player.mapper;

import com.osondoson.backend.domain.player.dto.KeyStat;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerPredictCalculator {

    public List<KeyStat> computeKeyStatDeltas(List<KeyStat> current, List<KeyStat> predicted) {
        Map<String, Double> currentValueByLabel = toValueByLabel(current);

        return predicted.stream()
                .map(predictedStat -> new KeyStat(
                        predictedStat.label(),
                        calculateDelta(currentValueByLabel.get(predictedStat.label()), predictedStat.value())
                ))
                .toList();
    }

    public Long marketValueDelta(Long predicted, Long current) {
        if (predicted == null || current == null) {
            return null;
        }
        return predicted - current;
    }

    private Map<String, Double> toValueByLabel(List<KeyStat> keyStats) {
        Map<String, Double> valueByLabel = new HashMap<>();
        for (KeyStat keyStat : keyStats) {
            valueByLabel.put(keyStat.label(), keyStat.value());
        }
        return valueByLabel;
    }

    private Double calculateDelta(Double currentValue, Double predictedValue) {
        if (currentValue == null || predictedValue == null) {
            return null;
        }
        return Math.round((predictedValue - currentValue) * 100.0) / 100.0;
    }
}
