package com.osondoson.backend.domain.player.mapper;

import com.osondoson.backend.domain.player.dto.KeyStat;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerPredictCalculator {

    public List<KeyStat> computeKeyStatDeltas(List<KeyStat> current, List<KeyStat> predicted) {
        List<KeyStat> deltas = new ArrayList<>();
        for (int i = 0; i < Math.min(current.size(), predicted.size()); i++) {
            KeyStat c = current.get(i);
            KeyStat p = predicted.get(i);
            Double delta = (c.value() != null && p.value() != null)
                    ? Math.round((p.value() - c.value()) * 100.0) / 100.0
                    : null;
            deltas.add(new KeyStat(c.label(), delta));
        }
        return deltas;
    }

    public Long marketValueDelta(Long predicted, Long current) {
        return (predicted != null && current != null) ? predicted - current : null;
    }
}
