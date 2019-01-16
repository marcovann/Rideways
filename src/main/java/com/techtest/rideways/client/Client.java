package com.techtest.rideways.client;

import java.util.Map;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;

public interface Client {

    public Map<String, Integer> getSupplierRides(String supplier_id, String pickup, String dropoff, int num_passengers)
            throws InternalServerErrorException, ProcessingException, BadRequestException, NotFoundException;

    public Map<String, Car> getAllRides(String pickup, String dropoff, int num_passengers)
        throws InternalServerErrorException, ProcessingException, BadRequestException;

}
