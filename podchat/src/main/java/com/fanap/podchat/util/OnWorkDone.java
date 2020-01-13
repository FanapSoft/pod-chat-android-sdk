package com.fanap.podchat.util;


import android.support.annotation.Nullable;

@FunctionalInterface
public interface OnWorkDone <T> {

    void onWorkDone(@Nullable T t);
}

