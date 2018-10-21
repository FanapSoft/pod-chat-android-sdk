package com.fanap.podchat.mainmodel;

import java.util.List;

public class LegsItem {
    private String summary;
    private Distance distance;
    private MapDuration duration;
    private List<Steps> steps;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public MapDuration getDuration() {
        return duration;
    }

    public void setDuration(MapDuration duration) {
        this.duration = duration;
    }

    public List<Steps> getSteps() {
        return steps;
    }

    public void setSteps(List<Steps> steps) {
        this.steps = steps;
    }
}
