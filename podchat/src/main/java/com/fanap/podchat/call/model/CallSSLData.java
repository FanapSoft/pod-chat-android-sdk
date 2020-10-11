package com.fanap.podchat.call.model;

import java.io.File;

public class CallSSLData {

    private File cert;
    private File key;
    private File client;


    public CallSSLData(File cert, File key, File client) {
        this.cert = cert;
        this.key = key;
        this.client = client;
    }

    public File getCert() {
        return cert;
    }

    public File getKey() {
        return key;
    }

    public File getClient() {
        return client;
    }

    public boolean clear(){

        try {
            return cert.delete() || key.delete() || client.delete();
        } catch (Exception e) {
            return false;
        }

    }
}
