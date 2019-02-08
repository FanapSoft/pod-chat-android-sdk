package com.fanap.podchat.requestobject;
/**
 * Get Static Image of a GeoLocation
 *
 * @param  {string}   type           Map style (default standard-night)
 * @param  {int}      zoom           Map zoom (default 15)
 * @param  {object}   center         Lat & Lng of Map center as a JSON
 * @param  {int}      width          width of image in pixels (default 800px)
 * @param  {int}      height         height of image in pixels (default 600px)
 */
public class RequestMapStaticImage  extends BaseRequestMapStImage {


    public RequestMapStaticImage(Builder builder) {
        super(builder);
    }

    public static class Builder extends BaseRequestMapStImage.Builder<Builder>{


        public RequestMapStaticImage build() {
            return new RequestMapStaticImage(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
