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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gameontext.regsvc.db.RegistrationRepository;
import org.gameontext.regsvc.models.Registration;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Root for controlling room registrations
 */
@Path("/register")
@Api( tags = {"register"})
@Produces(MediaType.APPLICATION_JSON)
public class RegistrationEndpoint {

    @Inject
    protected RegistrationRepository repo;

    /**
     * GET /regsvc/v1/register
     * @throws JsonProcessingException
     */
    @GET
    @ApiOperation(value = "List rooms registered for events",
        notes = "Provides a list of rooms that are registered for a given event.",
        code = HttpURLConnection.HTTP_OK)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegistrations() {
        return Response.ok(repo.getRegistrations()).build();
    }
    
	/**
     * POST /regsvc/v1/register
     * @throws JsonProcessingException
     */
    @POST
    @ApiOperation(value = "Create a new registration",
        notes = "Register a room for a given event",
        code = HttpURLConnection.HTTP_OK)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerRoom(
            @ApiParam(value = "Room to register", required = true) Registration registration) {
        registration.setId(registration.getSiteId());   //use the site id as the doc id for the database
        if(repo.createRegistration(registration)) {
            return Response.ok(registration).build();
        } else {
            //this ID already exists
            return Response.status(Status.CONFLICT).build();
        }
    }


    /**
     * DELETE /regsvc/v1/register/:id
     */
    @DELETE
    //@SignedRequest
    @Path("{id}")
    @ApiOperation(value = "Delete a room associated with an event",
        notes = "",
        code = 204 )
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Delete successful")
    })
    public Response deleteRoom(
            @ApiParam(value = "target room id", required = true) @PathParam("id") String roomId) {

        return Response.noContent().build();
    }

}
