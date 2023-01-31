package com.zKrypto;

public class Proof {
    static {
        System.loadLibrary("jni");
    }
//    public static native String hello(String input);

    public static native String encrypt(String input);
}
