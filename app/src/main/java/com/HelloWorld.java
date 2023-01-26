package com;

public class HelloWorld {
    static {
        System.loadLibrary("temp_jni");
    }
    public static native String hello(String input);
}
