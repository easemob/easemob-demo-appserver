package com.easemob.app.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUidUtils {
    public static String getUid() {
        int randomUid = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        if (randomUid == 0) {
            return String.valueOf(randomUid + 1);
        }
        return String.valueOf(randomUid);
    }
}
