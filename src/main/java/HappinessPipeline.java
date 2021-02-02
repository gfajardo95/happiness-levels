
/**
 * <p>To execute this pipeline, specify the pipeline configuration like this:
 * <pre>{@code
 *   --project=YOUR_PROJECT_ID
 *   --region=COMPUTE_REGION
 *   --tempLocation=gs://YOUR_TEMP_DIRECTORY
 *   --runner=YOUR_RUNNER
 *   --inputSubscription=projects/YOUR_PROJECT_ID/subscriptions/YOUR_INPUT_TOPIC
 *   --outputTopic=projects/YOUR_PROJECT_ID/topics/YOUR_OUTPUT_TOPIC
 * }
 * </pre>
 */

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.Mean;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.joda.time.Duration;

import models.Tweet;
import transforms.AnalyzeSentiment;
import transforms.MapTweetsByCountry;
import transforms.SentimentDataToString;

public class HappinessPipeline {

    public interface Options extends PipelineOptions {
        @Description("Pub/Sub subscription to get input from")
        @Validation.Required
        String getInputSubscription();

        void setInputSubscription(String value);

        @Description("Pub/Sub topic to send output to")
        @Validation.Required
        String getOutputTopic();

        void setOutputTopic(String value);
    }
    /**
     * The positivity of tweets coming through a Pub/Sub topic are recorded, and divided into different
     * 'country buckets'. The collective average of the tweet's positivity is then calculated for each
     * bucket. The averages are recalculated every 10 seconds.
     */
    public static void main(String[] args) {
        Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
        Pipeline pipeline = Pipeline.create(options);

        pipeline.apply(PubsubIO.readStrings().fromSubscription(options.getInputSubscription()))
                .apply(new AnalyzeSentiment())
                .apply(Window.<Tweet>into(FixedWindows.of(Duration.millis(200))))
                .apply(MapElements.via(new MapTweetsByCountry()))
                .apply(Mean.<String, Double>perKey())
                .apply(new SentimentDataToString())
                .apply(PubsubIO.writeStrings().to(options.getOutputTopic()));

        pipeline.run().waitUntilFinish();
    }
}
