package com.techtest.rideways.client;

import com.techtest.rideways.types.Ride;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;

public interface Service {

    public Ride getRide(String supplier, String pickup, String dropoff)
        throws ProcessingException, BadRequestException, InternalServerErrorException;

}
