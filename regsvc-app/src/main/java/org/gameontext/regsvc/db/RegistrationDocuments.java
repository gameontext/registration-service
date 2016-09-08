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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.gameontext.regsvc.Log;
import org.gameontext.regsvc.models.Registration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Repository tracking and working with registered rooms
 *
 * <pre>
 * {
 *   "eventId" : "myEvent",
 *   "siteId": "1d1e2f39f95ee2ad88f40e67a5006748",
 *   "gameonId": "github:123456789"
 * }
 * </pre>
 */
public class RegistrationDocuments {

    protected static final String DESIGN_DOC = "_design/registration";

    protected final CouchDbConnector db;
    protected final ViewQuery all;
    protected final ObjectMapper mapper;

    protected RegistrationDocuments(CouchDbConnector db) {
        this.db = db;
        all = new ViewQuery().designDocId(DESIGN_DOC).viewName("all").cacheOk(true);
        mapper = new ObjectMapper();
    }

    /**
     * LIST
     * @param owner Owner of sites (optional)
     * @param name Name of site/room (optional)
     * @return List of all sites, possibly filtered by owner and/or name. Will not return null.
     */
    public List<JsonNode> listRegistrations(String eventId) {

        List<JsonNode> registrations = Collections.emptyList();

        registrations = db.queryView(all, JsonNode.class);

        if ( registrations == null )
            return Collections.emptyList();

        return registrations;
    }

    /**
     * Register a room for an event
     */
    public boolean registerRoom(Registration reg) {
        Log.mapOperations(Level.FINE, this, "Add new registration: {0}", reg);

        try {
            System.out.println("Registering room : " + mapper.writeValueAsString(reg));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            db.create(reg);
        } catch (UpdateConflictException ex) {
            // If there is a conflict, we'll return false so that the caller tries again.
            return false;
        }
        
        return true;
    }
    
    public List<Registration> getRegistrations() {
        return db.queryView(all, Registration.class);
    }

 }
