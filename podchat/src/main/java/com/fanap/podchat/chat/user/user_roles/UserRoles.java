package com.fanap.podchat.chat.user.user_roles;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.user.user_roles.model.CacheUserRoles;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.util.ChatMessageType;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class UserRoles {


    public static String getUserRoles(RequestGetUserRoles request, String uniqueId) {


        long threadId = request.getThreadId();
        AsyncMessage message = new AsyncMessage();
        message.setSubjectId(threadId);
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.GET_USER_ROLES);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);

        return App.getGson().toJson(message);

    }


    public static ChatResponse<ResultCurrentUserRoles> handleOnGetUserRoles(ChatMessage chatMessage) {


        String jsonContent = chatMessage.getContent();

        ArrayList<String> roles = App.getGson().fromJson(jsonContent, new TypeToken<ArrayList<String>>() {
        }.getType());
        ResultCurrentUserRoles result = new ResultCurrentUserRoles();
        result.setRoles(roles);
        ChatResponse<ResultCurrentUserRoles> response = new ChatResponse<>();
        response.setResult(result);
        response.setUniqueId(chatMessage.getUniqueId());
        response.setSubjectId(chatMessage.getSubjectId());

        return response;

    }

    public static ChatResponse<ResultCurrentUserRoles> handleOnGetUserRolesFromCache(String uniqueId, RequestGetUserRoles request, CacheUserRoles cacheUserRole) {


        ArrayList<String> roles  = new ArrayList<>(cacheUserRole.getRole());


        ResultCurrentUserRoles result = new ResultCurrentUserRoles();
        result.setRoles(roles);
        ChatResponse<ResultCurrentUserRoles> response = new ChatResponse<>();
        response.setResult(result);
        response.setUniqueId(uniqueId);
        response.setSubjectId(request.getThreadId());
        response.setCache(true);

        return response;

    }


}
