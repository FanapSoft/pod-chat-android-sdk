package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class RequestGetLastSeens {

    private ArrayList<Integer> userIds;

    public RequestGetLastSeens(@NonNull Builder builder) {
        this.userIds = builder.userIds;
    }

    public static class Builder{

        private ArrayList<Integer> userIds;


        public Builder(ArrayList<Integer> userIds) {
            this.userIds = userIds;
        }


        @NonNull
        public Builder userIds(ArrayList<Integer> userIds){

            this.userIds = userIds;
            return this;

        }

        @NonNull
        public RequestGetLastSeens build(){return new RequestGetLastSeens(this);}



    }

    public ArrayList<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<Integer> userIds) {
        this.userIds = userIds;
    }
}
