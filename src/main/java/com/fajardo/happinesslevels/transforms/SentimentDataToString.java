package com.fajardo.happinesslevels.transforms;

import com.fajardo.happinesslevels.models.CountrySentiment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SentimentDataToString extends PTransform<PCollection<KV<String, Double>>, PCollection<String>> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PCollection<String> expand(PCollection<KV<String, Double>> row) {
        return row.apply(ParDo.of(new DoFn<KV<String, Double>, String>() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @ProcessElement
            public void ProcessElement(ProcessContext c) {
                CountrySentiment countrySentiment = new CountrySentiment(c.element().getKey(), c.element().getValue());
                log.info("{}: {}", countrySentiment.getCountry(), countrySentiment.getAverageSentiment());

                try {
                    c.output(objectMapper.writeValueAsString(countrySentiment));
                } catch (JsonProcessingException e) {
                    log.error("Error converting the CountrySentiment to a string: {}", e);
                }
            }
        }));
    }
}
