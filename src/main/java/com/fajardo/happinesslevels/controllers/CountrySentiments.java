package com.fajardo.happinesslevels.controllers;

import com.fajardo.happinesslevels.models.CountrySentiment;

import reactor.core.publisher.Flux;

public interface CountrySentiments {

    Flux<CountrySentiment> streamCountrySentiments(String subscriptionId);
}
