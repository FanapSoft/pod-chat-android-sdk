package com.fanap.podchat.mainmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapRoutItem {
    private List<LegsItem> legs;
    @SerializedName("overview_polyline")
    private OverviewPolyline overviewPolyline;


    public OverviewPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public List<LegsItem> getLegs() {
        return legs;
    }

    public void setLegs(List<LegsItem> legs) {
        this.legs = legs;
    }
}
