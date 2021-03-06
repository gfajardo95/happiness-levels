package com.fajardo.happinesslevels.transforms;

import java.util.Base64;

import com.fajardo.happinesslevels.models.Tweet;

import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;

public class ExtractTweetsFnTest {

    @Rule
    public final transient TestPipeline testPipeline = TestPipeline.create();

    
    @Test
    public void testProcessElementGetsTweet() {
        String testMessages = "{\"messages\": [{\"data\": {\"text\": \"TEST\", \"country\": \"\"}}]}";

        PCollection<String> input = testPipeline.apply(Create.of(Base64.getEncoder().encodeToString(testMessages.getBytes())));
        PCollection<Tweet> output = input.apply(ParDo.of(new ExtractTweetsFn()));

        PAssert.that(output).containsInAnyOrder(new Tweet("TEST", "", 0));

        testPipeline.run();
    }
}
