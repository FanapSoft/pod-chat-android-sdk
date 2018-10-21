package com.fanap.podchat.model;

import java.util.ArrayList;

public class ResultAddContacts {

    private ArrayList<Integer> id;
    private  ArrayList<String> firstName;
    private  ArrayList<String> lastName;
    private  ArrayList<String> email;
    private  ArrayList<String> cellphoneNumber;
    private  ArrayList<String> uniqueId;


    public ArrayList<Integer> getId() {
        return id;
    }

    public void setId(ArrayList<Integer> id) {
        this.id = id;
    }

    public ArrayList<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(ArrayList<String> firstName) {
        this.firstName = firstName;
    }

    public ArrayList<String> getLastName() {
        return lastName;
    }

    public void setLastName(ArrayList<String> lastName) {
        this.lastName = lastName;
    }

    public ArrayList<String> getEmail() {
        return email;
    }

    public void setEmail(ArrayList<String> email) {
        this.email = email;
    }

    public ArrayList<String> getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(ArrayList<String> cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public ArrayList<String> getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(ArrayList<String> uniqueId) {
        this.uniqueId = uniqueId;
    }
}

