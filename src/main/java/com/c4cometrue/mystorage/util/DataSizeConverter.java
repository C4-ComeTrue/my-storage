package com.c4cometrue.mystorage.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DataSizeConverter {
    private static final BigDecimal BYTES_PER_GIGABYTE = BigDecimal.valueOf(1024 * 1024 * 1024);

    private DataSizeConverter() {
        throw new AssertionError("DataSizeConverter should not be instantiated");
    }

    public static BigDecimal gigabytesToBytes(long gigabytes) {
        return BigDecimal.valueOf(gigabytes).multiply(BYTES_PER_GIGABYTE);
    }

    public static BigDecimal bytesToGigaBytes(BigDecimal bytes) {
        return bytes.divide(BYTES_PER_GIGABYTE, 5, RoundingMode.HALF_UP);
    }
}
