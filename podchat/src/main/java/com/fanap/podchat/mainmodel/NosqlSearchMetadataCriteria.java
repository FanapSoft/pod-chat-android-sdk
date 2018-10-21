package com.fanap.podchat.mainmodel;

import java.util.List;

public class NosqlSearchMetadataCriteria {
    // is :  can be used for string and number
    private String field;
    private String is;
    private String has;
    private String gt;
    private String gte;
    private String lt;
    private String lte;
    private List<NosqlSearchMetadataCriteria> and;
    private List<NosqlSearchMetadataCriteria> or;
    private List<NosqlSearchMetadataCriteria> not;

    public NosqlSearchMetadataCriteria(Builder builderSearchMetadata){
        this.field = builderSearchMetadata.field;
        this.is = builderSearchMetadata.is;
        this.gt = builderSearchMetadata.gt;
        this.gte = builderSearchMetadata.gte;
        this.has = builderSearchMetadata.has;
        this.lt = builderSearchMetadata.lt;
        this.lte = builderSearchMetadata.lte;
        this.and = builderSearchMetadata.and;
        this.not = builderSearchMetadata.not;
        this.or = builderSearchMetadata.or;

    }
    public static class Builder {
        private final String field;
        private String is;
        private String has;
        private String gt;
        private String gte;
        private String lt;
        private String lte;
        private List<NosqlSearchMetadataCriteria> and;
        private List<NosqlSearchMetadataCriteria> or;
        private List<NosqlSearchMetadataCriteria> not;

        public Builder(String field) {
            this.field = field;
        }

        public Builder has(String has) {
            this.has = has;
            return this;
        }public Builder is(String is) {
            this.is = is;
            return this;
        }
        public Builder gt(String gt) {
            this.gt = gt;
            return this;
        }
        public Builder gte(String gte) {
            this.gte = gte;
            return this;
        }
        public Builder lt(String lt) {
            this.lt = lt;
            return this;
        }
        public Builder lte(String lte) {
            this.lte = lte;
            return this;
        }
        public Builder and(List<NosqlSearchMetadataCriteria> and) {
            this.and = and;
            return this;
        }
        public Builder or(List<NosqlSearchMetadataCriteria> or) {
            this.or = or;
            return this;
        }
        public Builder not(List<NosqlSearchMetadataCriteria> not) {
            this.not = not;
            return this;
        }
        public NosqlSearchMetadataCriteria build(){
            return new NosqlSearchMetadataCriteria(this);
        }
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getIs() {
        return is;
    }

    public void setIs(String is) {
        this.is = is;
    }

    public String getHas() {
        return has;
    }

    public void setHas(String has) {
        this.has = has;
    }

    public String getGt() {
        return gt;
    }

    public void setGt(String gt) {
        this.gt = gt;
    }

    public String getLt() {
        return lt;
    }

    public void setLt(String lt) {
        this.lt = lt;
    }

    public String getLte() {
        return lte;
    }

    public void setLte(String lte) {
        this.lte = lte;
    }

    public List<NosqlSearchMetadataCriteria> getAnd() {
        return and;
    }

    public void setAnd(List<NosqlSearchMetadataCriteria> and) {
        this.and = and;
    }

    public List<NosqlSearchMetadataCriteria> getOr() {
        return or;
    }

    public void setOr(List<NosqlSearchMetadataCriteria> or) {
        this.or = or;
    }

    public List<NosqlSearchMetadataCriteria> getNot() {
        return not;
    }

    public void setNot(List<NosqlSearchMetadataCriteria> not) {
        this.not = not;
    }

    public String getGte() {
        return gte;
    }

    public void setGte(String gte) {
        this.gte = gte;
    }
}
