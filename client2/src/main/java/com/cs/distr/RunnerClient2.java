package com.cs.distr;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author: artsiom.kotov
 */
public class RunnerClient2 {
    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("appConfig.xml");
        System.out.println("waiting for requests...");
    }
}
