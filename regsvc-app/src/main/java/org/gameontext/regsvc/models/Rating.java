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
package org.gameontext.regsvc.models;

import org.ektorp.support.CouchDbDocument;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/*
 * Bean representing a user rating a room for an event
 */
@JsonInclude(Include.NON_NULL)
public class Rating extends CouchDbDocument {
    private static final long serialVersionUID = -5912973062793702168L;
    
    private String eventId;
    private String gameonId;    //this is the Game On id of the person doing the rating 
    private String siteId;      //the room that is being rated
    private int rating;         //rating for the room
    
    public String getEventId() {
        return eventId;
    }
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public String getGameonId() {
        return gameonId;
    }
    public void setGameonId(String gameonId) {
        this.gameonId = gameonId;
    }
    public String getSiteId() {
        return siteId;
    }
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
    
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    
}
