package com.fajardo.happinesslevels.transforms;

import com.fajardo.happinesslevels.models.Tweet;
import com.fajardo.happinesslevels.services.SentimentAnalyzer;

import org.apache.beam.sdk.transforms.DoFn;

/**
 * using the Stanford CoreNLP library, the sentiment of every tweet is
 * calculated and set on the .sentiment property of the Tweet class
 */
public class GetSentimentFn extends DoFn<Tweet, Tweet> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ProcessElement
    public void ProcessElement(ProcessContext c) {
        Tweet tw = new Tweet(c.element().getText(), c.element().getLocation(), c.element().getSentiment());
        SentimentAnalyzer analyzer = new SentimentAnalyzer();
        double sentiment = analyzer.getSentimentFromText(tw.getText());
        tw.setSentiment(sentiment);
        c.output(tw);
    }
}
