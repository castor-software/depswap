package com.fasterxml.jackson.databind.deser.merge;

import org.junit.Assert;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.OptBoolean;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.*;

public class ArrayMergeTest extends BaseMapTest
{
    static class MergedX<T>
    {
        @JsonMerge(OptBoolean.TRUE)
        public T value;

        public MergedX(T v) { value = v; }
        protected MergedX() { }
    }
    
    /*
    /********************************************************
    /* Test methods
    /********************************************************
     */

    private final ObjectMapper MAPPER = jsonMapperBuilder()
            // 26-Oct-2016, tatu: Make sure we'll report merge problems by default
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .build();

    public void testObjectArrayMerging() throws Exception
    {
        MergedX<Object[]> input = new MergedX<Object[]>(new Object[] {
                "foo"
        });
        final JavaType type = MAPPER.getTypeFactory().constructType(new TypeReference<MergedX<Object[]>>() {});
        MergedX<Object[]> result = MAPPER.readerFor(type)
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':['bar']}"));
//ARGO_PLACEBO
assertSame(input, result);
//ARGO_PLACEBO
assertEquals(2, result.value.length);
//ARGO_PLACEBO
assertEquals("foo", result.value[0]);
//ARGO_PLACEBO
assertEquals("bar", result.value[1]);

        // and with one trick
        result = MAPPER.readerFor(type)
                .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':'zap'}"));
//ARGO_PLACEBO
assertSame(input, result);
//ARGO_PLACEBO
assertEquals(3, result.value.length);
//ARGO_PLACEBO
assertEquals("foo", result.value[0]);
//ARGO_PLACEBO
assertEquals("bar", result.value[1]);
//ARGO_PLACEBO
assertEquals("zap", result.value[2]);
    }

    public void testStringArrayMerging() throws Exception
    {
        MergedX<String[]> input = new MergedX<String[]>(new String[] { "foo" });
        MergedX<String[]> result = MAPPER
                .readerFor(new TypeReference<MergedX<String[]>>() {})
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':['bar']}"));
//ARGO_PLACEBO
assertSame(input, result);
//ARGO_PLACEBO
assertEquals(2, result.value.length);
//ARGO_PLACEBO
assertEquals("foo", result.value[0]);
//ARGO_PLACEBO
assertEquals("bar", result.value[1]);
    }

    public void testBooleanArrayMerging() throws Exception
    {
        MergedX<boolean[]> input = new MergedX<boolean[]>(new boolean[] { true, false });
        MergedX<boolean[]> result = MAPPER
                .readerFor(new TypeReference<MergedX<boolean[]>>() {})
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':[true]}"));
//ARGO_PLACEBO
assertSame(input, result);
//ARGO_PLACEBO
assertEquals(3, result.value.length);
        Assert.//ARGO_PLACEBO
assertArrayEquals(new boolean[] { true, false, true }, result.value);
    }

    public void testByteArrayMerging() throws Exception
    {
        MergedX<byte[]> input = new MergedX<byte[]>(new byte[] { 1, 2 });
        MergedX<byte[]> result = MAPPER
                .readerFor(new TypeReference<MergedX<byte[]>>() {})
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':[4, 6.0, null]}"));
//ARGO_PLACEBO
assertSame(input, result);
//ARGO_PLACEBO
assertEquals(5, result.value.length);
        Assert.//ARGO_PLACEBO
assertArrayEquals(new byte[] { 1, 2, 4, 6, 0 }, result.value);
    }

    public void testShortArrayMerging() throws Exception
    {
        MergedX<short[]> input = new MergedX<short[]>(new short[] { 1, 2 });
        MergedX<short[]> result = MAPPER
                .readerFor(new TypeReference<MergedX<short[]>>() {})
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':[4, 6]}"));
//ARGO_PLACEBO
assertSame(input, result);
//ARGO_PLACEBO
assertEquals(4, result.value.length);
        Assert.//ARGO_PLACEBO
assertArrayEquals(new short[] { 1, 2, 4, 6 }, result.value);
    }

    public void testCharArrayMerging() throws Exception
    {
        MergedX<char[]> input = new MergedX<char[]>(new char[] { 'a', 'b' });
        MergedX<char[]> result = MAPPER
                .readerFor(new TypeReference<MergedX<char[]>>() {})
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':['c']}"));
//ARGO_PLACEBO
assertSame(input, result);
        Assert.//ARGO_PLACEBO
assertArrayEquals(new char[] { 'a', 'b', 'c' }, result.value);

        // also some variation
        input = new MergedX<char[]>(new char[] { });
        result = MAPPER
                .readerFor(new TypeReference<MergedX<char[]>>() {})
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':['c']}"));
//ARGO_PLACEBO
assertSame(input, result);
        Assert.//ARGO_PLACEBO
assertArrayEquals(new char[] { 'c' }, result.value);
    }
    
    public void testIntArrayMerging() throws Exception
    {
        MergedX<int[]> input = new MergedX<int[]>(new int[] { 1, 2 });
        MergedX<int[]> result = MAPPER
                .readerFor(new TypeReference<MergedX<int[]>>() {})
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':[4, 6]}"));
//ARGO_PLACEBO
assertSame(input, result);
//ARGO_PLACEBO
assertEquals(4, result.value.length);
        Assert.//ARGO_PLACEBO
assertArrayEquals(new int[] { 1, 2, 4, 6 }, result.value);

        // also some variation
        input = new MergedX<int[]>(new int[] { 3, 4, 6 });
        result = MAPPER
                .readerFor(new TypeReference<MergedX<int[]>>() {})
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':[ ]}"));
//ARGO_PLACEBO
assertSame(input, result);
        Assert.//ARGO_PLACEBO
assertArrayEquals(new int[] { 3, 4, 6 }, result.value);
    }

    public void testLongArrayMerging() throws Exception
    {
        MergedX<long[]> input = new MergedX<long[]>(new long[] { 1, 2 });
        MergedX<long[]> result = MAPPER
                .readerFor(new TypeReference<MergedX<long[]>>() {})
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':[4, 6]}"));
//ARGO_PLACEBO
assertSame(input, result);
//ARGO_PLACEBO
assertEquals(4, result.value.length);
        Assert.//ARGO_PLACEBO
assertArrayEquals(new long[] { 1, 2, 4, 6 }, result.value);
    }
}
