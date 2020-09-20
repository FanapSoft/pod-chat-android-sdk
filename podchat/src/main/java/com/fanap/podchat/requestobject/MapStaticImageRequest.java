package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

/**
 * Get Static Image of a GeoLocation
 *
 *  {string}   messageType           Map style (default standard-night)
 *   {int}      zoom           Map zoom (default 15)
 *   {object}   center         Lat & Lng of Map center as a JSON
 *   {int}      width          width of image in pixels (default 800px)
 *   {int}      height         height of image in pixels (default 600px)
 */
public class MapStaticImageRequest extends BaseRequestMapStImage {


    public MapStaticImageRequest(@NonNull Builder builder) {
        super(builder);
    }

    public static class Builder extends BaseRequestMapStImage.Builder<Builder>{


        @NonNull
        public MapStaticImageRequest build() {
            return new MapStaticImageRequest(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }
}
