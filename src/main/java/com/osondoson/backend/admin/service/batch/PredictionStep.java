package com.osondoson.backend.admin.service.batch;

public enum PredictionStep {
    PERFORMANCE("성과 예측 데이터 적재"),
    MARKET_VALUE("시장가치 예측 데이터 적재"),
    SIMILAR_PLAYERS("유사선수 추천 데이터 적재");

    private final String label;

    PredictionStep(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
