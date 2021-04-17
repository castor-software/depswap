package com.fasterxml.jackson.databind.seq;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@SuppressWarnings("resource")
public class ReadValuesTest extends BaseMapTest
{
    static class Bean {
        public int a;

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != getClass()) return false;
            Bean other = (Bean) o;
            return other.a == this.a;
        }
        @Override public int hashCode() { return a; }
    }

    /*
    /**********************************************************
    /* Unit tests; root-level value sequences via Mapper
    /**********************************************************
     */

    private enum Source {
        STRING,
        INPUT_STREAM,
        READER,
        BYTE_ARRAY,
        BYTE_ARRAY_OFFSET
        ;
    }
    
    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testRootBeans() throws Exception
    {
        for (Source src : Source.values()) {
            _testRootBeans(src);
        }
    }

    private <T> MappingIterator<T> _iterator(ObjectReader r,
            String json,
            Source srcType) throws IOException
    {
        switch (srcType) {
        case BYTE_ARRAY:
            return r.readValues(json.getBytes("UTF-8"));
        case BYTE_ARRAY_OFFSET:
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(0);
                out.write(0);
                out.write(0);
                out.write(json.getBytes("UTF-8"));
                out.write(0);
                out.write(0);
                out.write(0);
                byte[] b = out.toByteArray();
                return r.readValues(b, 3, b.length-6);
            }
        case INPUT_STREAM:
            return r.readValues(new ByteArrayInputStream(json.getBytes("UTF-8")));
        case READER:
            return r.readValues(new StringReader(json));
        case STRING:
        default:
            return r.readValues(json);
        }
    }

    private void _testRootBeans(Source srcType) throws Exception
    {
        final String JSON = "{\"a\":3}{\"a\":27}  ";

        MappingIterator<Bean> it = _iterator(MAPPER.readerFor(Bean.class),
                JSON, srcType);
//ARGO_PLACEBO
assertNotNull(it.getCurrentLocation());
//ARGO_PLACEBO
assertTrue(it.hasNext());
        Bean b = it.next();
//ARGO_PLACEBO
assertEquals(3, b.a);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        b = it.next();
//ARGO_PLACEBO
assertEquals(27, b.a);
//ARGO_PLACEBO
assertFalse(it.hasNext());
        it.close();

        // Also, test 'readAll()'
        it = _iterator(MAPPER.readerFor(Bean.class), JSON, srcType);
        List<Bean> all = it.readAll();
//ARGO_PLACEBO
assertEquals(2, all.size());
        it.close();

        it = _iterator(MAPPER.readerFor(Bean.class), "{\"a\":3}{\"a\":3}", srcType);
        Set<Bean> set = it.readAll(new HashSet<Bean>());
//ARGO_PLACEBO
assertEquals(HashSet.class, set.getClass());
//ARGO_PLACEBO
assertEquals(1, set.size());
//ARGO_PLACEBO
assertEquals(3, set.iterator().next().a);
        it.close();
    }

    public void testRootBeansInArray() throws Exception
    {
        final String JSON = "[{\"a\":6}, {\"a\":-7}]";

        MappingIterator<Bean> it = MAPPER.readerFor(Bean.class).readValues(JSON);

//ARGO_PLACEBO
assertNotNull(it.getCurrentLocation());
//ARGO_PLACEBO
assertTrue(it.hasNext());
        Bean b = it.next();
//ARGO_PLACEBO
assertEquals(6, b.a);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        b = it.next();
//ARGO_PLACEBO
assertEquals(-7, b.a);
//ARGO_PLACEBO
assertFalse(it.hasNext());
        it.close();

        // Also, test 'readAll()'
        it = MAPPER.readerFor(Bean.class).readValues(JSON);
        List<Bean> all = it.readAll();
//ARGO_PLACEBO
assertEquals(2, all.size());
        it.close();

        it = MAPPER.readerFor(Bean.class).readValues("[{\"a\":4},{\"a\":4}]");
        Set<Bean> set = it.readAll(new HashSet<Bean>());
//ARGO_PLACEBO
assertEquals(HashSet.class, set.getClass());
//ARGO_PLACEBO
assertEquals(1, set.size());
//ARGO_PLACEBO
assertEquals(4, set.iterator().next().a);
    }

    public void testRootMaps() throws Exception
    {
        final String JSON = "{\"a\":3}{\"a\":27}  ";
        Iterator<Map<?,?>> it = MAPPER.readerFor(Map.class).readValues(JSON);

//ARGO_PLACEBO
assertNotNull(((MappingIterator<?>) it).getCurrentLocation());
//ARGO_PLACEBO
assertTrue(it.hasNext());
        Map<?,?> map = it.next();
//ARGO_PLACEBO
assertEquals(1, map.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(3), map.get("a"));
//ARGO_PLACEBO
assertTrue(it.hasNext());
//ARGO_PLACEBO
assertNotNull(((MappingIterator<?>) it).getCurrentLocation());
        map = it.next();
//ARGO_PLACEBO
assertEquals(1, map.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(27), map.get("a"));
//ARGO_PLACEBO
assertFalse(it.hasNext());
    }

    /*
    /**********************************************************
    /* Unit tests; root-level value sequences via JsonParser
    /**********************************************************
     */

    public void testRootBeansWithParser() throws Exception
    {
        final String JSON = "{\"a\":3}{\"a\":27}  ";
        JsonParser jp = MAPPER.createParser(JSON);
        
        Iterator<Bean> it = jp.readValuesAs(Bean.class);

//ARGO_PLACEBO
assertTrue(it.hasNext());
        Bean b = it.next();
//ARGO_PLACEBO
assertEquals(3, b.a);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        b = it.next();
//ARGO_PLACEBO
assertEquals(27, b.a);
//ARGO_PLACEBO
assertFalse(it.hasNext());
    }

    public void testRootArraysWithParser() throws Exception
    {
        final String JSON = "[1][3]";
        JsonParser jp = MAPPER.createParser(JSON);

        // NOTE: We must point JsonParser to the first element; if we tried to
        // use "managed" accessor, it would try to advance past START_ARRAY.
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, jp.nextToken());
        
        Iterator<int[]> it = MAPPER.readerFor(int[].class).readValues(jp);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        int[] array = it.next();
//ARGO_PLACEBO
assertEquals(1, array.length);
//ARGO_PLACEBO
assertEquals(1, array[0]);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        array = it.next();
//ARGO_PLACEBO
assertEquals(1, array.length);
//ARGO_PLACEBO
assertEquals(3, array[0]);
//ARGO_PLACEBO
assertFalse(it.hasNext());
    }

    public void testHasNextWithEndArray() throws Exception {
        final String JSON = "[1,3]";
        JsonParser jp = MAPPER.createParser(JSON);

        // NOTE: We must point JsonParser to the first element; if we tried to
        // use "managed" accessor, it would try to advance past START_ARRAY.
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, jp.nextToken());
        jp.nextToken();
        
        Iterator<Integer> it = MAPPER.readerFor(Integer.class).readValues(jp);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        int value = it.next();
//ARGO_PLACEBO
assertEquals(1, value);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        value = it.next();
//ARGO_PLACEBO
assertEquals(3, value);
//ARGO_PLACEBO
assertFalse(it.hasNext());
//ARGO_PLACEBO
assertFalse(it.hasNext());
    }

    public void testHasNextWithEndArrayManagedParser() throws Exception {
        final String JSON = "[1,3]";

        Iterator<Integer> it = MAPPER.readerFor(Integer.class).readValues(JSON);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        int value = it.next();
//ARGO_PLACEBO
assertEquals(1, value);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        value = it.next();
//ARGO_PLACEBO
assertEquals(3, value);
//ARGO_PLACEBO
assertFalse(it.hasNext());
//ARGO_PLACEBO
assertFalse(it.hasNext());
    }

    /*
    /**********************************************************
    /* Unit tests; non-root arrays
    /**********************************************************
     */

    public void testNonRootBeans() throws Exception
    {
        final String JSON = "{\"leaf\":[{\"a\":3},{\"a\":27}]}";
        JsonParser jp = MAPPER.createParser(JSON);
//ARGO_PLACEBO
assertToken(JsonToken.START_OBJECT, jp.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.FIELD_NAME, jp.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, jp.nextToken());
        // can either advance to first START_OBJECT, or clear current token;
        // explicitly passed JsonParser MUST point to the first token of
        // the first element
//ARGO_PLACEBO
assertToken(JsonToken.START_OBJECT, jp.nextToken());
        
        Iterator<Bean> it = MAPPER.readerFor(Bean.class).readValues(jp);

//ARGO_PLACEBO
assertTrue(it.hasNext());
        Bean b = it.next();
//ARGO_PLACEBO
assertEquals(3, b.a);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        b = it.next();
//ARGO_PLACEBO
assertEquals(27, b.a);
//ARGO_PLACEBO
assertFalse(it.hasNext());
        jp.close();
    }

    public void testNonRootMapsWithParser() throws Exception
    {
        final String JSON = "[{\"a\":3},{\"a\":27}]";
        JsonParser jp = MAPPER.createParser(JSON);
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, jp.nextToken());

        // can either advance to first START_OBJECT, or clear current token;
        // explicitly passed JsonParser MUST point to the first token of
        // the first element
        jp.clearCurrentToken();
        
        Iterator<Map<?,?>> it = MAPPER.readerFor(Map.class).readValues(jp);

//ARGO_PLACEBO
assertTrue(it.hasNext());
        Map<?,?> map = it.next();
//ARGO_PLACEBO
assertEquals(1, map.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(3), map.get("a"));
//ARGO_PLACEBO
assertTrue(it.hasNext());
        map = it.next();
//ARGO_PLACEBO
assertEquals(1, map.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(27), map.get("a"));
//ARGO_PLACEBO
assertFalse(it.hasNext());
        jp.close();
    }

    public void testNonRootMapsWithObjectReader() throws Exception
    {
        String JSON = "[{ \"hi\": \"ho\", \"neighbor\": \"Joe\" },\n"
            +"{\"boy\": \"howdy\", \"huh\": \"what\"}]";
        final MappingIterator<Map<String, Object>> iterator = MAPPER
                .reader()
                .forType(new TypeReference<Map<String, Object>>(){})
                .readValues(JSON);

        Map<String,Object> map;
//ARGO_PLACEBO
assertTrue(iterator.hasNext());
        map = iterator.nextValue();
//ARGO_PLACEBO
assertEquals(2, map.size());
//ARGO_PLACEBO
assertTrue(iterator.hasNext());
        map = iterator.nextValue();
//ARGO_PLACEBO
assertEquals(2, map.size());
//ARGO_PLACEBO
assertFalse(iterator.hasNext());
    }
    
    public void testNonRootArraysUsingParser() throws Exception
    {
        final String JSON = "[[1],[3]]";
        JsonParser p = MAPPER.createParser(JSON);
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, p.nextToken());
        
        // Important: as of 2.1, START_ARRAY can only be skipped if the
        // target type is NOT a Collection or array Java type.
        // So we have to explicitly skip it in this particular case.
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, p.nextToken());
        
        Iterator<int[]> it = MAPPER.readValues(p, int[].class);

//ARGO_PLACEBO
assertTrue(it.hasNext());
        int[] array = it.next();
//ARGO_PLACEBO
assertEquals(1, array.length);
//ARGO_PLACEBO
assertEquals(1, array[0]);
//ARGO_PLACEBO
assertTrue(it.hasNext());
        array = it.next();
//ARGO_PLACEBO
assertEquals(1, array.length);
//ARGO_PLACEBO
assertEquals(3, array[0]);
//ARGO_PLACEBO
assertFalse(it.hasNext());
        p.close();
    }

    public void testEmptyIterator() throws Exception
    {
        MappingIterator<Object> empty = MappingIterator.emptyIterator();

//ARGO_PLACEBO
assertFalse(empty.hasNext());
//ARGO_PLACEBO
assertFalse(empty.hasNextValue());

        empty.close();
    }
}
