package com.fanap.podchat.call.request_model;

import com.fanap.podcall.view.CallPartnerView;

public class ReplaceViewsRequest {


    private final long partnerToRemoveVideoUserId;
    private final long partnerToAddVideoUserId;
    private final CallPartnerView view;

    ReplaceViewsRequest(Builder builder) {
        this.partnerToRemoveVideoUserId = builder.partnerToRemoveVideoUserId;
        this.partnerToAddVideoUserId = builder.partnerToAddVideoUserId;
        this.view = builder.view;
    }

    public long getPartnerToRemoveVideoUserId() {
        return partnerToRemoveVideoUserId;
    }

    public long getPartnerToAddVideoUserId() {
        return partnerToAddVideoUserId;
    }

    public CallPartnerView getView() {
        return view;
    }

    public static class Builder{
        private long partnerToRemoveVideoUserId;
        private long partnerToAddVideoUserId;
        private CallPartnerView view;


        public Builder setPartnerToRemoveVideoUserId(long partnerToRemoveVideoUserId) {
            this.partnerToRemoveVideoUserId = partnerToRemoveVideoUserId;
            return this;
        }

        public Builder setPartnerToAddVideoUserId(long partnerToAddVideoUserId) {
            this.partnerToAddVideoUserId = partnerToAddVideoUserId;
            return this;
        }

        public Builder setView(CallPartnerView view) {
            this.view = view;
            return this;
        }

        public ReplaceViewsRequest build(){
            return new ReplaceViewsRequest(this);
        }
    }

}
