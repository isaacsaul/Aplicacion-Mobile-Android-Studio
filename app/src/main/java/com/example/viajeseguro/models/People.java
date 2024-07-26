package com.example.viajeseguro.models;

public class People {
    String id;
    String name;
    String surname;
    String license;
    String plate;
    String brand;
    String color;

    public People(String id, String name, String surname, String license, String plate, String brand, String color) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.license = license;
        this.plate = plate;
        this.brand = brand;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getLicense() {
        return license;
    }

    public String getPlate() {
        return plate;
    }

    public String getBrand() {
        return brand;
    }

    public String getColor() {
        return color;
    }
}
