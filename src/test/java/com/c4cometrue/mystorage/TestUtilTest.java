package com.c4cometrue.mystorage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestUtilTest {

    @Test
    void test1() {
        assertEquals(30L, TestUtil.test());
    }

    @Test
    void test2() {
        assertEquals(10L, TestUtil.test2(true));
    }
}