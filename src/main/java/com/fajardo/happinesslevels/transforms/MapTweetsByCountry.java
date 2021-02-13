package com.fajardo.happinesslevels.transforms;

import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;

import lombok.extern.slf4j.Slf4j;
import com.fajardo.happinesslevels.models.Tweet;

/**
 * creates key/value pairs for the tweets where each tweet's key is the country
 * in which it's written
 */
@Slf4j
public class MapTweetsByCountry extends SimpleFunction<Tweet, KV<String, Integer>> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public KV<String, Integer> apply(Tweet tweet) {
        log.info("tweet location: {}", tweet.getCountry());

        return KV.of(tweet.getCountry(), tweet.getSentiment());
    }
}
