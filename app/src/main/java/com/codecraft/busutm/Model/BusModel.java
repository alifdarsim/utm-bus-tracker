package com.codecraft.busutm.Model;

public class BusModel {

    private String plate;
    private String route;
    private String driverName;

    public BusModel(String plate, String route, String driverName) {
        this.plate=plate;
        this.route=route;
        this.driverName=driverName;
    }

    public String getPlate() {
        return this.plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getRoute() {
        return this.route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDriverName() {
        return this.driverName;
    }
}
