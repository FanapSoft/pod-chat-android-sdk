package com.example.podchat;

import org.junit.Assert;

public class TestClass {


    public static void main(String[] args) {

        new Thread(() -> {

            System.out.println("1 Starts");

            sleep(1000);

            System.out.println("1 Done");


        }).start();

        new Thread(() -> {

            sleep(500);
            System.out.println("2 ");

        }).start();


        new Thread(() -> System.out.println("3")).start();
        new Thread(() -> System.out.println("4")).start();


    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Assert.fail();
        }
    }
}
