package com.example.podchat;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fanap.podchat.cachemodel.PhoneContact;
import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.thread.ThreadManager;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.LinkedUser;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.persistance.RoomIntegrityException;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.util.DataTypeConverter;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.PodThreadManager;
import com.fanap.podchat.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Single;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    public void testOnComplete() {


        new PodThreadManager()
                .doWithUI(() -> {
                    System.out.println("On UI Im on " + Thread.currentThread().getName());

                }, () -> {
                    System.out.println("Error Im on " + Thread.currentThread().getName());

                }, () -> {
                    System.out.println("Task Im on " + Thread.currentThread().getName());

                    int a = 10;
                    int b = 0;

                    int c = a / b;

                });
//
//        new PodThreadManager()
//                .addNewTask(()->{
//
//                    System.out.println("aaa " + Thread.currentThread().getName());
//
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    System.out.println("aaa " + "aaa " + Thread.currentThread().getName());
//
//                })
//                .addNewTask(()->{
//
//                    System.out.println("bbb " + Thread.currentThread().getName());
//
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    System.out.println("bbb " + "bbb " + Thread.currentThread().getName());
//
//                })
//        .runTasksASync();
//
//        new PodThreadManager()
//                .doThisSafe(()->{
//
//                    System.out.println("aaa " + Thread.currentThread().getName());
//
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    System.out.println("aaa " + "aaa " + Thread.currentThread().getName());
//
//                }, new PodThreadManager.IComplete() {
//                    @Override
//                    public void onComplete() {
//                        System.out.println("v " + "aaa " + Thread.currentThread().getName());
//
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        System.out.println("a " + "aaa " + Thread.currentThread().getName());
//
//                    }
//                });


    }

    @Test
    public void testFirstObservable() {

        Observable<String> a = Observable.create(ss -> {

            ss.onCompleted();
            
        });


        Observable<String> b =Observable.create(ss -> {
            ss.onNext("b");
        });


        Observable<String> c = Observable.create(ss -> {
            ss.onNext("c");
        });


        Observable.concat(a, b)
                .doOnError(eee -> {
                    System.out.println("errr");
                })
                .onErrorResumeNext(Observable.empty())
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())

                .subscribe(d -> {

                    if (d != null)
                        System.out.println("IS : " + d);
                    else System.out.println("uuu");

                });


    }

    @Test
    public void runCompleteAfterError() {


        Observable<String> a = Observable.create(subscriber -> {

            System.out.println("1");
            try {
                Thread.sleep(3000);

                subscriber.onError(new RoomIntegrityException());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("2");

            subscriber.onNext("Hello");

        });


        a.subscribeOn(Schedulers.immediate())
                .onErrorResumeNext(tt -> {
                    System.out.println("onError res: " + tt);
                    return Observable.empty();
                })
                .observeOn(Schedulers.immediate())
                .doOnCompleted(() -> System.out.println("Comp"))
                .subscribe(System.out::println);


    }

    @Test
    public void sortContactsWithFullNameTest() {


        ArrayList<Contact> unsorted = new ArrayList<>();

        ArrayList<Contact> expected = new ArrayList<>();

        ArrayList<Contact> sorted;


        Contact contact1 = new Contact();

        contact1.setFirstName("Bahar");
        contact1.setLastName("Bohloli");
        contact1.setHasUser(true);

        unsorted.add(contact1);


        Contact contact2 = new Contact();

        contact2.setFirstName("Ali");
        contact2.setLastName("Alavi");
        contact2.setHasUser(true);


        unsorted.add(contact2);


        expected.add(contact2);
        expected.add(contact1);

        sorted = new ArrayList<>(unsorted);

        Collections.sort(sorted, compareContacts());

        Assert.assertEquals(expected, sorted);


    }

    @Test
    public void sortContactsWithFirstNameTest() {


        ArrayList<Contact> unsorted = new ArrayList<>();

        ArrayList<Contact> expected = new ArrayList<>();

        ArrayList<Contact> sorted;


        Contact contact1 = new Contact();

        contact1.setFirstName("Bahar");
        contact1.setLastName("Bohloli");
        contact1.setHasUser(true);

        unsorted.add(contact1);


        Contact contact2 = new Contact();

        contact2.setFirstName("Ali");

        contact2.setHasUser(true);


        unsorted.add(contact2);


        Contact contact3 = new Contact();
        contact3.setFirstName("Bahar");
        contact3.setLastName("Alavi");
        contact3.setHasUser(true);

        unsorted.add(contact3);

        Contact contact4 = new Contact();
        contact4.setFirstName("Ahmad");
        contact4.setLastName("Ahmadian");
        contact4.setHasUser(false);

        unsorted.add(contact4);

        expected.add(contact3);
        expected.add(contact1);
        expected.add(contact2);
        expected.add(contact4);

        sorted = new ArrayList<>(unsorted);

        Collections.sort(sorted, compareContacts());

        Assert.assertEquals(expected, sorted);


    }

    @Test
    public void sortThreadsTest() {


        com.fanap.podchat.mainmodel.Thread t = new com.fanap.podchat.mainmodel.Thread();
        t.setId(100);
        t.setUniqueName("t1");
        t.setTitle("im pinned");
        t.setTime(new Date().getTime() + 2);
        t.setPin(true);

        com.fanap.podchat.mainmodel.Thread v = new com.fanap.podchat.mainmodel.Thread();
        v.setId(101);
        v.setUniqueName("t2");
        v.setTitle("im pinned too");
        v.setTime(new Date().getTime() + 1);
        v.setPin(true);

        com.fanap.podchat.mainmodel.Thread c = new com.fanap.podchat.mainmodel.Thread();
        c.setId(103);
        c.setUniqueName("t3");
        c.setTitle("im not pinned");
        c.setTime(new Date().getTime());
        c.setPin(false);

        List<com.fanap.podchat.mainmodel.Thread> unsorted = new ArrayList<>();
        unsorted.add(c);
        unsorted.add(t);
        unsorted.add(v);

        System.out.println("unsorted -> " + App.getGson().toJson(unsorted));

        List<com.fanap.podchat.mainmodel.Thread> sorted = ThreadManager.sortThreads(unsorted);

        System.out.println("sorted -> " + App.getGson().toJson(sorted));

        assertNotEquals(sorted, unsorted);


    }

    @Test
    public void equalityTest() {

        com.fanap.podchat.mainmodel.Thread t = new com.fanap.podchat.mainmodel.Thread();
        t.setId(100);
        t.setUniqueName("adas");
        com.fanap.podchat.mainmodel.Thread v = new com.fanap.podchat.mainmodel.Thread();
        v.setId(100);
        v.setUniqueName("asdasde");
        v.setTitle("aaaaaa");

        ArrayList<com.fanap.podchat.mainmodel.Thread> oo = new ArrayList<>();

        oo.add(t);
        Assert.assertTrue(oo.contains(v));
//        if(oo.remove(t))
//            oo.add(v);
        oo.set(oo.indexOf(v), v);

        assertEquals(oo.get(0).getTitle(), "aaaaaa");

        System.out.println("S: => " + oo.size());


//
//        assertEquals(t,v);
//        assertEquals(oo.size(),1);
    }

    @Test
    public void pagingTest() {


        Observable.concat(getList1(), getList2())
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .first()
                .map(data -> page(data, 3, 2))
                .subscribe(data -> {
                    ArrayList<Integer> list1 = new ArrayList<>();
//                    list1.add(0);
//                    list1.add(1);
                    list1.add(2);
                    list1.add(3);
                    list1.add(4);
//                    list1.add(5);
//                    list1.add(6);

                    System.out.println("data -> " + data);
                    assertEquals(data, list1);
                });


        Observable.concat(getList1(), getList2())
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .first()
                .map(data -> page(data, 10, 2))
                .subscribe(data -> {
                    ArrayList<Integer> list1 = new ArrayList<>();
                    list1.add(2);
                    list1.add(3);
                    list1.add(4);
                    list1.add(5);
                    list1.add(6);

                    System.out.println("data -> " + data);
                    assertEquals(data, list1);
                });


        Observable.concat(getList1(), getList2())
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .first()
                .map(data -> page(data, 1, 0))
                .subscribe(data -> {
                    ArrayList<Integer> list1 = new ArrayList<>();
                    list1.add(0);


                    System.out.println("data -> " + data);
                    assertEquals(data, list1);
                });


        Observable.concat(getList1(), getList2())
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .first()
                .map(data -> page(data, 100, 20))
                .subscribe(data -> {
                    ArrayList<Integer> list1 = new ArrayList<>();


                    System.out.println("data -> " + data);
                    assertEquals(data, list1);
                });


        Observable.concat(getList1(), getList2())
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .first()
                .map(data -> page(data, 1, 100))
                .subscribe(data -> {
                    ArrayList<Integer> list1 = new ArrayList<>();


                    System.out.println("data -> " + data);
                    assertEquals(data, list1);
                });


    }

    @Test
    public void findByIdTest() {

        List<Long> threads = new ArrayList<>();
        threads.add(1012310L);
        threads.add(1012320L);
        threads.add(1012340L);
        threads.add(1012380L);

        List<Integer> searchThreads = new ArrayList<>();
        searchThreads.add(1012341);
        searchThreads.add(1012381);
        searchThreads.add(1012311);


        List<Long> expected = new ArrayList<>();
//        expected.add(1012311L);
//        expected.add(1012341L);
//        expected.add(1012381L);


        getByIds(searchThreads, threads)
                .subscribeOn(Schedulers.immediate())
                .observeOn(Schedulers.immediate())
                .subscribe(founded -> {

                    System.out.println(founded);

                    assertEquals(expected, founded);


                });


    }

    private static Comparator<Contact> compareContacts() {
        return (contact1, contact2) -> {

            if (contact1.isHasUser() && contact2.isHasUser()) {

                if (Util.isNotNullOrEmpty(contact1.getLastName()) &&
                        Util.isNotNullOrEmpty(contact2.getLastName())) {

                    return contact1.getLastName().compareTo(contact2.getLastName());

                } else if (Util.isNotNullOrEmpty(contact1.getLastName()) ||
                        Util.isNotNullOrEmpty(contact2.getLastName())) {

                    return (contact2.getLastName() != null ? contact2.getLastName() : "")
                            .compareTo((contact1.getLastName() != null ? contact1.getLastName() : ""));

                } else if (Util.isNotNullOrEmpty(contact1.getFirstName()) &&
                        Util.isNotNullOrEmpty(contact2.getFirstName())) {

                    return contact1.getFirstName().compareTo(contact2.getFirstName());

                } else if (Util.isNotNullOrEmpty(contact1.getFirstName()) ||
                        Util.isNotNullOrEmpty(contact2.getFirstName())) {

                    return (contact2.getFirstName() != null ? contact2.getFirstName() : "")
                            .compareTo((contact1.getFirstName() != null ? contact1.getFirstName() : ""));

                } else return 1;

            } else return Boolean.compare(contact2.isHasUser(), contact1.isHasUser());


        };
    }

    public static Observable<List<Long>> getByIds(List<Integer> ids, List<Long> allThreads) {

        try {
            if (Util.isNotNullOrEmpty(ids))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return Observable.from(allThreads)
                            .filter(t -> ids.contains(Math.toIntExact(t)))
                            .toList();
                } else {
                    return Observable.from(allThreads)
                            .filter(t -> ids.contains((int) (long) t))
                            .toList();
                }
        } catch (Exception e) {
            return Observable.from(allThreads).toList();
        }

        return Observable.from(allThreads).toList();

    }

    public Observable<List<Integer>> getList1() {

        ArrayList<Integer> list1 = new ArrayList<>();
        list1.add(0);
        list1.add(1);
        list1.add(2);
        list1.add(3);
        list1.add(4);
        list1.add(5);
        list1.add(6);

        return Observable.create(en -> en.onNext(list1));

    }

    public Observable<List<Integer>> getList2() {

        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(7);
        list2.add(8);
        list2.add(9);

        return Observable.create(en -> en.onNext(list2));

    }

    public Observable<List<Integer>> lim(Integer lim, List<Integer> list) {

        return Observable.from(list)
                .limit(lim)
                .toList();

    }

    public <T> List<T> page(List<T> items, int count, int offset) {

        if (items.size() == 0 || count == 0) {
            return new ArrayList<>();
        }

        if (count + offset > items.size()) {

            if (offset > items.size())
                return new ArrayList<>();

            return items.subList(offset, items.size());
        }

        return items.subList(offset, offset + count);

    }

    @Test
    public void contactsEquality() {

        PhoneContact a = new PhoneContact();
        a.setName("a contact");
        a.setLastName("a lastname");
        a.setPhoneNumber("+98 915 777 0684");
        a.setVersion(150);


        PhoneContact b = new PhoneContact();
        b.setName("b contact");
        b.setLastName("b lastname");
        b.setPhoneNumber("+98 915 777 0684");
        b.setVersion(750);


        boolean e = a.equals(b);
        System.out.println(e);
        assertEquals(a, b);


    }


    @Test
    public void testConverters() {


        String p = "";


        List<Participant> participants = stringToList(p);

        String a = ListToString(participants);

        List<Participant> p2 = stringToList(a);

        assertEquals(participants, p2);


    }


    public List<Participant> stringToList(@Nullable String data) {

        Gson gson = new Gson();

        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Participant>>() {
        }.getType();

        return gson.fromJson(data, listType);
    }


    public String ListToString(List<Participant> t) {
        Gson gson = new Gson();
        return gson.toJson(t);
    }


    @Test
    public void contactsEqualityInList() {

        HashMap<String, PhoneContact> phoneContacts = new HashMap<>();
        List<PhoneContact> list = new ArrayList<>();


        PhoneContact a = new PhoneContact();
        a.setName("a contact");
        a.setLastName("a lastname");
        a.setPhoneNumber("+98 915 777 0684");
        a.setVersion(150);


        PhoneContact b = new PhoneContact();
        b.setName("b contact");
        b.setLastName("b lastname");
        b.setPhoneNumber("+98 915 777 0684");
        b.setVersion(750);

        phoneContacts.put(a.getPhoneNumber(), a);
        list.add(b);
        phoneContacts.put(b.getPhoneNumber(), b);
        list.add(a);

        System.out.println(phoneContacts.size());

        assertEquals(phoneContacts.size(), 1);

        assertEquals(phoneContacts.get("+98 915 777 0684").getName(), "b contact");

        System.out.println("list + " + list);
        Collections.sort(list, (o1, o2) -> Long.compare(o1.getVersion(), o2.getVersion()));
        System.out.println("list + " + list);

    }

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
    public void testExec() {

        BlockingQueue<Runnable> works = new LinkedBlockingDeque<>();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                1,
                TimeUnit.SECONDS,
                works
        );

//        Executor executor = command -> new Thread(command).start();

        executor.execute(() -> {
            try {
                String threadName = Thread.currentThread().getName();
                System.out.println("Start Job A in: " + threadName);
                Thread.sleep(5000);
                System.out.println("Job A Done");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        executor.execute(() -> {
            try {
                String threadName = Thread.currentThread().getName();
                System.out.println("Start Job B in: " + threadName);
                Thread.sleep(1000);
                System.out.println("Job B Done");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        executor.execute(() -> {
            try {
                String threadName = Thread.currentThread().getName();
                System.out.println("Start Job C in: " + threadName);
                Thread.sleep(10000);
                System.out.println("Job C Done");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
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

    @Test
    public void testMapVAlues() {

        Map<String, ArrayList<Long>> notificationsGroup = new HashMap<>();

        ArrayList<Long> a = new ArrayList<>();
        a.add(123L);
        a.add(122L);
        a.add(121L);
        a.add(120L);
        ArrayList<Long> b = new ArrayList<>();
        b.add(23L);
        b.add(12L);
        b.add(21L);
        b.add(11L);
        b.add(1220L);
        b.add(111110L);
        b.add(11L);

        notificationsGroup.put("a", a);
        notificationsGroup.put("b", b);


        int count = 0;
        for (String key :
                notificationsGroup.keySet()) {

            try {
                count += notificationsGroup.get(key) != null ? notificationsGroup.get(key).size() : 0;
            } catch (Exception ignored) {
            }

        }


        assertEquals(count, a.size() + b.size());

    }

}