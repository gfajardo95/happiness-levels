package com.fajardo.happinesslevels.controllers;

import java.io.File;
import java.io.IOException;

import com.fajardo.happinesslevels.PipelineProperties;
import com.fajardo.happinesslevels.models.CountrySentiment;
import com.fajardo.happinesslevels.models.CountrySentimentRequest;
import com.google.gson.Gson;

import org.springframework.cloud.gcp.pubsub.reactive.PubSubReactiveFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@AllArgsConstructor
@Controller
public class CountrySentimentsController implements CountrySentiments {

    private final PubSubReactiveFactory reactiveFactory;

    private final PipelineProperties pipelineProps;

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
                log.info("Received message: {}", message.getPubsubMessage().getData().toStringUtf8());

                message.ack();
            })
            .map(message -> gson.fromJson(
                message.getPubsubMessage().getData().toStringUtf8(),
                CountrySentiment.class));
    }

    private void runPipeline() {
        if (!pipelineProps.isTesting()) {
            try {
                new ProcessBuilder()
                        .command("zsh", "-c", ". venv/bin/activate && python3 run_pipeline.py")
                        .directory(new File(System.getProperty("user.dir")))
                        .redirectErrorStream(true)
                        .inheritIO()
                        .start();
            } catch (IOException e) {
                log.error("Error running the pipeline", e);
            }
        }
    }
}
