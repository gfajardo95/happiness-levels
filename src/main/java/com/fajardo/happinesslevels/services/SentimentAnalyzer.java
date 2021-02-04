package com.fajardo.happinesslevels.services;

import java.util.List;
import java.util.Properties;

import org.apache.commons.math3.util.Precision;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

public class SentimentAnalyzer {

    public double getSentimentFromText(String text) {
        RedwoodConfiguration.current().clear().apply();  // disable logging from Stanford CoreNLP

        double weightedSentiment = 0;

        if (text != null && text.length() > 0) {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
            
            Annotation doc = new Annotation(text);
            pipeline.annotate(doc);

            int sentiment = 0, sentimentWeights = 0, fragLength = 0, totalLength = 0;
            List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);

            /*a weighted sentiment for the text is calculated based on each sentence's sentiment and word count*/
            for (CoreMap sentence : sentences) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                //sentiments range 1 - 4: 1 is very bad, 2 is bad, 3 is good, and 4 is very good
                sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String textFrag = sentence.toString();
                fragLength = textFrag.split("\\s+").length;  //sentence's word count
                totalLength += fragLength;
                sentimentWeights += fragLength * sentiment;
            }

            weightedSentiment = (double)sentimentWeights / (double)totalLength;
        }

        return Precision.round(weightedSentiment, 4);
    }
}
