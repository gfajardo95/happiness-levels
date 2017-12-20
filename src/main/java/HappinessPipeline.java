
/**
 * <p>To execute this pipeline, specify the pipeline configuration like this:
 * <pre>{@code
 *   --project=YOUR_PROJECT_ID
 *   --tempLocation=gs://YOUR_TEMP_DIRECTORY
 *   --runner=YOUR_RUNNER
 *   --dataset=YOUR-DATASET
 *   --topic=projects/YOUR-PROJECT/topics/YOUR-TOPIC
 * }
 * </pre>
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubMessage;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class HappinessPipeline {

    @DefaultCoder(AvroCoder.class)
    static class TweetEntity {
        private String text;
        private String location;
        private String sentiment;

        public TweetEntity() {
            this.sentiment = "";
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getSentiment() {
            return sentiment;
        }

        public void setSentiment(String sentiment) {
            this.sentiment = sentiment;
        }
    }

    /**
     * converts messages from Pub/Sub topic to a Tweet Entity
     */
    static class ExtractTweetsFn extends DoFn<String, TweetEntity> {
        private static final Logger LOG = LoggerFactory.getLogger(ExtractTweetsFn.class);

        @ProcessElement
        public void ProcessElement(ProcessContext c) {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(c.element());
            String decodedString = new String(decodedBytes);

            Gson gson = new Gson();
            Type type = new TypeToken<
                    Map<String, List<Map<String, TweetEntity>>>>() {
            }.getType();
            Map<String, List<Map<String, TweetEntity>>> twraw = gson.fromJson(decodedString, type);
            List<Map<String, TweetEntity>> twmessages = twraw.get("messages");

            for (Map<String, TweetEntity> message : twmessages) {
                TweetEntity tw = message.get("data");
                LOG.info(tw.getText());
                LOG.info(tw.getLocation());
                c.output(tw);
            }

        }
    }

    static class GetSentiment extends DoFn<TweetEntity, TweetEntity> {
        private static final Logger LOG = LoggerFactory.getLogger(ExtractTweetsFn.class);

        @ProcessElement
        public void ProcessElement (ProcessContext c) {
            LOG.info("In GetSentiment$ProcessElement: " + c.element().toString());
        }
    }

    static class AnalyzeSentiment extends PTransform<PCollection<String>, PCollection<TweetEntity>> {

        public PCollection<TweetEntity> expand(PCollection<String> messages) {
            PCollection<TweetEntity> tweets = messages.apply(ParDo.of(new ExtractTweetsFn()));

            PCollection<TweetEntity> sentiments = tweets.apply(ParDo.of(new GetSentiment()));
            return sentiments;
        }
    }

    public interface Options extends PipelineOptions {
        @Description("Pub/Sub topic to get input from")
        // @Default.String("tweets")
        @Validation.Required
        String getTopic();

        void setTopic(String value);

        @Description("Path of the file to write to")
        @Validation.Required
        String getOutput();

        void setOutput(String value);
    }

    public static void main(String[] args) {
        Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);

        Pipeline pipeline = Pipeline.create(options);

        // alternative is readMessages() which just returns a PubSub stream, but lets see if this correctly
        // decodes it for me
        pipeline.apply(PubsubIO.readStrings().fromTopic(options.getTopic()))
            .apply(new AnalyzeSentiment());

        pipeline.run().waitUntilFinish();

    }
}