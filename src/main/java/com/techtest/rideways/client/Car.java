package com.techtest.rideways.client;

import com.techtest.rideways.types.Option.CarType;

public class Car {

    private CarType carType;
    private String supplier;
    private int passengers;
    private int price;

    public Car(CarType carType, String supplier, int price) {
        this.carType = carType;
        this.supplier = supplier;
        this.price = price;

        switch (carType) {
            case STANDARD:
                passengers = 4;
                break;
            case EXECUTIVE:
                passengers = 4;
                break;
            case LUXURY:
                passengers = 4;
                break;
            case PEOPLE_CARRIER:
                passengers = 6;
                break;
            case LUXURY_PEOPLE_CARRIER:
                passengers = 6;
                break;
            case MINIBUS:
                passengers = 16;
                break;
        }
    }

    public String getCarType() {
        return carType.value();
    }

    public String getSupplier() {
        return supplier;
    }

    public int getPassengers() {
        return passengers;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public int hashCode() {
        return price * 31 + carType.value().hashCode() + supplier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Car c1 = (Car)obj;
        return (c1.getCarType().equals(this.carType.value())
            && c1.getSupplier().equals(this.supplier)
            && c1.getPrice() == this.price);
    }

}
