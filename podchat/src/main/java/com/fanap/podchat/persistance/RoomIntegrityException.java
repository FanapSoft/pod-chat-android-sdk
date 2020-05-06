package com.fanap.podchat.persistance;

import android.support.annotation.Nullable;

public class RoomIntegrityException extends Exception {
    @Nullable
    @Override
    public String getMessage() {
        return "Room Integrity Exception";
    }
}
