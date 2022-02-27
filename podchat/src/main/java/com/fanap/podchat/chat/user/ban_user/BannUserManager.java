package com.fanap.podchat.chat.user.ban_user;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.user.profile.ResultBannedUser;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;

public class BannUserManager {


    public static ChatResponse<ResultBannedUser> prepareBannedClientResponse(ChatMessage chatMessage, String message) {
        ResultBannedUser bannedUser = App.getGson().fromJson(message, ResultBannedUser.class);

        ChatResponse<ResultBannedUser> finalResponse = new ChatResponse<>();
        finalResponse.setResult(bannedUser);
        finalResponse.setUniqueId(chatMessage.getUniqueId());
        finalResponse.setSubjectId(chatMessage.getSubjectId());

        return finalResponse;
    }

}
