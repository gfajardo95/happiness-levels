package com.fajardo.happinesslevels.transforms;

import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;

public class SentimentDataToStringTest {

    @Rule
    public final transient TestPipeline testPipeline = TestPipeline.create();

    @Test
    public void testExpandCanReturnCountryMappingWithDate() {
        KV<String, Double> countryMapping = KV.of("United States", 4.0);

        PCollection<KV<String, Double>> input = testPipeline.apply(Create.of(countryMapping));
        PCollection<String> output = input.apply(new SentimentDataToString());

        PAssert.that(output).containsInAnyOrder("{\"country\":\"United States\",\"averageSentiment\":4.0}");

        testPipeline.run();
    }
}
