package com.example.podchat;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.fanap.podchat.cachemodel.CacheMessageVO;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.util.Callback;
import com.fanap.podchat.util.FilePick;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DbTest {
    private Context appContext;

    //    @Inject
    public MessageDatabaseHelper messageDatabaseHelper;

    @Before
    public void setUp() {
        Looper.prepare();
        appContext = InstrumentationRegistry.getTargetContext();
//        Chat.init(appContext);
        MockitoAnnotations.initMocks(this);
//        DaggerMessageComponent.builder()
//                .appDatabaseModule(new AppDatabaseModule(appContext))
//                .appModule(new AppModule(appContext))
//                .build()
//                .inject(this);
    }

    @Test
    public void getPhoneContact() {
        String name;
        String phoneNumber;
        String lastName;
        int version;
        Cursor cursor = appContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor == null) throw new AssertionError();
        ArrayList<Contact> storeContacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            lastName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
            version = cursor.getInt(cursor.getColumnIndex(ContactsContract.RawContacts.VERSION));
//            creationDate = Long.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP)));
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact contact = new Contact();
            char ch1 = phoneNumber.charAt(0);
            if (!Character.toString(ch1).equals("+")) {
                contact.setCellphoneNumber(phoneNumber.replaceAll(Character.toString(ch1), "+98"));
            }
            contact.setCellphoneNumber(phoneNumber.replaceAll(" ", ""));
            contact.setFirstName(name.replaceAll(" ", ""));
            contact.setLastName(lastName.replaceAll(" ", ""));
            storeContacts.add(contact);
        }
        cursor.close();

    }

    @Test
    public void filePath() {
        String uri = "/storage/emulated/0/Download/tumblr_pk09kr69kl1vnqkdho1_1280.png";
        String path = FilePick.getSmartFilePath(appContext, Uri.parse(uri));
        File file = new File(path);

        if (file.exists()) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void updateMessagesWithMessageId() {
        long threadId = 2;
        Callback callback = new Callback();
        List<MessageVO> messageVOS = new ArrayList<>();
        callback.setMessageId(6181);
        callback.setOrder("asc");
        messageDatabaseHelper.updateGetHistoryResponse(callback, messageVOS, threadId, null);
    }

    // first messsage and last message
    // cache is not empty but server is empty
    @Test
    public void updateCacheFirstMsgIdAndLastMsgId1() {
        long threadId = 2;
        Callback callback = new Callback();
        List<MessageVO> messageVOS = new ArrayList<>();
        callback.setOffset(0);
        callback.setCount(50);
        callback.setOrder("asc");
        callback.setFirstMessageId(9234);
        callback.setLastMessageId(9235);

        messageDatabaseHelper.updateGetHistoryResponse(callback, messageVOS, threadId, null);
    }

    // first messsage and last message
    // Conditional 2
    // cache siz more than one but server size is 1
    @Test
    public void updateCacheFirstMsgIdAndLastMsgIdConditional2() {
        long threadId = 2;
        Callback callback = new Callback();
        List<MessageVO> messageVOS = new ArrayList<>();

        MessageVO messageVO = new MessageVO(
                5653,
                false,
                false,
                false,
                false,
                false,
                "91efe7da-547f-4c5f-c34b-0442951ffbbc",
                0,
                5652,
                "",
                null,
                13354321,
                321000000,
                "",
                null,
                null,
                null,
                null

        );

        List<CacheMessageVO> cacheMessageVOS = new ArrayList<>();
        CacheMessageVO cacheMessageVO = new CacheMessageVO();
        cacheMessageVO.setId(5878);

        cacheMessageVO.setThreadVoId(2L);

        cacheMessageVOS.add(cacheMessageVO);
        messageVOS.add(messageVO);
        callback.setOffset(0);

        callback.setOffset(0);
        callback.setCount(50);
        callback.setOrder("asc");
        callback.setFirstMessageId(5652);
        callback.setLastMessageId(5878);

        messageDatabaseHelper.updateGetHistoryResponse(callback, messageVOS, threadId, cacheMessageVOS);
    }


    // first messsage and last message
    // Conditional 3
    // cache siz more than one but server size is more than 1
    @Test
    public void updateCacheFAndLConditional3() {
        long threadId = 2;
        Callback callback = new Callback();
        List<MessageVO> messageVOS = new ArrayList<>();
        MessageVO messageVO = new MessageVO(
                5653,
                false,
                false,
                false,
                false,
                false,
                "91efe7da-547f-4c5f-c34b-0442951ffbbc",
                0,
                5652,
                "",
                null,
                13354321,
                321000000,
                "",
                null,
                null,
                null,
                null
        );

        messageVOS.add(messageVO);
        callback.setOffset(0);
        callback.setCount(50);
        callback.setOrder("asc");
        callback.setFirstMessageId(5652);
        callback.setLastMessageId(5653);

        messageDatabaseHelper.updateGetHistoryResponse(callback, messageVOS, threadId, null);
    }

    //first message id conditional 1
    // cache siz more than one but server size is 1
    @Test
    public void justFirstMsgIdsetConditional1() {
        Callback callback = new Callback();
        long threadId = 2;

        MessageVO messageVO = new MessageVO(
                5653,
                false,
                false,
                false,
                false,
                false,
                "91efe7da-547f-4c5f-c34b-0442951ffbbc",
                0,
                5652,
                "",
                null,
                13354321,
                321000000,
                "",
                null,
                null,
                null,
                null
        );

        callback.setOffset(0);
        callback.setCount(2);
        callback.setOrder("asc");
        callback.setFirstMessageId(9261);

        messageDatabaseHelper.updateGetHistoryResponse(callback, null, threadId, null);
    }

    //first message id conditional 2
    @Test
    public void justFirstMsgIdset2() {
        Callback callback = new Callback();
        long threadId = 2;

        List<MessageVO> messageVOS = new ArrayList<>();

        MessageVO messageVO = new MessageVO(
                5653,
                false,
                false,
                false,
                false,
                false,
                "91efe7da-547f-4c5f-c34b-0442951ffbbc",
                0,
                5652,
                "",
                null,
                13354321,
                321000000,
                "",
                null,
                null,
                null,
                null
        );

        callback.setOffset(0);
        callback.setCount(2);
        callback.setOrder("asc");
        callback.setFirstMessageId(9236);

        messageDatabaseHelper.updateGetHistoryResponse(callback, null, threadId, null);
    }

    @Test
    public void justFirstMsgIdC3() {
        Callback callback = new Callback();
        long threadId = 2;


        callback.setOffset(0);
        callback.setCount(2);
        callback.setOrder("asc");
        callback.setFirstMessageId(9236);

        MessageVO messageVO = new MessageVO(
                5653,
                false,
                false,
                false,
                false,
                false,
                "91efe7da-547f-4c5f-c34b-0442951ffbbc",
                0,
                5652,
                "",
                null,
                13354321,
                321000000,
                "",
                null,
                null,
                null,
                null
        );

//        messageDatabaseHelper.updateGetHistoryResponse(callback);
    }


}
