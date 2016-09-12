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
package org.gameontext.regsvc;

import java.net.HttpURLConnection;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gameontext.regsvc.db.RatingRepository;
import org.gameontext.regsvc.models.Rating;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Root for controlling room ratings
 */
@Path("/rate")
@Api( tags = {"register"})
@Produces(MediaType.APPLICATION_JSON)
public class RatingEndpoint {

    @Inject
    protected RatingRepository repo;

    /**
     * GET /regsvc/v1/rate
     * @throws JsonProcessingException
     */
    @GET
    @ApiOperation(value = "List rooms ratings",
        notes = "Provides a list of room ratings for a given event.",
        code = HttpURLConnection.HTTP_OK)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRatings() {
        return Response.ok(repo.getRatings()).build();
    }
    
    /**
     * POST /regsvc/v1/rate
     * @throws JsonProcessingException
     */
    @POST
    @ApiOperation(value = "Rate a registered room",
        notes = "Rate a room for a given event",
        code = HttpURLConnection.HTTP_OK)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response rateRoom(
            @ApiParam(value = "Room to rate", required = true) Rating rating) {
        repo.createRating(rating);
        return Response.ok(rating).build();
    }


}
