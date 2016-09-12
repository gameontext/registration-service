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

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.gameontext.regsvc.Log;

public class CouchInjector {
    @Resource(lookup="couchdb/connector")
    protected CouchDbInstance dbi;

    @Produces
    @DbConfig
    public CouchDbConnector expose(InjectionPoint ip) {

        try {
            DbConfig config = ip.getAnnotated().getAnnotation(DbConfig.class);
            // Connect to the database with the specified
            CouchDbConnector dbc = dbi.createConnector(config.name(), true);
            org.gameontext.regsvc.Log.log(Level.FINER, this, "Connected to {0}", config.name());
            return dbc;
        } catch (Exception e) {
            // Log the warning, and then re-throw to prevent this class from going into service,
            // which will prevent injection to the Health check, which will make the app stay down.
            Log.log(Level.WARNING, this, "Unable to connect to database", e);
            throw e;
        }
    }
}
