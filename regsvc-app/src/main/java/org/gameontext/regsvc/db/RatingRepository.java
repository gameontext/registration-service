/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.gameontext.regsvc.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ektorp.CouchDbConnector;
import org.gameontext.regsvc.Log;
import org.gameontext.regsvc.models.Rating;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@ApplicationScoped
public class RatingRepository {

    @Inject
    @DbConfig(name="rating_repository")
    protected CouchDbConnector db;

    protected RatingDocuments ratings;

    protected ObjectMapper mapper;

    //@Inject
    //Kafka kafka;

    @PostConstruct
    protected void postConstruct() {
        // Create an ObjectMapper for marshalling responses back to REST clients
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            // Ensure required views exist
            ratings = new RatingDocuments(db);

        } catch (Exception e) {
            // Log the warning, and then re-throw to prevent this class from going into service,
            // which will prevent injection to the Health check, which will make the app stay down.
            Log.log(Level.WARNING, this, "Unable to connect to database", e);
            throw e;
        }
    }

    public String getRatings() {
        try {
            List<Rating> data = ratings.getRatings();
            Map<String, Rating> results = new HashMap<>();
            for(Rating item : data) {
                if(!results.containsKey(item.getSiteId())) {
                    RatingAverager avg = data.stream()
                                                .filter(r -> r.getSiteId() == item.getSiteId())
                                                .map(Rating::getRating)
                                                .collect(RatingAverager::new, RatingAverager::accept, RatingAverager::combiner);
                    item.setRating(avg.calculate());
                    results.put(item.getSiteId(), item);                              
                }
                
            }
            return mapper.writeValueAsString(results.values());
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "{}";
        }
    }
    
    
    public void createRating(Rating rating) {
        ratings.rateRoom(rating);
    }
    
    private class RatingAverager implements IntConsumer {
        private int ratingCount = 0;    //how many ratings have been added to the sum
        private int sum = 0;            //total sum of ratings so far

        @Override
        public void accept(int value) {
            ratingCount++;
            sum += value;
        }
        
        //need a combiner to be able to use this in a collector
        public void combiner(RatingAverager value) {
            ratingCount += value.ratingCount;
            sum += value.sum;
        }
        
        //calculates the average
        public int calculate() {
            return (sum / ratingCount);
        }
        
    }

}
