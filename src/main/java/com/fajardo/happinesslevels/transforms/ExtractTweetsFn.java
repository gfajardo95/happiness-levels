package com.fajardo.happinesslevels.transforms;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.fajardo.happinesslevels.models.Tweet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.beam.sdk.transforms.DoFn;

import lombok.extern.slf4j.Slf4j;

/**
 * decodes base64 encoded messages from a Pub/Sub topic into an equivalent Map.
 * The list of 'tweets' in the Map are encoded to Avro and returned for further
 * pipeline execution
 */
@Slf4j
public class ExtractTweetsFn extends DoFn<String, Tweet> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private ObjectMapper objectMapper;

    @Setup
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @ProcessElement
    public void ProcessElement(ProcessContext c) {
        String decodedString = new String(Base64.getUrlDecoder().decode(c.element()));

        try {
            Map<String, List<Map<String, Tweet>>> twraw = objectMapper.readValue(decodedString,
                    new TypeReference<Map<String, List<Map<String, Tweet>>>>() {
                    });
            List<Map<String, Tweet>> twmessages = twraw.get("messages");

            for (Map<String, Tweet> message : twmessages) {
                Tweet tw = message.get("data");
                c.output(tw);
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing the Pub/Sub JSON string: ", e);
        }
    }
}
