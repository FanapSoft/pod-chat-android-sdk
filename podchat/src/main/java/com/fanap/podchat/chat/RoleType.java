package com.fanap.podchat.chat;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RoleType {


    @StringDef({
            Constants.ADD_NEW_USER,
            Constants.ADD_RULE_TO_USER,
            Constants.CHANGE_THREAD_INFO,
            Constants.DELETE_MESSAGE_OF_OTHERS,
            Constants.EDIT_MESSAGE_OF_OTHERS,
            Constants.EDIT_THREAD,
            Constants.POST_CHANNEL_MESSAGE,
            Constants.READ_THREAD,
            Constants.REMOVE_ROLE_FROM_USER,
            Constants.REMOVE_USER,
            Constants.THREAD_ADMIN})

    @Retention(RetentionPolicy.SOURCE)
    public @interface Constants{
        String CHANGE_THREAD_INFO = "change_thread_info";
        String POST_CHANNEL_MESSAGE = "post_channel_message";
        String EDIT_MESSAGE_OF_OTHERS = "edit_message_of_others";
        String DELETE_MESSAGE_OF_OTHERS = "delete_message_of_others";
        String ADD_NEW_USER = "add_new_user";
        String REMOVE_USER = "remove_user";
        String ADD_RULE_TO_USER = "add_rule_to_user";
        String REMOVE_ROLE_FROM_USER = "remove_role_from_user";
        String READ_THREAD = "read_thread";
        String EDIT_THREAD = "edit_thread";
        String THREAD_ADMIN = "thread_admin";
    }
}
