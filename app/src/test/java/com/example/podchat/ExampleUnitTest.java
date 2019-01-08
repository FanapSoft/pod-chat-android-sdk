package com.example.podchat;

import com.fanap.podchat.persistance.MessageDatabaseHelper;
import com.fanap.podchat.util.Util;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
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
    public void checkNullOrEmptyNumber(){
        assertTrue(Util.isNullOrEmpty(0));
    }

    @Test
    public void addTimeToNanos(){
        long time = 1546954441289L;
        long timeNanos = 289391000;
        long pow =(long) Math.pow(10,9);
        long timestamp = ((time/1000)*pow)+timeNanos;

        assertNotEquals(13351,1546954441289391000L);
    }

}