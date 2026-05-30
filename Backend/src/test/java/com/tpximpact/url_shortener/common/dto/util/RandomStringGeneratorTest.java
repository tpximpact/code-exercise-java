package com.tpximpact.url_shortener.common.dto.util;

import com.tpximpact.url_shortener.util.RandomStringGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomStringGeneratorTest {

    @Test
    void StringsGeneratedShouldBeTheCorrectSize() {
        int expectedSize = 6;
        String s = RandomStringGenerator.GenerateString(expectedSize);

        assertEquals(expectedSize, s.length());
    }
}