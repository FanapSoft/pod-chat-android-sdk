package com.fanap.podchat.util;

@FunctionalInterface
public interface FunctionalListener<T extends Object,Z extends Object> {

    void onWorkDone(T t, Z z);
}

