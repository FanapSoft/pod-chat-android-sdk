package com.fanap.podchat.chat.requestobject;

import android.support.annotation.NonNull;

public class RequestAddContact extends GeneralRequestObject {

    private String firstName;
    private String lastName;
    private String cellphoneNumber;
    private String email;

    RequestAddContact(@NonNull Builder builder) {
        super(builder);
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.cellphoneNumber = builder.cellphoneNumber;
        this.email = builder.email;
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

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String firstName;
        private String lastName;
        private String cellphoneNumber;
        private String email;

        @NonNull
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        @NonNull
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        @NonNull
        public Builder cellphoneNumber(String cellphoneNumber) {
            this.cellphoneNumber = cellphoneNumber;
            return this;
        }

        @NonNull
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        @NonNull
        public RequestAddContact build(){
            return new RequestAddContact(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }
}
