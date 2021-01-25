package transforms;

import java.util.Base64;

import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;

import models.Tweet;

public class AnalyzeSentimentTest {
    
    @Rule
    public final transient TestPipeline testPipeline = TestPipeline.create();

    
    @Test
    public void testExpandCalculatesTweetSentiment() {
        String testMessages = "{\"messages\": [{\"data\": {\"text\": \"happy\", \"location\": \"\"}}]}";
        Tweet expectedTweet = new Tweet("happy", "", 4.0);

        PCollection<String> input = testPipeline.apply(Create.of(Base64.getEncoder().encodeToString(testMessages.getBytes())));
        PCollection<Tweet> output = input.apply(new AnalyzeSentiment());

        PAssert.that(output).containsInAnyOrder(expectedTweet);

        testPipeline.run();
    }
}
