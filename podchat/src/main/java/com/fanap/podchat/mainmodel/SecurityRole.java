package com.fanap.podchat.mainmodel;

import java.util.List;
/*roleTypes*/
//    CHANGE_THREAD_INFO("change_thread_info"),
//    POST_CHANNEL_MESSAGE("post_channel_message"),
//    EDIT_MESSAGE_OF_OTHERS("edit_message_of_others"),
//    DELETE_MESSAGE_OF_OTHERS("delete_message_of_others"),
//    ADD_NEW_USER("add_new_user"),
//    REMOVE_USER("remove_user"),
//    SET_ROLE_TO_USER("add_rule_to_user"),
//    REMOVE_ROLE_FROM_USER("remove_role_from_user"),
//    READ_THREAD("read_thread"),
//    EDIT_THREAD("edit_thread"),
//    THREAD_ADMIN("thread_admin");

public class SecurityRole {
    private String ssoUserId;
    private long aclResourceId;
    private List roleTypes;

    public String getSsoUserId() {
        return ssoUserId;
    }

    public void setSsoUserId(String ssoUserId) {
        this.ssoUserId = ssoUserId;
    }

    public long getAclResourceId() {
        return aclResourceId;
    }

    public void setAclResourceId(long aclResourceId) {
        this.aclResourceId = aclResourceId;
    }

    public List getRoleTypes() {
        return roleTypes;
    }

    public void setRoleTypes(List roleTypes) {
        this.roleTypes = roleTypes;
    }
}
