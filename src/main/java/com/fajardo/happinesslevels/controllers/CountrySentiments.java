package com.fajardo.happinesslevels.controllers;

import com.fajardo.happinesslevels.models.CountrySentiment;
import com.fajardo.happinesslevels.models.CountrySentimentRequest;

import reactor.core.publisher.Flux;

public interface CountrySentiments {

    Flux<CountrySentiment> streamCountrySentiments(final CountrySentimentRequest request);
}
