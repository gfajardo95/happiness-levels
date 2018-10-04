import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Key;
import com.google.datastore.v1.Value;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreIO;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatastoreWriter {
    static class CreateEntityFn extends DoFn<String, Entity> {
        private Entity makeEntity(String content){
            String[] props = content.split(",");
            Key key = Key.newBuilder().addPath(Key.PathElement.newBuilder()
                            .setKind("world_city")
                            .setName(UUID.randomUUID().toString())).build();
            Entity.Builder entityBuilder = Entity.newBuilder();
            entityBuilder.setKey(key);
            Map<String, Value> propertyMap = new HashMap<>();
            propertyMap.put("city_ascii", Value.newBuilder().setStringValue(props[0]).build());
            propertyMap.put("country", Value.newBuilder().setStringValue(props[1]).build());
            propertyMap.put("iso2", Value.newBuilder().setStringValue(props[2]).build());
            propertyMap.put("iso3", Value.newBuilder().setStringValue(props[3]).build());
            entityBuilder.putAllProperties(propertyMap);
            return entityBuilder.build();
        }

        @ProcessElement
        public void processElement(ProcessContext c){
            c.output(makeEntity(c.element()));
        }
    }
    public interface Options extends PipelineOptions {
        @Description("Get Cloud Storage input file")
        @Validation.Required
        String getInput();

        void setInput(String value);
    }
    /*
    * Run command: mvn compile exec:java -D exec.mainClass=DatastoreWriter -D exec.args="--runner=DataFlowRunner --project=happiness-level --gcpTempLocation=gs://dataflow-happiness-level/writer/tmp --input=gs://world_cities/worldcities.csv --workerMachineType=n1-standard-1 --maxNumWorkers=2" -Pdataflow-runner
    *
    * Writes a .csv file in a Cloud Storage bucket to Cloud Datastore.
    * The .csv file has columns: city_ascii, country, iso2, iso3
    * */
    public static void main(String... args) {
        Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
        Pipeline p = Pipeline.create(options);

        p.apply(TextIO.read().from(options.getInput()))
                .apply(ParDo.of(new CreateEntityFn()))
                .apply(DatastoreIO.v1().write().withProjectId("happiness-level"));
        p.run().waitUntilFinish();
    }
}
