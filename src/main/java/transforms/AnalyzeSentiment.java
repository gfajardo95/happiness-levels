package transforms;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;

import models.Tweet;

/**
 * Tweets are collected from a Pub/Sub topic. The sentiment of the messages are
 * analyzed and their positivity is recorded.
 */
public class AnalyzeSentiment extends PTransform<PCollection<String>, PCollection<Tweet>> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PCollection<Tweet> expand(PCollection<String> messages) {
        return messages
            .apply(ParDo.of(new ExtractTweetsFn()))
            .apply(ParDo.of(new GetSentimentFn()));
    }
}
