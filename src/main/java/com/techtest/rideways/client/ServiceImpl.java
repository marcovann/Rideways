package com.techtest.rideways.client;

import com.techtest.rideways.types.Error;
import com.techtest.rideways.types.Ride;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.client.ClientProperties;

public class ServiceImpl {

    private static final String API_URL =
        System.getProperty("api.url") != null ?
        System.getProperty("api.url") :
        "https://techtest.rideways.com";

    private Client client;

    public ServiceImpl() {
        client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 2000);
        client.property(ClientProperties.READ_TIMEOUT, 2000);
    }

    public Ride getRide(String supplier, String pickup, String dropoff)
        throws ProcessingException, BadRequestException, InternalServerErrorException {

        Response response = client
            .target(API_URL)
            .path(supplier)
            .queryParam("pickup", pickup)
            .queryParam("dropoff", dropoff)
            .request(MediaType.APPLICATION_JSON)
            .get();

        if (response.getStatus() == Status.REQUEST_TIMEOUT.getStatusCode()) {
            Error error = response.readEntity(Error.class);
            throw new ProcessingException(error.getMessage());
        } else if (response.getStatus() == Status.BAD_REQUEST.getStatusCode()) {
            Error error = response.readEntity(Error.class);
            throw new BadRequestException(error.getMessage());
        } else if (response.getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            Error error = response.readEntity(Error.class);
            throw new InternalServerErrorException(error.getMessage());
        }

        return response.readEntity(Ride.class);
    }

}