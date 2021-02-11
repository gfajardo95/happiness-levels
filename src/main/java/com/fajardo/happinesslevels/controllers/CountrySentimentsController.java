package com.fajardo.happinesslevels.controllers;

import java.io.File;
import java.io.IOException;

import com.fajardo.happinesslevels.PipelineProperties;
import com.fajardo.happinesslevels.models.CountrySentiment;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.gson.Gson;

import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@AllArgsConstructor
@Controller
public class CountrySentimentsController implements CountrySentiments {

    private final PubSubTemplate pubsubTemplate;

    private final PipelineProperties pipelineProps;

    @MessageMapping("country-sentiments")
    @Override
    public Flux<CountrySentiment> streamCountrySentiments(String subscriptionId) {
        Gson gson = new Gson();
        log.info("Requesting country sentiments from \"topics/{}/subscriptions/{}\"",
                System.getenv("GOOGLE_CLOUD_PROJECT"), subscriptionId);

        runPipeline();

        return Flux.create(emitter -> {
            Subscriber subscriber = pubsubTemplate.subscribe(subscriptionId, (message) -> {
                message.ack();

                log.info("Received message: {}", message.getPubsubMessage().getData().toStringUtf8());
                emitter.next(
                        gson.fromJson(message.getPubsubMessage().getData().toStringUtf8(), CountrySentiment.class));
            });

            emitter.onDispose(() -> {
                subscriber.stopAsync();
            });
        });
    }

    private void runPipeline() {
        if (!pipelineProps.isTesting()) {
            try {
                new ProcessBuilder().command("zsh", "-c", ". venv/bin/activate && python3 run_pipeline.py")
                        .directory(new File(System.getProperty("user.dir"))).redirectErrorStream(true).inheritIO()
                        .start();
            } catch (IOException e) {
                log.error("Error running the pipeline", e);
            }
        }
    }
}
