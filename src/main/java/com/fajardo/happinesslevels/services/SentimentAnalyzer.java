package com.fajardo.happinesslevels.services;

import java.util.Properties;

import com.fajardo.happinesslevels.models.Tweet;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SentimentAnalyzer {

    public Tweet getTweetWithSentiment(Tweet tweet) {
        RedwoodConfiguration.current().clear().apply();  // disable logging from Stanford CoreNLP
        
        Tweet tweetWithSentiment = null;

        if (tweet.getText() != null && tweet.getText().length() > 0) {
            int mainSentiment = 0;
            int longest = 0;

            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

            Annotation doc = new Annotation(tweet.getText());
            pipeline.annotate(doc);

            for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);

                String partText = sentence.toString();
                if (partText.length() > longest) {
                    longest = partText.length();
                    mainSentiment = sentiment;
                }
            }
            
            log.info(tweet.getText() + ": " + mainSentiment);
            tweetWithSentiment = new Tweet("", tweet.getCountry(), mainSentiment);
        }

        return tweetWithSentiment;
    }
}
