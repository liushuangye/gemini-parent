package com.gemini.toolkit.universal.calculate.service;

public interface GroovyTestService{
    default void fun1(){
        System.out.println("hello groovy!");
    };
    String fun2(String name);
}
