package services;

import static com.google.datastore.v1.client.DatastoreHelper.makeFilter;
import static com.google.datastore.v1.client.DatastoreHelper.makeValue;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.EntityResult;
import com.google.datastore.v1.PropertyFilter;
import com.google.datastore.v1.Query;
import com.google.datastore.v1.RunQueryRequest;
import com.google.datastore.v1.RunQueryResponse;
import com.google.datastore.v1.Value;
import com.google.datastore.v1.client.Datastore;
import com.google.datastore.v1.client.DatastoreException;
import com.google.datastore.v1.client.DatastoreHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CityDatastore {

    public static String getCountryOfOrigin(String city) {
        String countryOfOrigin = "";

        Query.Builder q = Query.newBuilder();
        q.addKindBuilder().setName("world_city");
        q.setFilter(makeFilter("city_ascii", PropertyFilter.Operator.EQUAL, makeValue(city)));

        Entity result = runQuery(q.build());
        if (result != null) {
            Map<String, Value> resultMap = result.getPropertiesMap();
            countryOfOrigin = resultMap.get("country").getStringValue();
            log.info("country of origin: {}", countryOfOrigin);
            
            return countryOfOrigin;
        }

        return countryOfOrigin;
    }

    private static Entity runQuery(Query query) {
        Entity entity = null;
        
        try {
            Datastore datastore = DatastoreHelper.getDatastoreFromEnv();
            RunQueryRequest.Builder request = RunQueryRequest.newBuilder();
            request.setQuery(query);

            RunQueryResponse response = datastore.runQuery(request.build());
            int resultCount = response.getBatch().getEntityResultsCount();

            EntityResult result = null;
            if (resultCount > 0) {
                log.info("result count is greater than 0");
                result = response.getBatch().getEntityResults(0);
                entity = result.getEntity();
            }
        } catch (DatastoreException e) {
            log.error("Error talking to the datastore: " + e.getMessage());
            System.exit(2);
        } catch (GeneralSecurityException exception) {
            log.error("Security error connecting to the datastore: " + exception.getMessage());
            System.exit(2);
        } catch (IOException exception) {
            log.error("I/O error connecting to the datastore: " + exception.getMessage());
            System.exit(2);
        }

        return entity;
    }
}
