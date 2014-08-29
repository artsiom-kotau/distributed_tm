package com.cs.distr.client1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author: artsiom.kotov
 */
public class RunnerClient1 {
    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("appConfig.xml");
        System.out.println("waiting for requests...");
    }
}
