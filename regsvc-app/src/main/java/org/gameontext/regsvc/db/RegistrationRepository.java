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

import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ektorp.CouchDbConnector;
import org.gameontext.regsvc.Log;
import org.gameontext.regsvc.models.Registration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@ApplicationScoped
public class RegistrationRepository {

    @Inject
    protected CouchDbConnector db;

    protected RegistrationDocuments registrations;

    protected ObjectMapper mapper;

    //@Inject
    //Kafka kafka;

    @PostConstruct
    protected void postConstruct() {
        // Create an ObjectMapper for marshalling responses back to REST clients
        mapper = new ObjectMapper();

        try {
            // Ensure required views exist
            registrations = new RegistrationDocuments(db);

        } catch (Exception e) {
            // Log the warning, and then re-throw to prevent this class from going into service,
            // which will prevent injection to the Health check, which will make the app stay down.
            Log.log(Level.WARNING, this, "Unable to connect to database", e);
            throw e;
        }
    }

    public String getRegistrations() {
        try {
            return mapper.writeValueAsString(registrations.getRegistrations());
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "{}";
        }
    }
    
    public boolean createRegistration(Registration registration) {
        return registrations.registerRoom(registration);
    }

}
