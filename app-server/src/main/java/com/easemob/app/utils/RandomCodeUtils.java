package com.easemob.app.utils;

public class RandomCodeUtils {

    public static int smsCode() {
        return (int)((Math.random() * 9 + 1) * 100000);
    }

    public static int imageCode() {
        return (int)((Math.random() * 9 + 1) * 1000);
    }
}
