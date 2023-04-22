package edu.duke.ece651.team6.server.repository;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//@Entity("test")
public class TestEntity implements Serializable {

    private String id;
    private String name;


    private Car car;


    private Car anotherCar;


    private Map<Car, Map<Car, Integer>> map;

    public TestEntity(String id, String name, Car car, Car anotherCar, Map<Car, Map<Car, Integer>> map) {
        this.id = id;
        this.name = name;
        this.car = car;
        this.anotherCar = anotherCar;
        this.map = map;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<Car, Map<Car, Integer>> getMap() {
        return map;
    }

    public Car getCar() {
        return car;
    }

    public Car getAnotherCar() {
        return anotherCar;
    }
}
