/**
 * <p>To execute this pipeline, specify the pipeline configuration like this:
 * <pre>{@code
 *   --project=YOUR_PROJECT_ID
 *   --tempLocation=gs://YOUR_TEMP_DIRECTORY
 *   --runner=YOUR_RUNNER
 *   --dataset=YOUR-DATASET
 *   --inputTopic=projects/YOUR_PROJECT_ID/topics/YOUR_INPUT_TOPIC
 *   --outputTopic=projects/YOUR_PROJECT_ID/topics/YOUR_OUTPUT_TOPIC
 * }
 * </pre>
 */

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.datastore.v1.*;
import com.google.datastore.v1.client.Datastore;
import com.google.datastore.v1.client.DatastoreException;
import com.google.datastore.v1.client.DatastoreHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.datastore.v1.client.DatastoreHelper.makeFilter;
import static com.google.datastore.v1.client.DatastoreHelper.makeValue;

public class HappinessPipeline {
    private static final String COUNTRY_COLUMN = "COUNTRY";
    private static final String SENTIMENT_COLUMN = "AVERAGE_SENTIMENT";
    private static final String CREATED_DATE_COLUMN = "CREATION_DATE";

    @DefaultCoder(AvroCoder.class)
    static class TweetEntity {
        private String text;
        private String location;
        private double sentiment;

        TweetEntity() {
            this.sentiment = 0;
        }

        String getText() {
            return text;
        }

        void setText(String text) {
            this.text = text;
        }

        String getLocation() {
            return location;
        }

        void setLocation(String location) {
            this.location = location;
        }

        double getSentiment() {
            return sentiment;
        }

        void setSentiment(double sentiment) {
            this.sentiment = sentiment;
        }
    }

    /**
     * decodes base64 encoded messages from a Pub/Sub topic into an equivalent Map.
     * The list of 'tweets' in the Map are encoded to Avro and returned for further
     * pipeline execution
     */
    static class ExtractTweetsFn extends DoFn<String, TweetEntity> {
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
                c.output(tw);
            }
        }
    }

    /**
     * using the Stanford CoreNLP library, the sentiment of every tweet is calculated and set on the .sentiment
     * property of the TweetEntity class
     */
    static class GetSentiment extends DoFn<TweetEntity, TweetEntity> {
        @ProcessElement
        public void ProcessElement(ProcessContext c) {
            TweetEntity tw = c.element();
            SentimentAnalyzer analyzer = new SentimentAnalyzer();
            double sentiment = analyzer.getSentimentFromText(tw.getText());
            tw.setSentiment(sentiment);
            c.output(tw);
        }
    }

    /**
     * Tweets are collected from a Pub/Sub topic. The sentiment of the messages are analyzed and
     * their positivity is recorded.
     */
    static class AnalyzeSentiment extends PTransform<PCollection<String>, PCollection<TweetEntity>> {
        public PCollection<TweetEntity> expand(PCollection<String> messages) {
            return messages
                    .apply(ParDo.of(new ExtractTweetsFn()))
                    .apply(ParDo.of(new GetSentiment()));
        }
    }

    static class SentimentDataToString extends PTransform<PCollection<KV<String, Double>>, PCollection<String>> {
        public PCollection<String> expand (PCollection<KV<String, Double>> row) {
            return row.apply(ParDo.of(new DoFn<KV<String, Double>, String>() {
                @ProcessElement
                public void ProcessElement(ProcessContext c){
                    c.output(c.element().getKey() + ": " + c.element().getValue() + ", " + new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss").format(new Date()));
                }
            }));
        }
    }

    /**
     * creates key/value pairs for the tweets where each tweet's key is the country in which it's written
     */
    static class MapTweetsByCountry extends SimpleFunction<TweetEntity, KV<String, Double>> {
        private static final Logger LOG = LoggerFactory.getLogger(MapTweetsByCountry.class);

        private Entity runQuery(Query query) {
            Entity entity = null;
            try {
                Datastore datastore = DatastoreHelper.getDatastoreFromEnv();
                RunQueryRequest.Builder request = RunQueryRequest.newBuilder();
                request.setQuery(query);
                RunQueryResponse response = datastore.runQuery(request.build());
                int resultCount = response.getBatch().getEntityResultsCount();
                EntityResult result = null;
                if (resultCount > 0) {
                    LOG.info("result count is greater than 0");
                    result = response.getBatch().getEntityResults(0);
                    entity = result.getEntity();
                }
            } catch(DatastoreException e) {
                System.err.println("Error talking to the datastore: " + e.getMessage());
                System.exit(2);
            } catch (GeneralSecurityException exception) {
                System.err.println("Security error connecting to the datastore: " + exception.getMessage());
                System.exit(2);
            } catch (IOException exception) {
                System.err.println("I/O error connecting to the datastore: " + exception.getMessage());
                System.exit(2);
            }
            return entity;
        }

        @Override
        public KV<String, Double> apply(TweetEntity tweet) {
            String[] locationTokens = tweet.getLocation().split(",");
            String countryOfOrigin = "";
            LOG.info(tweet.getLocation());
            for (String token : locationTokens){
                LOG.info("token: " + token);
            }
            Query.Builder q = Query.newBuilder();
            q.addKindBuilder().setName("world_city");
            q.setFilter(makeFilter("city_ascii", PropertyFilter.Operator.EQUAL, makeValue(locationTokens[0])));
            Entity result = runQuery(q.build());
            if (result != null) {
                Map<String, Value> resultMap = result.getPropertiesMap();
                countryOfOrigin = resultMap.get("country").getStringValue();
                LOG.info("country of origin: " + countryOfOrigin);
            }
            return KV.of(countryOfOrigin, tweet.getSentiment());
        }
    }

    public interface Options extends PipelineOptions {
        @Description("Pub/Sub topic to get input from")
        @Validation.Required
        String getInputTopic();

        void setInputTopic(String value);

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

        pipeline.apply(PubsubIO.readStrings().fromTopic(options.getInputTopic()))
                .apply(new AnalyzeSentiment())
                .apply(Window.<TweetEntity>into(FixedWindows.of(Duration.standardMinutes(2))))
                .apply(MapElements.via(new MapTweetsByCountry()))
                .apply(Mean.<String, Double>perKey())
                .apply(new SentimentDataToString())
                .apply(PubsubIO.writeStrings().to(options.getOutputTopic()));
        pipeline.run().waitUntilFinish();
    }
}