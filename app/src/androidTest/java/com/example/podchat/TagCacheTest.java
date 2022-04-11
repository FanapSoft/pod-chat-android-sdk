package com.example.podchat;

import static com.example.chat.application.chatexample.ChatActivity.APP_ID;
import static com.fanap.podchat.util.ChatStateType.ChatSateConstant.CHAT_READY;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.chat.application.chatexample.BaseApplication;
import com.example.chat.application.chatexample.ChatActivity;
import com.example.chat.application.chatexample.ChatContract;
import com.example.chat.application.chatexample.ChatPresenter;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatListener;
import com.fanap.podchat.chat.tag.request_model.AddTagParticipantRequest;
import com.fanap.podchat.chat.tag.request_model.CreateTagRequest;
import com.fanap.podchat.chat.tag.request_model.DeleteTagRequest;
import com.fanap.podchat.chat.tag.request_model.EditTagRequest;
import com.fanap.podchat.chat.tag.request_model.GetTagListRequest;
import com.fanap.podchat.chat.tag.request_model.RemoveTagParticipantRequest;
import com.fanap.podchat.chat.tag.result_model.TagListResult;
import com.fanap.podchat.chat.tag.result_model.TagParticipantResult;
import com.fanap.podchat.chat.tag.result_model.TagResult;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.TagVo;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestGetContact;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;


@RunWith(AndroidJUnit4.class)
public class TagCacheTest {

    public static final boolean CACHE = true;
    private static ChatContract.presenter presenter;
    @Mock
    private static ChatContract.view view;

    @Mock
    private Activity activity;
    private static Context appContext;

    private static String serverName = "chat-server";
    private static String appId = "POD-Chat";
    private static String ssoHost = BaseApplication.getInstance().getString(R.string.ssoHost);
    private static String NAME = BaseApplication.getInstance().getString(R.string.sandbox_server_name);
    private static String socketAddress = BaseApplication.getInstance().getString(R.string.sandbox_socketAddress);
    private static String platformHost = BaseApplication.getInstance().getString(R.string.sandbox_platformHost);
    private static String fileServer = BaseApplication.getInstance().getString(R.string.sandbox_fileServer);
    private static String TOKEN = "c4d02a9aa987466f9520fdfeb0196c9d";

    @Mock
    ChatListener chatListeners;

    private ChatActivity chatActivity;

    static Chat chat;

    static final Object sync = new Object();

    @Rule
    public ActivityTestRule<ChatActivity> chatActivityRule = new ActivityTestRule<>(ChatActivity.class);

    final ArrayList<TagVo> serverTags = new ArrayList<>();
    final ArrayList<TagVo> cacheTags = new ArrayList<>();

    @BeforeClass
    public static void initial() {
        appContext = InstrumentationRegistry.getTargetContext();

        chat = Chat.init(appContext);
        Looper.prepare();
    }

    @Before
    public void createChat() {


        view = Mockito.mock(ChatContract.view.class);

        chatActivity = chatActivityRule.getActivity();
        presenter = new ChatPresenter(appContext, view, chatActivity);

        RequestConnect rc = new RequestConnect.Builder(
                socketAddress,
                APP_ID,
                serverName,
                TOKEN,
                ssoHost,
                platformHost,
                fileServer,
                "podSpaceServer")
                .build();


        chatListeners = new ChatListener() {
            @Override
            public void onChatState(String state) {
                if (state.equals(CHAT_READY)) {
                    resumeProcess();
                }
            }
        };

        chat.addListener(chatListeners);

        chat.connect(rc);

        chat.isCacheables(CACHE);

        pauseProcess();


    }

    long tagId = 0;

    @Test
    public void testAllTagActions() {
        addNewTag();
        editTag(tagId);
        addTagParticipant(tagId);
        removeTagParticipant(tagId);
        deleteTag(tagId);
    }

//    @Test
//    public void testIfTagParticipantsAreSame() {
//        populateServerTags();
//
//        populateCacheTags();
//        assert serverTags.size() > 0;
//        assert cacheTags.size() > 0;
//        assert serverTags.size() == cacheTags.size();
//
//    }

    @Test
    public void testIfTagsAreSame() {
        populateServerTags();

        populateCacheTags();
        assert serverTags.size() > 0;
        assert cacheTags.size() > 0;
        assert serverTags.size() == cacheTags.size();


        for (TagVo serverTag :
                serverTags) {
            if (serverTag == null) continue;
            System.out.println(">>>>>>>>>>> Server Tag " + serverTag.getTagId());
            TagVo tagInCache = cacheTags.stream().filter(cacheTag -> cacheTag.getTagId() == serverTag.getTagId()).findFirst().get();
            System.out.println(">>>>>>>>>>> Cache Tag " + tagInCache.getTagName());

            Assert.assertEquals(serverTag.getTagId(), tagInCache.getTagId());
            Assert.assertEquals(serverTag.getTagName(), tagInCache.getTagName());
            //    Assert.assertEquals(serverTag.getOwner(), tagInCache.getOwner());
            Assert.assertEquals(serverTag.getAllUnreadCount(), tagInCache.getAllUnreadCount());
            Assert.assertEquals(serverTag.isActive(), tagInCache.isActive());


            System.out.println(">>>>>>>>>>>TagParticipants in cache" +  tagInCache.getTagParticipants().size());
            System.out.println(">>>>>>>>>>>TagParticipants in server" +  serverTag.getTagParticipants().size());
            System.out.println(">>>>>>>>>>>Tags are same");
        }
    }

    public void addNewTag() {
        chatListeners = new ChatListener() {
            @Override
            public void onTagCreated(String content, ChatResponse<TagResult> response) {
                chat.removeListener(chatListeners);
                resumeProcess();
                tagId = response.getResult().getTag().getTagId();
            }
        };

        chat.setListener(chatListeners);

        CreateTagRequest request = new CreateTagRequest.Builder("Tag_" + System.currentTimeMillis() / 1000).build();
        chat.createTag(request);

        pauseProcess();
        System.out.println("New Tag Created: ");
    }

    public void editTag(long tagId) {
        chatListeners = new ChatListener() {
            @Override
            public void onTagEdited(String content, ChatResponse<TagResult> response) {
                chat.removeListener(chatListeners);
                resumeProcess();
            }
        };

        chat.setListener(chatListeners);

        EditTagRequest request = new EditTagRequest.Builder(tagId, "EditedTag_" + System.currentTimeMillis()).build();
        chat.editTag(request);

        pauseProcess();
        System.out.println("Tag Edited: ");
    }

    public void deleteTag(long tagId) {
        chatListeners = new ChatListener() {
            @Override
            public void onTagDeleted(String content, ChatResponse<TagResult> response) {
                chat.removeListener(chatListeners);
                resumeProcess();
            }
        };

        chat.setListener(chatListeners);

        DeleteTagRequest request = new DeleteTagRequest.Builder(tagId).build();
        chat.deleteTag(request);

        pauseProcess();
        System.out.println("Tag Deleted: ");
    }

    public void addTagParticipant(long tagId) {
        chatListeners = new ChatListener() {
            @Override
            public void OnTagParticipantAdded(String content, ChatResponse<TagParticipantResult> response) {
                chat.removeListener(chatListeners);
                resumeProcess();
            }
        };

        chat.setListener(chatListeners);

        List<Long> threadIds = new ArrayList<>();
        threadIds.add(8688l);
        threadIds.add(8730l);
        threadIds.add(8729l);
        AddTagParticipantRequest request = new AddTagParticipantRequest.Builder(tagId, threadIds).build();
        chat.addTagParticipant(request);

        pauseProcess();
        System.out.println("Tag Participant Added: ");
    }

    public void removeTagParticipant(long tagId) {
        chatListeners = new ChatListener() {
            @Override
            public void OnTagParticipantRemoved(String content, ChatResponse<TagParticipantResult> response) {
                chat.removeListener(chatListeners);
                resumeProcess();
            }
        };

        chat.setListener(chatListeners);

        List<Long> threadIds = new ArrayList<>();
        threadIds.add(8688l);
        threadIds.add(8730l);
        threadIds.add(8729l);
        RemoveTagParticipantRequest request = new RemoveTagParticipantRequest.Builder(tagId, threadIds).build();
        chat.removeTagParticipant(request);

        pauseProcess();
        System.out.println("Tag Participant Removed: ");
    }

    public void populateServerTags() {
        chatListeners = new ChatListener() {
            @Override
            public void OnTagList(String content, ChatResponse<TagListResult> response) {
                if (!response.isCache()) {
                    System.out.println("Received List: " + content);
                    serverTags.addAll(response.getResult().getTags());
                    chat.removeListener(chatListeners);
                }
                resumeProcess();
            }
        };

        chat.setListener(chatListeners);

        GetTagListRequest request = new GetTagListRequest.Builder().withNoCache().build();
        chat.getTagList(request);

        pauseProcess();
        System.out.println("Received List: " + serverTags.size());
    }

    public void populateCacheTags() {
        chatListeners = new ChatListener() {
            @Override
            public void OnTagList(String content, ChatResponse<TagListResult> response) {
                if (response.isCache()) {
                    System.out.println("Received List: " + content);
                    cacheTags.addAll(response.getResult().getTags());
                    chat.removeListener(chatListeners);
                }
                resumeProcess();
            }
        };

        chat.setListener(chatListeners);

        GetTagListRequest request = new GetTagListRequest.Builder().build();
        chat.getTagList(request);

        pauseProcess();
        System.out.println("Received List: " + cacheTags.size());
    }

    private void resumeProcess() {
        synchronized (sync) {
            sync.notify();
        }
    }

    private void pauseProcess() {
        synchronized (sync) {
            try {
                sync.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sleep(int i) {
        try {
            java.lang.Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
