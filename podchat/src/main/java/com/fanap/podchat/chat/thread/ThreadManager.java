package com.fanap.podchat.chat.thread;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ThreadManager {


    public interface ILastMessageChanged{

        void onThreadExistInCache(Thread thread);

        void threadNotFoundInCache();

    }

    public interface IThreadInfoCompleter {

        void onThreadInfoReceived(ChatMessage chatMessage);

    }
}
