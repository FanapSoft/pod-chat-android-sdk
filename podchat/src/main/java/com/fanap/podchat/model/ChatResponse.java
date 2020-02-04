package com.fanap.podchat.model;

import com.fanap.podchat.chat.App;

public class ChatResponse<T> extends BaseOutPut {

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getJson(){
        return App.getGson().toJson(result);
    }
}
