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

import java.util.List;
import java.util.logging.Level;

import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.gameontext.regsvc.Log;
import org.gameontext.regsvc.models.Rating;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Repository tracking and working with registered rooms
 *
 * <pre>
 * {
 *   "eventId" : "myEvent",
 *   "siteId": "1d1e2f39f95ee2ad88f40e67a5006748",
 *   "gameonId": "github:123456789"
 *   "rating" : 2
 * }
 * </pre>
 */
public class RatingDocuments {

    protected static final String DESIGN_DOC = "_design/rating";

    protected final CouchDbConnector db;
    protected final ViewQuery all;
    protected final ObjectMapper mapper;

    protected RatingDocuments(CouchDbConnector db) {
        this.db = db;
        all = new ViewQuery().designDocId(DESIGN_DOC).viewName("all").cacheOk(true);
        mapper = new ObjectMapper();
    }
    
    /**
     * Rate a room for an event
     */
    public void rateRoom(Rating rating) {
        Log.mapOperations(Level.FINE, this, "Add new rating: {0}", rating);

        try {
            System.out.println("Rating room : " + mapper.writeValueAsString(rating));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            db.create(rating);
        } catch (UpdateConflictException ex) {
            // If there is a conflict, update with the new rating
            db.update(rating);
        }
    }
    
    public List<Rating> getRatings() {
        return db.queryView(all, Rating.class);
    }

 }
