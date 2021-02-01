import java.util.Base64;

import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestStream;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.Mean;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Rule;
import org.junit.Test;

import models.Tweet;
import transforms.AnalyzeSentiment;
import transforms.MapTweetsByCountry;
import transforms.SentimentDataToString;

public class HappinessPipelineTest {

    private final Instant baseTime = new Instant(0);

    private final Duration windowDuration = Duration.standardMinutes(2);

    @Rule
    public final transient TestPipeline testPipeline = TestPipeline.create();

    /**
     * The tweet messages used in the test are "happy", "okay", and "sad", which Stanford's
     * CoreNLP calculates as 4, 2, and 1 respectively.
     */
    @Test
    public void testRunComputesWindowSentimentAverage() {
        // averageSentiment = 3
        String testMessages1 = "{\"messages\": [{\"data\": {\"text\": \"happy\", \"location\": \"\"}}, {\"data\": {\"text\": \"okay\", \"location\": \"\"}}, {\"data\": {\"text\": \"happy\", \"location\": \"\"}}, {\"data\": {\"text\": \"okay\", \"location\": \"\"}} ]}";
        // averageSentiment = 2
        String testMessages2 = "{\"messages\": [{\"data\": {\"text\": \"happy\", \"location\": \"\"}}, {\"data\": {\"text\": \"sad\", \"location\": \"\"}}, {\"data\": {\"text\": \"okay\", \"location\": \"\"}}, {\"data\": {\"text\": \"sad\", \"location\": \"\"}} ]}";

        TestStream<String> tweetStream = TestStream
            .create(StringUtf8Coder.of())
            .advanceWatermarkTo(baseTime)
            .addElements(Base64.getEncoder().encodeToString(testMessages1.getBytes()))
            .advanceWatermarkTo(baseTime.plus(windowDuration).plus(Duration.standardMinutes(1)))
            .addElements(Base64.getEncoder().encodeToString(testMessages2.getBytes())).advanceWatermarkToInfinity();

        PCollection<String> output = testPipeline
            .apply(tweetStream)
            .apply(new AnalyzeSentiment())
            .apply(Window.<Tweet>into(FixedWindows.of(windowDuration)))
            .apply(MapElements.via(new MapTweetsByCountry()))
            .apply(Mean.<String, Double>perKey())
            .apply(new SentimentDataToString());

        PAssert
            .that(output)
            .inWindow(new IntervalWindow(baseTime, windowDuration))
            .containsInAnyOrder("USA: 3.0");

        PAssert
            .that(output)
            .inWindow(new IntervalWindow(baseTime.plus(windowDuration), windowDuration))
            .containsInAnyOrder("USA: 2.0");

        testPipeline.run();
    }
}