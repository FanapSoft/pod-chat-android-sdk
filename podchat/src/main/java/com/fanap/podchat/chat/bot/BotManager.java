package com.fanap.podchat.chat.bot;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.bot.model.BotVo;
import com.fanap.podchat.chat.bot.request_model.CreateBotRequest;
import com.fanap.podchat.chat.bot.request_model.DefineBotCommandRequest;
import com.fanap.podchat.chat.bot.request_model.GetUserBotsRequest;
import com.fanap.podchat.chat.bot.request_model.StartAndStopBotRequest;
import com.fanap.podchat.chat.bot.result_model.CreateBotResult;
import com.fanap.podchat.chat.bot.result_model.DefineBotCommandResult;
import com.fanap.podchat.chat.bot.result_model.GetUserBotsResult;
import com.fanap.podchat.chat.bot.result_model.StartStopBotResult;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.PodChatException;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class BotManager {

    public static String createCreateBotRequest(CreateBotRequest request,
                                                String uniqueId) throws PodChatException {


        validateBotName(request.getBotName());

        JsonObject content = new JsonObject();

        content.addProperty("botName", request.getBotName());


        AsyncMessage message = new AsyncMessage();
        message.setContent(request.getBotName());
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.CREATE_BOT);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);

        return App.getGson().toJson(message);


    }


    public static String createDefineBotCommandRequest(DefineBotCommandRequest request,
                                                       String uniqueId) throws PodChatException {


        validateCommands(request.getCommandList());


        AsyncMessage message = new AsyncMessage();
        message.setContent(App.getGson().toJson(request));
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.DEFINE_BOT_COMMAND);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);

        return App.getGson().toJson(message);

    }


    public static String createStartBotRequest(StartAndStopBotRequest request, String uniqueId) throws PodChatException {


        validateThreadId(request.getThreadId());

        validateBotName(request.getBotName());

        JsonObject content = new JsonObject();

        content.addProperty("botName", request.getBotName());


        AsyncMessage message = new AsyncMessage();
        message.setContent(content.toString());
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.START_BOT);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setSubjectId(request.getThreadId());
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);

        return App.getGson().toJson(message);


    }


    public static String createStopBotRequest(StartAndStopBotRequest request, String uniqueId) throws PodChatException {

        validateThreadId(request.getThreadId());

        validateBotName(request.getBotName());

        JsonObject content = new JsonObject();

        content.addProperty("botName", request.getBotName());


        AsyncMessage message = new AsyncMessage();
        message.setContent(content.toString());
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.STOP_BOT);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setSubjectId(request.getThreadId());
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);

        return App.getGson().toJson(message);


    }


    public static String createGetUserBotsRequest(GetUserBotsRequest request, String uniqueId) throws PodChatException {

        AsyncMessage message = new AsyncMessage();
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.GET_USER_BOTS);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);

        return App.getGson().toJson(message);


    }

    private static void validateBotName(String botName) throws PodChatException {

        if (!botName.endsWith("BOT") || Util.isNullOrEmpty(botName))
            throw new PodChatException(ChatConstant.ERROR_INVALID_BOT_NAME, ChatConstant.ERROR_CODE_INVALID_BOT_NAME);

    }

    private static void validateCommands(List<String> commandList) throws PodChatException {

        if (Util.isNullOrEmpty(commandList))
            throw new PodChatException(ChatConstant.ERROR_EMPTY_BOT_COMMANDS, ChatConstant.ERROR_CODE_INVALID_BOT_COMMAND);


        for (String command :
                commandList) {

            if (Util.isNullOrEmpty(command) || command.replace(" ","").equals("/"))
                throw new PodChatException(ChatConstant.ERROR_BLANK_BOT_COMMAND, ChatConstant.ERROR_CODE_INVALID_BOT_COMMAND);

            if (!command.startsWith("/"))
                throw new PodChatException(ChatConstant.ERROR_INVALID_BOT_COMMAND_FORMAT_1, ChatConstant.ERROR_CODE_INVALID_BOT_COMMAND);

            if (command.contains("@"))
                throw new PodChatException(ChatConstant.ERROR_INVALID_BOT_COMMAND_FORMAT_2, ChatConstant.ERROR_CODE_INVALID_BOT_COMMAND);
        }


    }

    private static void validateThreadId(long threadId) throws PodChatException {

        if (threadId <= 0)
            throw new PodChatException(ChatConstant.ERROR_INVALID_THREAD_ID, ChatConstant.ERROR_CODE_INVALID_THREAD_ID);

    }

    public static ChatResponse<CreateBotResult> handleOnBotCreated(ChatMessage chatMessage) {

        ChatResponse<CreateBotResult> response = new ChatResponse<>();

        CreateBotResult result = App.getGson().fromJson(chatMessage.getContent(), CreateBotResult.class);

        response.setResult(result);

        response.setUniqueId(chatMessage.getUniqueId());


        return response;
    }

    public static ChatResponse<DefineBotCommandResult> handleOnBotCommandDefined(ChatMessage chatMessage) {

        ChatResponse<DefineBotCommandResult> response = new ChatResponse<>();

        DefineBotCommandResult result = App.getGson().fromJson(chatMessage.getContent(), DefineBotCommandResult.class);

        response.setResult(result);

        response.setUniqueId(chatMessage.getUniqueId());

        return response;

    }

    public static ChatResponse<StartStopBotResult> handleOnBotStartedOrStopped(ChatMessage chatMessage) {

        ChatResponse<StartStopBotResult> response = new ChatResponse<>();

        StartStopBotResult result = new StartStopBotResult(chatMessage.getContent());

        response.setResult(result);

        response.setUniqueId(chatMessage.getUniqueId());

        response.setSubjectId(chatMessage.getSubjectId());

        return response;

    }
    public static ChatResponse<GetUserBotsResult> handleOnUserBots(ChatMessage chatMessage) {

        ChatResponse<GetUserBotsResult> response = new ChatResponse<>();

        GetUserBotsResult  result = new GetUserBotsResult(App.getGson().fromJson(chatMessage.getContent(), new TypeToken<List<BotVo>>() {
        }.getType()));

        response.setResult(result);

        response.setUniqueId(chatMessage.getUniqueId());

        response.setSubjectId(chatMessage.getSubjectId());

        return response;

    }
}
