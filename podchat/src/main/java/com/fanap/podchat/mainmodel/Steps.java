package com.fanap.podchat.mainmodel;

import java.util.List;

public class Steps {
    private String name;
    private String instruction;
    private Distance distance  ;
    private List<Double> start_location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public List<Double> getStart_location() {
        return start_location;
    }

    public void setStart_location(List<Double> start_location) {
        this.start_location = start_location;
    }
}
