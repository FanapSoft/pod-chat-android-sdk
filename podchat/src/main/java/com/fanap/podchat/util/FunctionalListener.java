package com.fanap.podchat.util;


import android.support.annotation.Nullable;

import java.util.List;

@FunctionalInterface
public interface FunctionalListener<T,Z> {

    void onWorkDone(T t, Z z);
}

