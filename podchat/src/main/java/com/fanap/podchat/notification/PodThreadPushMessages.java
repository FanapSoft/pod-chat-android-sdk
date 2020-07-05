package com.fanap.podchat.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PodThreadPushMessages {

    private static Map<String, ArrayList<PodPushMessage>> notificationsGroup = new HashMap<>();

    public PodThreadPushMessages() {
    }

    public static void clearMessages() {

        notificationsGroup.clear();

    }

    public static void addNewMessage(PodPushMessage pushMessage) {

        String threadId = pushMessage.getThreadIdAsStringKey();

        ArrayList<PodPushMessage> dataList;

        if (notificationsGroup.containsKey(threadId)) {
            dataList = notificationsGroup.get(threadId);
        } else {
            dataList = new ArrayList<>();
        }

        if (dataList != null) {
            dataList.add(pushMessage);
        }
        notificationsGroup.put(threadId, dataList);

    }

    public static int getUnreadPushMessagesCount() {


        int count = 0;

        for (String key :
                notificationsGroup.keySet()) {

            try {
                count += notificationsGroup.get(key) != null ? notificationsGroup.get(key).size() : 0;
            } catch (Exception ignored) {
            }

        }
        return count;

    }

    public static int getUnreadThreadCount() {
        return notificationsGroup.keySet().size();
    }

    public static Map<String, ArrayList<PodPushMessage>> getNotificationsGroup() {

        return notificationsGroup;

    }

    public static String getLastUnreadThreadName() {


        if (notificationsGroup.size() > 0) {
            for (String key :
                    notificationsGroup.keySet()) {

                return notificationsGroup.get(key).get(notificationsGroup.get(key).size() - 1).getThreadName();
            }
        }
        return "-";
    }

    public static String getLastUnreadThreadSenderUserName() {


        if (notificationsGroup.size() > 0) {
            for (String key :
                    notificationsGroup.keySet()) {

                return notificationsGroup.get(key).get(notificationsGroup.get(key).size() - 1).getMessageSenderUserName();
            }
        }
        return "-";
    }

    public static String getLastUnreadThreadMessage() {


        if (notificationsGroup.size() > 0) {
            for (String key :
                    notificationsGroup.keySet()) {

                return notificationsGroup.get(key).get(notificationsGroup.get(key).size() - 1).getText();
            }
        }
        return "-";
    }

    public static long getLastUnreadThreadId() {


        if (notificationsGroup.size() > 0) {
            for (String key :
                    notificationsGroup.keySet()) {

                return notificationsGroup.get(key).get(notificationsGroup.get(key).size() - 1).getThreadId();
            }
        }
        return 0;
    }

    public static long getLastUnreadMessageId() {


        if (notificationsGroup.size() > 0) {
            for (String key :
                    notificationsGroup.keySet()) {

                return notificationsGroup.get(key).get(notificationsGroup.get(key).size() - 1).getMessageId();
            }
        }
        return 0;
    }

    public static Set<String> getUnreadThreads() {

        return notificationsGroup.keySet();

    }

    public static ArrayList<PodPushMessage> getNotificationsOfThread(long threadId) {

        return notificationsGroup.get(String.valueOf(threadId));

    }

    public static ArrayList<PodPushMessage> getNotificationsOfThread(String threadId) {

        return notificationsGroup.get(threadId);

    }

    public static boolean markThreadAsRead(long threadId) {

        if (notificationsGroup.containsKey(String.valueOf(threadId))
                && notificationsGroup.get(String.valueOf(threadId)) != null) {
            notificationsGroup.remove(String.valueOf(threadId));
            return true;
        }

        return false;

    }


}
