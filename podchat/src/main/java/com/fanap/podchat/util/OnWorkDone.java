package com.fanap.podchat.util;


import android.support.annotation.Nullable;

import java.util.List;

@FunctionalInterface
public interface OnWorkDone <T,Z> {

    abstract void onWorkDone(@Nullable T t);

    default void onWorkDone(@Nullable T t, List<Z> z) {}
}

