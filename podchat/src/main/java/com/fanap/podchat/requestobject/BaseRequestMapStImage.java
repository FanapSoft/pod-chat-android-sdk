package com.fanap.podchat.requestobject;

abstract class BaseRequestMapStImage {
    private String type;
    private int zoom;
    private int width;
    private int height;
    private String center;

    BaseRequestMapStImage(Builder<?> builder) {
        this.center = builder.center;
        this.type = builder.type;
        this.zoom = builder.zoom;
        this.width = builder.width;
        this.height = builder.height;
    }


    static abstract class Builder<T extends Builder> {
        private String type;
        private int zoom;
        private int width;
        private int height;
        private String center;
        abstract BaseRequestMapStImage build();

        public T center(String center){
            this.center = center;
            return self();
        }
        public T type(String type){
            this.type = type;
            return self();
        }
        public T zoom(int zoom){
            this.zoom = zoom;
            return self();
        }
        public T width(int width){
            this.width = width;
            return self();
        }
        public T height(int height){
            this.height = height;
            return self();
        }

        protected abstract T self();
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

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }
}
