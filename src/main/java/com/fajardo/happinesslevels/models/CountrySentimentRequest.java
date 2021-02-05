package com.fajardo.happinesslevels.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountrySentimentRequest {
    private String projectId;
    private String subscriptionId;
}
