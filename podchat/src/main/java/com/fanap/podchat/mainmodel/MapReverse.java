package com.fanap.podchat.mainmodel;

public class MapReverse {

    private String status;
    private String message;
    private int code;
    private String address;
    private String neighbourhood;
    private String municipality_zone;
    private boolean in_traffic_zone;
    private boolean in_odd_even_zone;
    private String city;
    private String state;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getMunicipality_zone() {
        return municipality_zone;
    }

    public void setMunicipality_zone(String municipality_zone) {
        this.municipality_zone = municipality_zone;
    }

    public boolean isIn_traffic_zone() {
        return in_traffic_zone;
    }

    public void setIn_traffic_zone(boolean in_traffic_zone) {
        this.in_traffic_zone = in_traffic_zone;
    }

    public boolean isIn_odd_even_zone() {
        return in_odd_even_zone;
    }

    public void setIn_odd_even_zone(boolean in_odd_even_zone) {
        this.in_odd_even_zone = in_odd_even_zone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
