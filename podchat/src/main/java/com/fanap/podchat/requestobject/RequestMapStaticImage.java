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
public class RequestMapStaticImage {
    private String type;
    private int zoom;
    private int width;
    private int height;
    private String center;

    RequestMapStaticImage (Builder builder){
        this.type = builder.type;
        this.zoom = builder.zoom;
        this.width = builder.width;
        this.height = builder.height;
        this.center = builder.center;
    }

    public static class Builder{
        private String type;
        private int zoom;
        private int width;
        private int height;
        private String center;

        public Builder(String center){
            this.center = center;
        }
        public Builder type(String type){
            this.type = type;
            return this;
        }
        public Builder zoom(int zoom){
            this.zoom = zoom;
            return this;
        }
        public Builder width(int width){
            this.width = width;
            return this;
        }
        public Builder height(int height){
            this.height = height;
            return this;
        }

        public RequestMapStaticImage build(){
            return new RequestMapStaticImage(this);
        }

    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
