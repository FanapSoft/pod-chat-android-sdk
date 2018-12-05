package com.fanap.podchat.requestobject;


public class RequestUpdateContact extends GeneralRequestObject {

    private String firstName;
    private String lastName;
    private String cellphoneNumber;
    private String email;
    private long userId;


    RequestUpdateContact(Builder builder){
        super(builder);
        this.userId = builder.userId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder>{
        private long userId;


        public Builder(long userId){
            this.userId = userId;
        }

        public RequestUpdateContact build(){
            return new RequestUpdateContact(this);
        }

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
