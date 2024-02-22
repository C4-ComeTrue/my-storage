package com.c4cometrue.mystorage.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DataSizeConverter {
    private DataSizeConverter() {
        throw new AssertionError("should not be invoke");
    }

    public static BigDecimal gigabytesToBytes(long gigabytes) {
        return BigDecimal.valueOf(gigabytes * 1024 * 1024 * 1024);
    }

    public static BigDecimal bytesToGigaBytes(BigDecimal bytes) {
        return bytes.divide(BigDecimal.valueOf(1024 * 1024 * 1024), 2, RoundingMode.HALF_UP);
    }
}
