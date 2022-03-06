package com.fanap.podchat.chat.contact.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddContactVO {

    @SerializedName("firstNameList")
    private List<String> firstNameList;
    @SerializedName("lastNameList")
    private List<String>  lastNameList;
    @SerializedName("cellphoneNumberList")
    private List<String>  cellphoneNumberList;
    @SerializedName("emailList")
    private List<String>  emailList;
    @SerializedName("userNameList")
    private List<String>  userNameList;
    @SerializedName("uniqueIdList")
    private List<String> uniqueIdList;

    public AddContactVO() {
    }

    public AddContactVO setFirstNameList(List<String> firstNameList) {
        this.firstNameList = firstNameList;
        return this;
    }

    public AddContactVO setLastNameList(List<String> lastNameList) {
        this.lastNameList = lastNameList;
        return this;
    }

    public AddContactVO setCellphoneNumberList(List<String> cellphoneNumberList) {
        this.cellphoneNumberList = cellphoneNumberList;
        return this;
    }

    public AddContactVO setEmailList(List<String> emailList) {
        this.emailList = emailList;
        return this;
    }

    public AddContactVO setUserNameList(List<String> userNameList) {
        this.userNameList = userNameList;
        return this;
    }

    public AddContactVO setUniqueIdList(List<String> uniqueIdList) {
        this.uniqueIdList = uniqueIdList;
        return this;
    }
}
