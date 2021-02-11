package com.fajardo.happinesslevels.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fajardo.happinesslevels.HappinessLevelsApplication;
import com.fajardo.happinesslevels.models.CountrySentiment;
import com.fajardo.happinesslevels.models.CountrySentimentRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = HappinessLevelsApplication.class)
public class CountrySentimentsControllerTest {

    @Autowired
    private PubSubTemplate pubsubTemplate;

    @Autowired
    private RSocketRequester.Builder builder;

    private static RSocketRequester requester;

    @Before
    public void setUp() {
        initMocks(this);
        requester = builder.tcp("localhost", 7000);
    }

    @Test
    public void testCanGetCountrySentimentStream() {
        CountrySentimentRequest request = new CountrySentimentRequest("happiness-level", "pull-country-sentiments");
        String pubsubMessage = "{\"country\": \"USA\",\"averageSentiment\": 4.0}";

        // publish message to pub/sub
        pubsubTemplate.publish("country-sentiments", pubsubMessage);

        // send a request message
        Flux<CountrySentiment> stream = requester.route("country-sentiments").data(request)
                .retrieveFlux(CountrySentiment.class);

        // Verify that the response messages contain the expected data
        StepVerifier.create(stream).consumeNextWith(message -> {
            assertThat(message.getCountry(), is("USA"));
            assertThat(message.getAverageSentiment(), is(4.0));
        }).thenCancel().verify();
    }
}
