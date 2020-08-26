package com.fanap.podchat.requestobject;


import android.support.annotation.NonNull;

public class UpdateContactsRequest extends GeneralRequestObject {

    private String firstName;
    private String lastName;
    private String cellphoneNumber;
    private String email;
    private long userId;

    private UpdateContactsRequest(@NonNull Builder builder){
        super(builder);
        this.userId = builder.userId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.cellphoneNumber = builder.cellphoneNumber;
        this.email = builder.email;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder>{
        private long userId;
        private String firstName;
        private String lastName;
        private String cellphoneNumber;
        private String email;

        public Builder(long userId){
            this.userId = userId;
        }

        public Builder firstName(String firstName){
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName){
            this.lastName = lastName;
            return this;
        }

        public Builder cellphoneNumber(String cellphoneNumber){
            this.cellphoneNumber = cellphoneNumber;
            return this;
        }

        public Builder email(String email){
            this.email = email;
            return this;
        }

        @NonNull
        public UpdateContactsRequest build(){
            return new UpdateContactsRequest(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }



}
