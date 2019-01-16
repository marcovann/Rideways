package com.techtest.rideways.client;

import com.techtest.rideways.types.Option;
import com.techtest.rideways.types.Option.CarType;
import com.techtest.rideways.types.Ride;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;

public class ClientImpl implements Client {

    private static final String[] SUPPLIERS = {"dave", "eric", "jeff"};

    private boolean isCoordinate(String s) {
        String[] coord = s.split(",");
        try {
            Double.parseDouble(coord[0]);
            Double.parseDouble(coord[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public Map<String, Integer> getSupplierRides(String supplier_id, String pickup, String dropoff, int num_passengers)
            throws InternalServerErrorException, ProcessingException, BadRequestException {

        Map<String, Integer> cars = new LinkedHashMap<>();
        ServiceImpl client = new ServiceImpl();

        if (!Arrays.asList(SUPPLIERS).contains(supplier_id))
            throw new BadRequestException("Supplier not found.");
        if (!isCoordinate(pickup))
            throw new BadRequestException("Pickup is malformed.");
        if (!isCoordinate(dropoff))
            throw new BadRequestException("Dropoff is malformed.");

        try {
            Ride ride = client.getRide(supplier_id, pickup, dropoff);
            List<Option> options = new ArrayList<>(ride.getOptions());

            for (Option option : options) {

                CarType carType = option.getCarType();
                String supplierId = ride.getSupplierId();
                int price = option.getPrice();
                Car car = new Car(carType, supplierId, price);

                if (car.getPassengers() >= num_passengers) {
                    cars.put(carType.value(), price);
                }
            }

        } catch(InternalServerErrorException e){
            throw new InternalServerErrorException(e.getMessage());
        } catch(ProcessingException e){
            throw new ProcessingException(e.getMessage());
        } catch(BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        }

        return cars;
    }

    @Override
    public Map<String, Car> getAllRides(String pickup, String dropoff, int num_passengers)
        throws InternalServerErrorException, ProcessingException, BadRequestException {

        Map<String, Car> cars = new LinkedHashMap<>();
        ServiceImpl client = new ServiceImpl();

        for (String supplier : SUPPLIERS) {

            try {
                Ride ride = client.getRide(supplier, pickup, dropoff);
                List<Option> options = new ArrayList<>(ride.getOptions());

                for (Option option : options) {

                    CarType carType = option.getCarType();
                    String supplierId = ride.getSupplierId();
                    int price = option.getPrice();
                    Car car = new Car(carType, supplierId, price);

                    if (car.getPassengers() >= num_passengers) {
                        Car c = cars.get(car.getCarType());
                        if (c == null || price < c.getPrice()) {
                            cars.put(carType.value(), car);
                        }
                    }
                }

            } catch(InternalServerErrorException | ProcessingException | BadRequestException e){
                System.out.println(e.getMessage());
            }

        }

        return cars;
    }

}
