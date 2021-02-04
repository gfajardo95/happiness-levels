package com.fajardo.happinesslevels.transforms;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.fajardo.happinesslevels.models.Tweet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.beam.sdk.transforms.DoFn;

/**
 * decodes base64 encoded messages from a Pub/Sub topic into an equivalent Map.
 * The list of 'tweets' in the Map are encoded to Avro and returned for further
 * pipeline execution
 */
public class ExtractTweetsFn extends DoFn<String, Tweet> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ProcessElement
    public void ProcessElement(ProcessContext c) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(c.element());
        String decodedString = new String(decodedBytes);

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, List<Map<String, Tweet>>>>() {}.getType();
        Map<String, List<Map<String, Tweet>>> twraw = gson.fromJson(decodedString, type);
        List<Map<String, Tweet>> twmessages = twraw.get("messages");

        for (Map<String, Tweet> message : twmessages) {
            Tweet tw = message.get("data");
            c.output(tw);
        }
    }
}
