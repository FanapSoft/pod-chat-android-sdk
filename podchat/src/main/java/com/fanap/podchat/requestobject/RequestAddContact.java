package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RequestAddContact extends GeneralRequestObject {

    private String firstName;
    private String lastName;
    private String cellphoneNumber;
    private String email;
    private String username;

    RequestAddContact(@NonNull Builder builder) {
        super(builder);
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.cellphoneNumber = builder.cellphoneNumber;
        this.email = builder.email;
        this.username = builder.username;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String firstName;
        private String lastName;
        private String cellphoneNumber;
        private String email;
        private String username;


        public Builder cellphoneNumber(String cellphoneNumber) {
            this.cellphoneNumber = cellphoneNumber;
            return this;

        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

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
