package com.fanap.podchat.call.recording;

import com.fanap.podchat.util.Util;

public class CallRecordTracer {

    public static String requestUniqueId;

    public static String ongoingRecordUniqueId;


    public static boolean isRecordedFileMessage(String uniqueId) {
        return (
                (
                        Util.isNotNullOrEmpty(requestUniqueId)
                                &&
                                requestUniqueId.equals(uniqueId)
                )
                        ||
                        (
                                Util.isNotNullOrEmpty(ongoingRecordUniqueId)
                                        &&
                                        ongoingRecordUniqueId.equals(uniqueId)
                        )
        );
    }
}
