package com.gigaspaces.storm.googleanalytics.feeder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Mykola_Zalyayev
 */
public class Main {

    public static void main(String[] args) {
        if(args.length<1){
            throw new IllegalArgumentException("Unable to find host parameter");
        }
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        FeederStarter starter = context.getBean(FeederStarter.class);
        starter.start(args[0]);

    }
}
