package com.fajardo.happinesslevels.transforms;

import com.fajardo.happinesslevels.models.Tweet;

import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;

public class MapTweetsByCountryTest {

    @Rule
    public final transient TestPipeline testPipeline = TestPipeline.create();


    @Test
    public void testApplyMapsTweetToCountry() {
        Tweet inputTweet = new Tweet("happy", "", 4);

        PCollection<Tweet> input = testPipeline.apply(Create.of(inputTweet));
        PCollection<KV<String, Integer>> output = input.apply(MapElements.via(new MapTweetsByCountry()));

        PAssert.that(output).containsInAnyOrder(KV.of("USA", inputTweet.getSentiment()));

        testPipeline.run();
    }
}
