package com.techtest.rideways.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import com.techtest.rideways.types.Error;
import com.techtest.rideways.types.Option;
import com.techtest.rideways.types.Option.CarType;
import com.techtest.rideways.types.Ride;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().port(9090));

    @Before
    public void setUp() {
        System.setProperty("api.url", "http://localhost:9090");
    }

    private List<Option> initDave() {
        List<Option> options = new ArrayList<>();
        Option people_carrier = new Option();
        people_carrier.setCarType(CarType.valueOf("PEOPLE_CARRIER"));
        people_carrier.setPrice(583438);

        Option minibus = new Option();
        minibus.setCarType(CarType.valueOf("MINIBUS"));
        minibus.setPrice(37456);

        options.add(people_carrier);
        options.add(minibus);
        return options;
    }

    @Test
    public void getDavesRide() {

        String supplier_id = "dave";
        String pickup = "51.8342,50.6721";
        String dropoff = "51.8134,50.1276";
        String url = "/" + supplier_id + "?pickup=" + pickup + "&dropoff=" + dropoff;

        Ride ride = new Ride();
        ride.setSupplierId("DAVE");
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

            ServiceImpl client = new ServiceImpl();
            Ride rideResult = client.getRide(supplier_id, pickup, dropoff);

            List<Option> options = new ArrayList<>(rideResult.getOptions());
            Map<String, Integer> test = new LinkedHashMap<>();
            for (Option o : options) {
                test.put(o.getCarType().value(), o.getPrice());
            }

            assertEquals(reference, test);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @Test(expected = InternalServerErrorException.class)
    public void getDavesRideInternalServerError() {

        String supplier_id = "dave";
        String pickup = "51.8342,50.6721";
        String dropoff = "51.8134,50.1276";
        String url = "/" + supplier_id + "?pickup=" + pickup + "&dropoff=" + dropoff;

        Error error = new Error();
        error.setTimestamp("2018-08-14T13:12:34.072+0000");
        error.setStatus(500);
        error.setError("Internal Server Error");
        error.setMessage("Something has gone wrong");
        error.setPath("/dave");

        ObjectMapper mapper = new ObjectMapper();

        try {
            stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                    .withStatus(500)
                    .withHeader("Content-Type", "text/json")
                    .withBody(mapper.writeValueAsString(error))));

            ServiceImpl client = new ServiceImpl();
            Ride ride = client.getRide(supplier_id, pickup, dropoff);

            List<Option> options = new ArrayList<>(ride.getOptions());
            Map<String, Integer> test = new LinkedHashMap<>();
            for (Option o : options) {
                test.put(o.getCarType().value(), o.getPrice());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = ProcessingException.class)
    public void getDavesRideTimeout() {

        String supplier_id = "dave";
        String pickup = "51.8342,50.6721";
        String dropoff = "51.8134,50.1276";
        String url = "/" + supplier_id + "?pickup=" + pickup + "&dropoff=" + dropoff;

        Ride ride = new Ride();
        ride.setSupplierId("DAVE");
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
                    .withStatus(200)
                    .withHeader("Content-Type", "text/json")
                    .withFixedDelay(3000)
                    .withBody(mapper.writeValueAsString(ride))));

            ServiceImpl client = new ServiceImpl();
            Ride rideResult = client.getRide(supplier_id, pickup, dropoff);

            List<Option> options = new ArrayList<Option>(rideResult.getOptions());
            Map<String, Integer> test = new LinkedHashMap<>();
            for (Option o : options) {
                test.put(o.getCarType().value(), o.getPrice());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}