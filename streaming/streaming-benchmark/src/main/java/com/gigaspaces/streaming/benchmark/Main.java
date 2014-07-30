package com.gigaspaces.streaming.benchmark;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Oleksiy_Dyagilev
 */
public class Main {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext(new String[] {"client-applicationContext.xml"});
    }
}
