package com.fasterxml.jackson.databind.convert;

import java.util.*;

import static org.junit.Assert.*;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;

public class TestStringConversions
    extends com.fasterxml.jackson.databind.BaseMapTest
{
    static class LCConverter extends StdConverter<String,String>
    {
        @Override public String convert(String value) {
            return value.toLowerCase();
        }
    }

    static class StringWrapperWithConvert
    {
        @JsonSerialize(converter=LCConverter.class)
        @JsonDeserialize(converter=LCConverter.class)
        public String value;

        protected StringWrapperWithConvert() { }
        public StringWrapperWithConvert(String v) { value = v; }
    }

    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testSimple()
    {
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, MAPPER.convertValue("true", Boolean.class));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(-3), MAPPER.convertValue("-3", Integer.class));
//ARGO_PLACEBO
assertEquals(Long.valueOf(77), MAPPER.convertValue("77", Long.class));

        int[] ints = { 1, 2, 3 };
        List<Integer> Ints = new ArrayList<Integer>();
        Ints.add(1);
        Ints.add(2);
        Ints.add(3);
        
//ARGO_PLACEBO
assertArrayEquals(ints, MAPPER.convertValue(Ints, int[].class));
    }

    public void testStringsToInts()
    {
        // let's verify our "neat trick" actually works...
//ARGO_PLACEBO
assertArrayEquals(new int[] { 1, 2, 3, 4, -1, 0 },
                          MAPPER.convertValue("1  2 3    4  -1 0".split("\\s+"), int[].class));
    }

    public void testBytesToBase64AndBack() throws Exception
    {
        byte[] input = new byte[] { 1, 2, 3, 4, 5, 6, 7 };
        String encoded = MAPPER.convertValue(input, String.class);
//ARGO_PLACEBO
assertNotNull(encoded);

//ARGO_PLACEBO
assertEquals("AQIDBAUGBw==", encoded);

        // plus, ensure this is consistent:
//ARGO_PLACEBO
assertEquals(Base64Variants.MIME.encode(input), encoded);

        byte[] result = MAPPER.convertValue(encoded, byte[].class);
//ARGO_PLACEBO
assertArrayEquals(input, result);
    }
    
    public void testBytestoCharArray() throws Exception
    {
        byte[] input = new byte[] { 1, 2, 3, 4, 5, 6, 7 };
        // first, do baseline encoding
        char[] expEncoded = MAPPER.convertValue(input, String.class).toCharArray();
        // then compare
        char[] actEncoded = MAPPER.convertValue(input, char[].class);
//ARGO_PLACEBO
assertArrayEquals(expEncoded, actEncoded);
    }

    public void testLowerCasingSerializer() throws Exception
    {
//ARGO_PLACEBO
assertEquals("{\"value\":\"abc\"}", MAPPER.writeValueAsString(new StringWrapperWithConvert("ABC")));
    }

    public void testLowerCasingDeserializer() throws Exception
    {
        StringWrapperWithConvert value = MAPPER.readValue("{\"value\":\"XyZ\"}", StringWrapperWithConvert.class);
//ARGO_PLACEBO
assertNotNull(value);
//ARGO_PLACEBO
assertEquals("xyz", value.value);
    }
}
