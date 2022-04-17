package com.fanap.podchat.call.request_model;

import com.fanap.podcall.view.CallPartnerView;

public class SwapViewsRequest {


    private final long firstPartnerUserId;
    private final long secondPartnerUserId;

    private final CallPartnerView firstPartnerView;
    private final CallPartnerView secondPartnerView;

    SwapViewsRequest(Builder builder) {
        this.firstPartnerUserId = builder.firstPartnerUserId;
        this.secondPartnerUserId = builder.secondPartnerUserId;
        this.firstPartnerView = builder.firstPartnerView;
        this.secondPartnerView = builder.secondPartnerView;
    }

    public long getFirstPartnerUserId() {
        return firstPartnerUserId;
    }

    public long getSecondPartnerUserId() {
        return secondPartnerUserId;
    }

    public CallPartnerView getFirstPartnerView() {
        return firstPartnerView;
    }

    public CallPartnerView getSecondPartnerView() {
        return secondPartnerView;
    }

    public static class Builder{
        private long firstPartnerUserId;
        private long secondPartnerUserId;

        private CallPartnerView firstPartnerView;
        private CallPartnerView secondPartnerView;

        public Builder setFirstPartnerUserId(long firstPartnerUserId) {
            this.firstPartnerUserId = firstPartnerUserId;
            return this;
        }

        public Builder setSecondPartnerUserId(long secondPartnerUserId) {
            this.secondPartnerUserId = secondPartnerUserId;
            return this;
        }

        public Builder setFirstPartnerView(CallPartnerView firstPartnerView) {
            this.firstPartnerView = firstPartnerView;
            return this;
        }

        public Builder setSecondPartnerView(CallPartnerView secondPartnerView) {
            this.secondPartnerView = secondPartnerView;
            return this;
        }

        public SwapViewsRequest build(){
            return new SwapViewsRequest(this);
        }
    }

}
