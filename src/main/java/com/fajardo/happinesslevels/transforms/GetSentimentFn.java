package com.fajardo.happinesslevels.transforms;

import com.fajardo.happinesslevels.services.SentimentAnalyzer;
import com.fajardo.happinesslevels.models.Tweet;

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

    private SentimentAnalyzer analyzer;

    @Setup
    public void setUp() {
        analyzer = new SentimentAnalyzer();
    }

    @ProcessElement
    public void ProcessElement(ProcessContext c) {
        
        c.output(analyzer.getTweetWithSentiment(c.element()));
    }
}
