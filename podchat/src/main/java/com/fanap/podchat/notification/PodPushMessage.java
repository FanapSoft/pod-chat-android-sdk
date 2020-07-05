package com.fanap.podchat.notification;

import com.fanap.podchat.util.TextMessageType;
import com.fanap.podchat.util.Util;

import java.util.Date;
import java.util.Map;

import static com.fanap.podchat.util.TextMessageType.Constants.FILE;
import static com.fanap.podchat.util.TextMessageType.Constants.LINK;
import static com.fanap.podchat.util.TextMessageType.Constants.PICTURE;
import static com.fanap.podchat.util.TextMessageType.Constants.POD_SPACE_FILE;
import static com.fanap.podchat.util.TextMessageType.Constants.POD_SPACE_PICTURE;
import static com.fanap.podchat.util.TextMessageType.Constants.POD_SPACE_SOUND;
import static com.fanap.podchat.util.TextMessageType.Constants.POD_SPACE_VIDEO;
import static com.fanap.podchat.util.TextMessageType.Constants.POD_SPACE_VOICE;
import static com.fanap.podchat.util.TextMessageType.Constants.SOUND;
import static com.fanap.podchat.util.TextMessageType.Constants.TEXT;
import static com.fanap.podchat.util.TextMessageType.Constants.VIDEO;
import static com.fanap.podchat.util.TextMessageType.Constants.VOICE;

public class PodPushMessage {


    private static final String THREAD_NAME = "threadName";
    private static final String MESSAGE_SENDER_NAME = "MessageSenderName";
    private static final String SENDER_IMAGE = "senderImage";
    private static final String TEXT = "text";
    private static final String MESSAGE_TYPE = "messageType";
    private static final String IS_GROUP = "isGroup";
    private static final String MESSAGE_SENDER_USER_NAME = "MessageSenderUserName";
    private static final String MESSAGE_ID = "messageId";
    private static final String THREAD_ID = "threadId";
    private boolean isGroup;
    private String threadName;
    private String messageSenderUserName;
    private String messageSenderName;
    private String text;
    private String profileImage;
    private long messageId;
    private long threadId;


    PodPushMessage createFromMapData(Map<String, String> notificationData) {


        String threadName = notificationData.get(THREAD_NAME);
        String senderName = notificationData.get(MESSAGE_SENDER_NAME);
        String profileUrl = notificationData.get(SENDER_IMAGE);
        String text = getNotificationText(notificationData.get(TEXT), notificationData.get(MESSAGE_TYPE));
        String isGroup = notificationData.get(IS_GROUP) != null ? notificationData.get(IS_GROUP) : "false";
        String messageSenderUserName = notificationData.get(MESSAGE_SENDER_USER_NAME);
        String messageId = notificationData.get(MESSAGE_ID);
        String threadId = notificationData.get(THREAD_ID);


        this.threadName = threadName;
        this.messageSenderUserName = messageSenderUserName;
        this.messageSenderName = senderName;
        this.text = text;
        this.profileImage = profileUrl;

        try {
            this.messageId = Long.parseLong(messageId != null ? messageId : "0");
            this.threadId = Long.parseLong(threadId != null ? threadId : "0");
            this.isGroup = (isGroup != null && isGroup.equals("true"));
            if (!this.isGroup) {
                setThreadName(senderName);
            }
            if (Util.isNullOrEmpty(messageSenderUserName)) {
                setMessageSenderUserName(senderName);
            }
        } catch (NumberFormatException ignored) {
        }


        return this;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getMessageSenderUserName() {
        return messageSenderUserName;
    }

    public void setMessageSenderUserName(String messageSenderUserName) {
        this.messageSenderUserName = messageSenderUserName;
    }

    public String getMessageSenderName() {
        return messageSenderName;
    }

    public void setMessageSenderName(String messageSenderName) {
        this.messageSenderName = messageSenderName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getTitle() {

        if (isGroup) {
            return threadName + ": " + messageSenderName;
        } else return messageSenderName;

    }

    public String getTitleWithUnreadCount(int unreadCount) {

        String counter = "پیام جدید";
//        String counter = unreadCount > 1 ? "messages" : "message";
        if (isGroup) {
            return threadName + "(" + unreadCount + " " + counter + ")" + ": " + messageSenderName;
        } else
            return messageSenderName + "(" + unreadCount + " " + counter + ")" + ": ";

    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getMessageIdAsStringKey() {
        return String.valueOf(messageId);
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getThreadIdAsStringKey() {
        return String.valueOf(threadId);
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    private static String getNotificationText(String text, String messageType) {

        try {

            if (Util.isNullOrEmpty(messageType) || messageType.equals("null"))
                return text;

            int textMessageType = Integer.parseInt(messageType);

            switch (textMessageType) {

                case TextMessageType.Constants.TEXT: {
                    return text;
                }

                case POD_SPACE_VOICE:
                case VOICE: {
                    return "صدا فرستاده شد";
                }

                case POD_SPACE_PICTURE:
                case PICTURE: {
                    String emoji = new String(Character.toChars(0x1F306));
                    return emoji + " تصویری فرستاده شد";
                }

                case POD_SPACE_FILE:
                case FILE: {
                    return "فایلی فرستاده شد";
                }

                case POD_SPACE_VIDEO:
                case VIDEO: {
                    return "ویدئویی فرستاده شد";
                }
                case POD_SPACE_SOUND:
                case SOUND: {
                    return "فایل صوتی فرستاده شد";
                }

                case LINK: {
                    return "لینکی فرستاده شد";
                }

                default: {
                    return "پیام جدیدی ارسال شد";
                }

            }


        } catch (NumberFormatException e) {
            return "پیام جدیدی ارسال شد";
        }


    }

    public long getTime() {
        return new Date().getTime();
    }

    public String getThreadImage() {
        return profileImage;
    }
}
