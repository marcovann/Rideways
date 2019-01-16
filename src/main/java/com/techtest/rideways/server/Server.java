package com.techtest.rideways.server;

import com.techtest.rideways.client.Car;
import com.techtest.rideways.client.Client;
import com.techtest.rideways.client.ClientImpl;
import com.techtest.rideways.types.Result;
import com.techtest.rideways.types.RideResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/")
public class Server {

	@GET
	@Path("/{supplier_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRides(
		@PathParam("supplier_id") String supplier_id,
		@QueryParam("pickup") String pickup,
		@QueryParam("dropoff") String dropoff,
		@DefaultValue("0") @QueryParam("num_passengers") Integer num_passengers) {

		Client client = new ClientImpl();
		List<RideResult> rides = new ArrayList<>();

		try {
			Map<String, Integer> cars = client.getSupplierRides(supplier_id, pickup, dropoff, num_passengers);
			for (Entry<String, Integer> car : cars.entrySet()) {
				RideResult ride = new RideResult();
				ride.setCarType(car.getKey());
				ride.setPrice(car.getValue());
				rides.add(ride);
			}
		} catch (InternalServerErrorException e) {
			Response.status(Status.INTERNAL_SERVER_ERROR).entity("ERROR").build();
		} catch (ProcessingException e) {
			Response.status(Status.REQUEST_TIMEOUT).entity("TIMEOUT").build();
		}

		Result result = new Result();
		result.setRideResults(rides);
		return Response.status(Status.OK).entity(result).build();
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBestRides(
		@QueryParam("pickup") String pickup,
		@QueryParam("dropoff") String dropoff,
		@DefaultValue("0") @QueryParam("num_passengers") Integer num_passengers) {

		Client client = new ClientImpl();
		List<RideResult> rides = new ArrayList<>();

		try {
			Map<String, Car> cars = client.getAllRides(pickup, dropoff, num_passengers);
			for (Car car : cars.values()) {
				RideResult ride = new RideResult();
				ride.setCarType(car.getCarType());
				ride.setSupplier(car.getSupplier());
				ride.setPrice(car.getPrice());
				rides.add(ride);
			}
		} catch (InternalServerErrorException e) {
			Response.status(Status.INTERNAL_SERVER_ERROR).entity("ERROR").build();
		} catch (ProcessingException e) {
			Response.status(Status.REQUEST_TIMEOUT).entity("TIMEOUT").build();
		}

		Result result = new Result();
		result.setRideResults(rides);
		return Response.status(Status.OK).entity(result).build();
	}

}
