package com.techtest.rideways.client;

import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;

public class App {

    public static void main(String [] args) {

        if (args.length < 2) {
            System.out.println("Usage java -jar ... <supplier_id> <pickup> <dropoff> [<num_passengers>]");
            return;
        }

        String supplier = args[0];
        String pickup = args[1];
        String dropoff = args[2];

        int num_passengers = 0;
        if (args.length > 3)
            num_passengers = Integer.parseInt(args[2]);

        Client client = new ClientImpl();

        if (supplier.equals("all")) {

            try {
                Map<String, Car> cars = client.getAllRides(pickup, dropoff, num_passengers);
                for (Car car : cars.values()) {
                    System.out.println(car.getCarType() + " " + car.getSupplier() + " " + car.getPrice());
                }
            } catch (BadRequestException e) {
                System.out.println(e.getMessage());
            }

        } else {

            try {
                Map<String, Integer> cars = client.getSupplierRides(supplier, pickup, dropoff, num_passengers);
                for (Entry<String, Integer> car : cars.entrySet()) {
                    System.out.println(car.getKey() + " " + car.getValue());
                }
            } catch (BadRequestException e) {
                System.out.println(e.getMessage());
            } catch (InternalServerErrorException e) {
                System.out.println(e.getMessage());
            } catch (ProcessingException e) {
                System.out.println(e.getMessage());
            }

        }
    }

}
