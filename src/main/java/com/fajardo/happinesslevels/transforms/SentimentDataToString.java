package com.fajardo.happinesslevels.transforms;

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

    public PCollection<String> expand(PCollection<KV<String, Double>> row) {
        return row.apply(ParDo.of(new DoFn<KV<String, Double>, String>() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @ProcessElement
            public void ProcessElement(ProcessContext c) {
                String sentimentOfCountry = c.element().getKey() + ": " + c.element().getValue();
                log.info(sentimentOfCountry);
                c.output(sentimentOfCountry);
            }
        }));
    }
}
