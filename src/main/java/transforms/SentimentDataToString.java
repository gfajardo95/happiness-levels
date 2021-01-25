package transforms;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;

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
                c.output(c.element().getKey() + ": " + c.element().getValue());
            }
        }));
    }
}
