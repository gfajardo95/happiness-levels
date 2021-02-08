package com.fajardo.happinesslevels.controllers;

import java.io.File;
import java.io.IOException;

import com.fajardo.happinesslevels.models.CountrySentiment;
import com.fajardo.happinesslevels.models.CountrySentimentRequest;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.reactive.PubSubReactiveFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
public class CountrySentimentsController implements CountrySentiments {

    @Autowired
    PubSubReactiveFactory reactiveFactory;

    @MessageMapping("country-sentiments")
    @Override
    public Flux<CountrySentiment> streamCountrySentiments(CountrySentimentRequest request) {
        Gson gson = new Gson();
        log.info("Requesting country sentiments from \"topics/{}/subscriptions/{}\"", request.getProjectId(),
                request.getSubscriptionId());

        runPipeline();

        return reactiveFactory
            .poll(request.getSubscriptionId(), 500)
            .doOnNext(message -> {
                log.info("Received message: {} -- published {}",
                    message.getPubsubMessage().getData().toStringUtf8(),
                    message.getPubsubMessage().getPublishTime());

                message.ack();
            })
            .map(message -> gson.fromJson(
                message.getPubsubMessage().getData().toStringUtf8(),
                CountrySentiment.class));
    }

    private void runPipeline() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder()
                    .command("zsh", "-c", ". venv/bin/activate && python3 run_pipeline.py")
                    .directory(new File(System.getProperty("user.dir"))).redirectErrorStream(true);

            processBuilder.inheritIO().start();
        } catch (IOException e) {
            log.error("Error running the pipeline", e);
        }
    }
}
