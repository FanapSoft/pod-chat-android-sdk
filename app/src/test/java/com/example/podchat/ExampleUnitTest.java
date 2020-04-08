package com.example.podchat;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.util.DataTypeConverter;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void testTimes() {

        Date date = new Date();

        long start = System.currentTimeMillis();

        System.out.println("Start: " + start);


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println("End: " + end);


        System.out.println(end - start);

        Assert.assertEquals(3000, end - start);


    }


    @Test
    public void inviteeToJson() {

        Gson gson = new GsonBuilder().create();


        List<String> invitees = new ArrayList<>();

        invitees.add("fkheirkhah");
        invitees.add("f.khojasteh");
        invitees.add("m.zhiani");


        JsonArray participantsJsonArray = new JsonArray();


        try {
            for (String username :
                    invitees) {

                Invitee invitee = new Invitee();
                invitee.setId(username);
                invitee.setIdType(InviteType.Constants.TO_BE_USER_USERNAME);
                JsonElement jsonElement = gson.toJsonTree(invitee);
                participantsJsonArray.add(jsonElement);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }


        Assert.assertTrue(true);


    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void instanceOfTest() {

        RequestCreatePublicThread request =
                new RequestCreatePublicThread.Builder(
                        0, new ArrayList<>(), "unique"
                ).build();

        getUniqueName(request);


    }


    public void getUniqueName(RequestCreateThread request) {


        if (request instanceof RequestCreatePublicThread) {

            RequestCreatePublicThread pt = (RequestCreatePublicThread) request;

            Assert.assertEquals("unique", pt.getUniqueName());


        } else {

            Assert.fail("Name is Lost");


        }


    }


    @Test
    public void compareDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.SECOND, 2);
        Date expireDate = c.getTime();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        c.setTime(new Date());
        Date nowDate = c.getTime();

        if (expireDate.compareTo(nowDate) < 0) {
            assertTrue(true);
        } else {
            assertFalse(false);
        }

    }

    @Test
    public void checkNullOrEmpty() {
        String srting = "yes";

        assertFalse(Util.isNullOrEmpty(srting));

    }

    @Test
    public void checkNullOrEmptyNumber() {
        assertTrue(Util.isNullOrEmpty(0));
    }

    @Test
    public void addTimeToNanos() {
        long time = 1546954441289L;
        long timeNanos = 289391000;
        long pow = (long) Math.pow(10, 9);
        long timestamp = ((time / 1000) * pow) + timeNanos;

        assertNotEquals(13351, 1546954441289391000L);
    }

    @Test
    public void sepratedString() {
        String center = "35.7003510,51.3376472";
        String part1 = center.substring(0, center.lastIndexOf(','));
//        int lastIndex = center.lastIndexOf('2');
        String part2 = center.substring(center.lastIndexOf(',') + 1, center.length());

    }


    @Test
    public void listTest() {


        ArrayList<String> list = new ArrayList<>();

        list.add("1");

        list.add("23");


        if (list.contains("4")) {

            list.remove("4");

        } else {

            list.add("4");

        }


        assertEquals(3, list.size());


    }


    @Test
    public void testFindKeyWithValue() {


        HashMap<String, ArrayList<String>> map = new HashMap<>();

        ArrayList<String> a1 = new ArrayList<>();
        ArrayList<String> a2 = new ArrayList<>();
        ArrayList<String> a3 = new ArrayList<>();

        a1.add("a1val1");
        a1.add("a1val2");
        a1.add("a1val3");

        a2.add("a2val1");
        a2.add("a2val2");
        a2.add("a2val3");

        a3.add("a3val1");
        a3.add("a3val2");
        a3.add("a3val3");


        map.put("key1", a1);
        map.put("key2", a2);
        map.put("key3", a3);


//       assertEquals( findKeyWithUniqueValue(map,"a3val3"),"key3");
//       assertEquals( findKeyWithUniqueValue(map,"a3val4"),"");
//       assertEquals( findKeyWithUniqueValue(map,"a1val2"),"key1");
//
        assertEquals(Util.findKeyWithUniqueValue(map, "a3val3"), "key3");
        assertEquals(Util.findKeyWithUniqueValue(map, "a3val4"), "");
        assertEquals(Util.findKeyWithUniqueValue(map, "a1val2"), "key1");
        assertNotEquals(Util.findKeyWithUniqueValue(map, "a1val2"), "");


    }

    private String findKeyWithValue(Map<String, ArrayList<String>> map, String query) {

        for (String key : map.keySet()) {

            for (String value :
                    map.get(key)) {

                if (value.equals(query))
                    return key;

            }


        }

        return "";
    }


    @Test
    public void converters() {

        String s = "[\"thread_admin\",\"add_new_user\",\"remove_user\"]";

//        String s = "[role1,role2,role3,role4]";

        DataTypeConverter dataTypeConverter = new DataTypeConverter();

        List<String> lst = new ArrayList<>();

        lst.add("thread_admin");
        lst.add("add_new_user");
        lst.add("remove_user");

        System.out.println(s);
        System.out.println(lst.toString());
        System.out.println("***");
        System.out.println(dataTypeConverter.dataToList(s));
        System.out.println(dataTypeConverter.convertListToString(lst));

        assertEquals(lst, dataTypeConverter.dataToList(s));
        assertEquals(s, dataTypeConverter.convertListToString(lst));


    }


    @Test
    public void testExtensions() {

        String aGifPath = "storage/emulated/m_file.gif";

        assertTrue(aGifPath.endsWith(".gif"));
        assertFalse(aGifPath.endsWith(".jpg"));


    }


    @Test
    public void testListEquality() {


        ArrayList<String> parentList = new ArrayList<>();
        parentList.add("10");
        parentList.add("9");
        parentList.add("8");
        parentList.add("7");
        parentList.add("6");
        parentList.add("5");


        ArrayList<String> childList = new ArrayList<>();
        childList.add("6");
        childList.add("5");
        childList.add("4");
        childList.add("3");
        childList.add("2");
        childList.add("1");


        ArrayList<String> answer = new ArrayList<>();
        answer.add("10");
        answer.add("9");
        answer.add("8");
        answer.add("7");


        parentList.removeAll(childList);

        assertEquals(parentList, answer);

    }


}