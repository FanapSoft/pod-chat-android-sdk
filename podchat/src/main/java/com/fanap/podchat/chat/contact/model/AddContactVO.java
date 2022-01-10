package com.fanap.podchat.chat.contact.model;
import com.google.gson.annotations.SerializedName;

public class AddContactVO {
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("cellphoneNumber")
    private String cellphoneNumber;
    @SerializedName("email")
    private String email;
    @SerializedName("userName")
    private String userName;

    public AddContactVO() {
    }

    public AddContactVO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public AddContactVO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public AddContactVO setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
        return this;
    }

    public AddContactVO setEmail(String email) {
        this.email = email;
        return this;
    }

    public AddContactVO setUserName(String userName) {
        this.userName = userName;
        return this;
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

    public String getUserName() {
        return userName;
    }
}
