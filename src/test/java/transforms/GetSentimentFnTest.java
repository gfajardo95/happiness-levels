package transforms;

import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Rule;
import org.junit.Test;

import models.Tweet;

public class GetSentimentFnTest {

    @Rule
    public final transient TestPipeline testPipeline = TestPipeline.create();

    
    @Test
    public void testProcessElementSetsSentiment() {
        Tweet expectedTweet = new Tweet("happy", "", 4.0);
        
        PCollection<Tweet> input = testPipeline.apply(Create.of(new Tweet("happy", "", 0.0)));
        PCollection<Tweet> output = input.apply(ParDo.of(new GetSentimentFn()));

        PAssert.that(output).containsInAnyOrder(expectedTweet);

        testPipeline.run();
    }
}
