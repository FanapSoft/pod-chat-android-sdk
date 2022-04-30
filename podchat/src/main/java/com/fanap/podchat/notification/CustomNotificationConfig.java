package com.fanap.podchat.notification;

import android.app.Activity;
import androidx.annotation.NonNull;


public class CustomNotificationConfig {

    private String appId;

    private String channelId;
    private String channelName;
    private String channelDescription;
    private Integer notificationImportance;
    private Activity targetActivity;
    private String targetActivityString;
    private int icon;

    private CustomNotificationConfig(Builder builder) {

        this.appId = builder.appId;
        channelId = builder.channelId;
        channelName = builder.channelName;
        channelDescription = builder.channelDescription;
        notificationImportance = builder.notificationImportance;
        targetActivity = builder.activity;
        this.icon = builder.icon;
        this.targetActivityString = builder.targetActivityString;
    }

    public String getTargetActivityString() {
        return targetActivityString;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public Integer getNotificationImportance() {
        return notificationImportance;
    }

    public Activity getTargetActivity() {
        return targetActivity;
    }

    public int getIcon() {
        return icon;
    }

    public String getAppId() {
        return appId;
    }

    public static class Builder {

        private String appId;

        private int icon;
        private String channelId;
        private String channelName;
        private String channelDescription;
        private Integer notificationImportance;
        private Activity activity;
        private String targetActivityString;



        @Deprecated
        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        @Deprecated
        public Builder(@NonNull String targetActivityString) {
            this.targetActivityString = targetActivityString;
        }

        public Builder(@NonNull String appId,@NonNull String targetActivityString) {
            this.appId = appId;
            this.targetActivityString = targetActivityString;
        }

        public Builder(@NonNull String appId,@NonNull Activity activity) {
            this.appId = appId;
            this.activity = activity;
        }

        public Builder setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder setIcon(int icon) {
            this.icon = icon;
            return this;
        }

        public Builder setChannelName(String channelName) {
            this.channelName = channelName;
            return this;
        }

        public Builder setChannelDescription(String channelDescription) {
            this.channelDescription = channelDescription;
            return this;
        }

        public Builder setNotificationImportance(Integer notificationImportance) {
            this.notificationImportance = notificationImportance;
            return this;
        }


        public CustomNotificationConfig build() {

            return new CustomNotificationConfig(this);
        }

    }


}
