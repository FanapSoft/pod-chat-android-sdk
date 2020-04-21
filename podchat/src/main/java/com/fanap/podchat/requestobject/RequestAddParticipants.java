package com.fanap.podchat.requestobject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestAddParticipants extends GeneralRequestObject {
    private long threadId;
    private List<Long> contactIds;
    private List<String> userNames;
    private List<Long> coreUserIds;

    private RequestAddParticipants(Builder builder) {
        super(builder);
        this.contactIds = builder.contactIds;
        this.threadId = builder.threadId;
        this.userNames = builder.userNames;
        this.coreUserIds = builder.coreUserIds;
    }

    public RequestAddParticipants() {
    }

    public static ThreadIdStep newBuilder() {

        return new Steps();
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public List<Long> getContactIds() {
        return contactIds;
    }

    public void setContactIds(List<Long> contactIds) {
        this.contactIds = contactIds;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public List<Long> getCoreUserIds() {
        return coreUserIds;
    }

    public void setCoreUserIds(List<Long> coreUserIds) {
        this.coreUserIds = coreUserIds;
    }

    public interface ThreadIdStep {
        ActionStep threadId(Long threadId);
    }

    public interface ActionStep {

        BuildStep withUsername(String username);

        BuildStep withUserNames(String... userNames);

        BuildStep withUserNames(List<String> userNames);


        BuildStep withContactId(Long contactId);

        BuildStep withContactIds(Long... contactIds);

        BuildStep withContactIds(List<Long> contactIds);


        BuildStep withCoreUserId(Long coreUserId);

        BuildStep withCoreUserIds(Long... coreUserIds);

        BuildStep withCoreUserIds(List<Long> coreUserIds);


    }


    public interface BuildStep {


        RequestAddParticipants build();

    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {

        private long threadId;
        private List<Long> contactIds;
        private List<String> userNames;
        private List<Long> coreUserIds;


        @Deprecated
        public Builder(long threadId, List<Long> contactIds) {
            this.threadId = threadId;
            this.contactIds = contactIds;
        }

        @Override
        protected Builder self() {
            return this;
        }

        public RequestAddParticipants build() {
            return new RequestAddParticipants(this);
        }
    }

    private static class Steps implements ThreadIdStep, ActionStep, BuildStep {

        private long threadId;
        private List<Long> contactIds;
        private List<String> userNames;
        private List<Long> coreUserIds;


        @Override
        public BuildStep withUsername(String username) {
            this.userNames.add(username);
            return this;
        }

        @Override
        public BuildStep withUserNames(String... userNames) {
            this.userNames = Arrays.asList(userNames);
            return this;
        }

        @Override
        public BuildStep withUserNames(List<String> userNames) {
            this.userNames = userNames;
            return this;
        }

        @Override
        public BuildStep withContactId(Long contactId) {
            contactIds = new ArrayList<>();
            this.contactIds.add(contactId);
            return this;
        }

        @Override
        public BuildStep withContactIds(Long... contactIds) {
            this.contactIds = Arrays.asList(contactIds);
            return this;
        }

        @Override
        public BuildStep withContactIds(List<Long> contactIds) {
            this.contactIds = contactIds;
            return this;
        }


        @Override
        public BuildStep withCoreUserId(Long coreUserId) {
            coreUserIds = new ArrayList<>();
            this.coreUserIds.add(coreUserId);
            return this;
        }

        @Override
        public BuildStep withCoreUserIds(Long... coreUserIds) {
            this.coreUserIds = Arrays.asList(coreUserIds);
            return this;
        }

        @Override
        public BuildStep withCoreUserIds(List<Long> coreUserIds) {
            this.coreUserIds = coreUserIds;
            return this;
        }


        @Override
        public RequestAddParticipants build() {

            RequestAddParticipants request = new RequestAddParticipants();

            request.setThreadId(threadId);

            if (contactIds != null) {
                request.setContactIds(contactIds);
            } else if (coreUserIds != null) {
                request.setCoreUserIds(coreUserIds);
            } else {
                request.setUserNames(userNames);
            }

            return request;
        }

        @Override
        public ActionStep threadId(Long threadId) {
            this.threadId = threadId;
            return this;
        }

    }


}
