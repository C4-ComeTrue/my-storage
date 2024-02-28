package com.c4cometrue.mystorage.util;

import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("데이터 사이즈 컨버터 테스트")
class DataSizeConverterTest {
    @Test
    @DisplayName("기가 바이트를 바이트로 바꾸는 테스트")
    void testGigabytesToBytes() {
        long gigabytes = 1L;
        BigDecimal expected = new BigDecimal("1073741824");
        BigDecimal actual = DataSizeConverter.gigabytesToBytes(gigabytes);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("바이트를 기가 바이트로 바꾸는 테스트")
    void testBytesToGigaBytes() {
        BigDecimal bytes = new BigDecimal("1073741824");
        BigDecimal expected = new BigDecimal("1.00000");
        BigDecimal actual = DataSizeConverter.bytesToGigaBytes(bytes);
        Assertions.assertEquals(expected, actual);
    }
}
