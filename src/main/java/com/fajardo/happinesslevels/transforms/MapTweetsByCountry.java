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
public class MapTweetsByCountry extends SimpleFunction<Tweet, KV<String, Double>> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public KV<String, Double> apply(Tweet tweet) {
        // String[] locationTokens = tweet.getLocation().split(",");
        // String countryOfOrigin = getCountryOfOrigin(locationTokens[0]);  does not work if "city, country" format is not provided
        log.info("tweet location: {}", tweet.getLocation());

        return KV.of("USA", tweet.getSentiment());
    }
}
