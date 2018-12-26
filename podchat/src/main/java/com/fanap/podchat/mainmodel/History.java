package com.fanap.podchat.mainmodel;

public class History  {
    private long id;
    private long firstMessageId;
    private long lastMessageId;
    private long offset;
    private long count;
    private String order;
    private String query;
    private NosqlSearchMetadataCriteria metadataCriteria;

    public History(Builder builder){
        this.count = builder.count;
        this.offset = builder.offset;
        this.order = builder.order;
        this.firstMessageId = builder.firstMessageId;
        this.lastMessageId = builder.lastMessageId;
        this.id = builder.id;
        this.query = builder.query;
        this.metadataCriteria = builder.metadataCriteria;
    }


    public static class Builder{
        private long offset;
        private long count;
        private String order;
        private long firstMessageId;
        private long lastMessageId;
        private NosqlSearchMetadataCriteria metadataCriteria;
        private long id;
        private String query;

        public Builder id(long id){
            this.id = id;
            return this;
        }

        public Builder id(String query){
            this.query = query;
            return this;
        }

        public Builder metadataCriteria(NosqlSearchMetadataCriteria metadataCriteria){
            this.metadataCriteria = metadataCriteria;
            return this;
        }

        public Builder offset(long offset){
           this.offset = offset;
           return this;
        }

        public Builder count(long count){
            this.count = count;
            return this;
        }

        public Builder order(String order){
            this.order = order;
            return this;
        }

        public Builder firstMessageId(long firstMessageId){
            this.firstMessageId = firstMessageId;
            return this;
        }

        public Builder lastMessageId (long lastMessageId){
            this.lastMessageId = lastMessageId;
            return this;
        }

        public History build(){
            return new History(this);
        }
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public long getFirstMessageId() {
        return firstMessageId;
    }

    public void setFirstMessageId(long firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public NosqlSearchMetadataCriteria getMetadataCriteria() {
        return metadataCriteria;
    }

    public void setMetadataCriteria(NosqlSearchMetadataCriteria metadataCriteria) {
        this.metadataCriteria = metadataCriteria;
    }
}