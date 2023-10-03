package com.c4cometrue.mystorage;

public class TestUtil {
    public static long test() {
        var data    = 10L;
        var data2   = 20L;

        return data + data2;
    }

    public static long test2(boolean go) {
        var data = 10L;
        var data2 = 20L;

        return go ? data : data2;
    }
}
