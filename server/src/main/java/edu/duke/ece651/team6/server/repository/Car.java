package edu.duke.ece651.team6.server.repository;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

import java.io.Serializable;

//@Entity("car")
public class Car implements Serializable {
    //@Id
    private String id;
    private String name;
    private String color;
    private int year;

    Car(String id, String name, String color, int year) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.year = year;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getYear() {
        return year;
    }
}
