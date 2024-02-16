package com.c4cometrue.mystorage.util;

import java.math.BigDecimal;

public class DataSizeConverter {
    private DataSizeConverter() {
        throw new AssertionError("should not be invoke");
    }

    public static BigDecimal gigabytesToBytes(long gigabytes) {
        return BigDecimal.valueOf(gigabytes * 1024 * 1024 * 1024);
    }
}
