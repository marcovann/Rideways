package com.techtest.rideways.server;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.techtest.rideways.client.Car;
import com.techtest.rideways.types.Option;
import com.techtest.rideways.types.Option.CarType;
import com.techtest.rideways.types.Result;
import com.techtest.rideways.types.Ride;
import com.techtest.rideways.types.RideResult;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import org.junit.Rule;
import org.junit.Test;

public class ServerTest {

    private static final String SERVER_URL = System.getProperty("server.url");
    private static final String SERVER_PATH = System.getProperty("server.path");

    private static final String pickup = "51.8342,50.6721";
    private static final String dropoff = "51.8134,50.1276";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().port(Integer.parseInt(System.getProperty("server.port.mock"))));

    private List<Option> initDave() {
        List<Option> options = new ArrayList<>();
        Option people_carrier = new Option();
        people_carrier.setCarType(CarType.valueOf("PEOPLE_CARRIER"));
        people_carrier.setPrice(593438);

        Option minibus = new Option();
        minibus.setCarType(CarType.valueOf("MINIBUS"));
        minibus.setPrice(37456);

        options.add(people_carrier);
        options.add(minibus);
        return options;
    }

    private List<Option> initEric() {
        List<Option> options = new ArrayList<>();
        Option standard = new Option();
        standard.setCarType(CarType.valueOf("STANDARD"));
        standard.setPrice(123679);

        Option minibus = new Option();
        minibus.setCarType(CarType.valueOf("MINIBUS"));
        minibus.setPrice(374560);

        options.add(standard);
        options.add(minibus);
        return options;
    }

    private List<Option> initJeff() {
        List<Option> options = new ArrayList<>();
        Option standard = new Option();
        standard.setCarType(CarType.valueOf("STANDARD"));
        standard.setPrice(223679);

        Option people_carrier = new Option();
        people_carrier.setCarType(CarType.valueOf("PEOPLE_CARRIER"));
        people_carrier.setPrice(83438);

        options.add(standard);
        options.add(people_carrier);
        return options;
    }


    @Test
    public void getDavesRideWithNumPassengers() {

        String supplier_id = "dave";
        String url = "/" + supplier_id + "?pickup=" + pickup + "&dropoff=" + dropoff;
        int num_passengers = 6;

        Ride ride = new Ride();
        ride.setPickup("DAVE");
        ride.setPickup(pickup);
        ride.setDropoff(dropoff);
        ride.setOptions(initDave());

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Integer> reference = new LinkedHashMap<>();
        for (Option option : ride.getOptions())
            reference.put(option.getCarType().value(), option.getPrice());

        try {
            stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "text/json")
                    .withBody(mapper.writeValueAsString(ride))));

            Client client = ClientBuilder.newClient();
            Result response = client
                .target(SERVER_URL)
                .path(SERVER_PATH).path("rest")
                .path(supplier_id)
                .queryParam("pickup", pickup)
                .queryParam("dropoff", dropoff)
                .queryParam("num_passengers", num_passengers)
                .request(MediaType.APPLICATION_JSON)
                .get(Result.class);

            Map<String, Integer> test = new LinkedHashMap<>();
            for (RideResult rideResult : response.getRideResults()) {
                CarType carType = CarType.fromValue(rideResult.getCarType());
                Integer price = rideResult.getPrice();
                Car car = new Car(carType, supplier_id, price);
                if (car.getPassengers() >= num_passengers) {
                    test.put(carType.value(), price);
                }
            }

            assertEquals(reference, test);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllRides() {

        String supplier_id_dave = "dave";
        String supplier_id_eric = "eric";
        String supplier_id_jeff = "jeff";
        String url_dave = "/" + supplier_id_dave + "?pickup=" + pickup + "&dropoff=" + dropoff;
        String url_eric = "/" + supplier_id_eric + "?pickup=" + pickup + "&dropoff=" + dropoff;
        String url_jeff = "/" + supplier_id_jeff + "?pickup=" + pickup + "&dropoff=" + dropoff;
        int num_passengers = 1;

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Car> reference = new LinkedHashMap<>();

        Ride ride = new Ride();
        ride.setPickup(pickup);
        ride.setDropoff(dropoff);

        try {

            ride.setSupplierId("DAVE");
            ride.setOptions(initDave());
            for (Option option : ride.getOptions()) {
                if (reference.get(option.getCarType().value()) == null
                    || reference.get(option.getCarType().value()).getPrice() > option.getPrice())
                reference.put(option.getCarType().value(), new Car(option.getCarType(), "DAVE", option.getPrice()));
            }

            stubFor(get(urlEqualTo(url_dave))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "text/json")
                    .withBody(mapper.writeValueAsString(ride))));

            ride.setSupplierId("ERIC");
            ride.setOptions(initEric());
            for (Option option : ride.getOptions()) {
                if (reference.get(option.getCarType().value()) == null
                    || reference.get(option.getCarType().value()).getPrice() > option.getPrice())
                    reference.put(option.getCarType().value(), new Car(option.getCarType(), "ERIC", option.getPrice()));
            }

            stubFor(get(urlEqualTo(url_eric))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "text/json")
                    .withBody(mapper.writeValueAsString(ride))));

            ride.setSupplierId("JEFF");
            ride.setOptions(initJeff());
            for (Option option : ride.getOptions()) {
                if (reference.get(option.getCarType().value()) == null
                    || reference.get(option.getCarType().value()).getPrice() > option.getPrice())
                    reference.put(option.getCarType().value(), new Car(option.getCarType(), "JEFF", option.getPrice()));
            }

            stubFor(get(urlEqualTo(url_jeff))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "text/json")
                    .withBody(mapper.writeValueAsString(ride))));

            Client client = ClientBuilder.newClient();
            Result response = client
                .target(SERVER_URL)
                .path(SERVER_PATH).path("rest")
                .queryParam("pickup", pickup)
                .queryParam("dropoff", dropoff)
                .queryParam("num_passengers", num_passengers)
                .request(MediaType.APPLICATION_JSON)
                .get(Result.class);

            Map<String, Car> test = new LinkedHashMap<>();
            for (RideResult rideResult : response.getRideResults()) {
                CarType carType = CarType.fromValue(rideResult.getCarType());
                String supplierId = rideResult.getSupplier();
                Integer price = rideResult.getPrice();
                Car car = new Car(carType, supplierId, price);
                test.put(carType.value(), car);
            }

            assertEquals(reference, test);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
