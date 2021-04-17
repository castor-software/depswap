package com.fasterxml.jackson.databind.ser;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.*;

/**
 * Unit tests for checking handling of some of {@link MapperFeature}s
 * and {@link SerializationFeature}s for serialization.
 */
public class SerializationFeaturesTest
    extends BaseMapTest
{
    static class CloseableBean implements Closeable
    {
        public int a = 3;

        protected boolean wasClosed = false;

        @Override
        public void close() throws IOException {
            wasClosed = true;
        }
    }

    private static class StringListBean {
        @SuppressWarnings("unused")
        public Collection<String> values;
        
        public StringListBean(Collection<String> v) { values = v; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    // Test for [JACKSON-282]
    @SuppressWarnings("resource")
    public void testCloseCloseable() throws IOException
    {
        ObjectMapper m = new ObjectMapper();
        // default should be disabled:
        CloseableBean bean = new CloseableBean();
        m.writeValueAsString(bean);
//ARGO_PLACEBO
assertFalse(bean.wasClosed);

        // but can enable it:
        m.configure(SerializationFeature.CLOSE_CLOSEABLE, true);
        bean = new CloseableBean();
        m.writeValueAsString(bean);
//ARGO_PLACEBO
assertTrue(bean.wasClosed);

        // also: let's ensure that ObjectWriter won't interfere with it
        bean = new CloseableBean();
        m.writerFor(CloseableBean.class).writeValueAsString(bean);
//ARGO_PLACEBO
assertTrue(bean.wasClosed);
    }

    // Test for [JACKSON-289]
    public void testCharArrays() throws IOException
    {
        char[] chars = new char[] { 'a','b','c' };
        ObjectMapper m = new ObjectMapper();
        // default: serialize as Strings
//ARGO_PLACEBO
assertEquals(quote("abc"), m.writeValueAsString(chars));
        
        // new feature: serialize as JSON array:
        m.configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true);
//ARGO_PLACEBO
assertEquals("[\"a\",\"b\",\"c\"]", m.writeValueAsString(chars));
    }

    // Test for [JACKSON-401]
    public void testFlushingAutomatic() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
//ARGO_PLACEBO
assertTrue(mapper.getSerializationConfig().isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE));
        // default is to flush after writeValue()
        StringWriter sw = new StringWriter();
        JsonGenerator g = mapper.createGenerator(sw);
        mapper.writeValue(g, Integer.valueOf(13));
//ARGO_PLACEBO
assertEquals("13", sw.toString());
        g.close();

        // ditto with ObjectWriter
        sw = new StringWriter();
        g = mapper.createGenerator(sw);
        ObjectWriter ow = mapper.writer();
        ow.writeValue(g, Integer.valueOf(99));
//ARGO_PLACEBO
assertEquals("99", sw.toString());
        g.close();
    }

    public void testFlushingNotAutomatic() throws IOException
    {
        // but should not occur if configured otherwise
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, false);
        StringWriter sw = new StringWriter();
        JsonGenerator g = mapper.createGenerator(sw);

        mapper.writeValue(g, Integer.valueOf(13));
        // no flushing now:
//ARGO_PLACEBO
assertEquals("", sw.toString());
        // except when actually flushing
        g.flush();
//ARGO_PLACEBO
assertEquals("13", sw.toString());
        g.close();
        // Also, same should happen with ObjectWriter
        sw = new StringWriter();
        g = mapper.createGenerator(sw);
        ObjectWriter ow = mapper.writer();
        ow.writeValue(g, Integer.valueOf(99));
//ARGO_PLACEBO
assertEquals("", sw.toString());
        // except when actually flushing
        g.flush();
//ARGO_PLACEBO
assertEquals("99", sw.toString());
        g.close();
    }

    public void testSingleElementCollections() throws IOException
    {
        final ObjectWriter writer = objectWriter().with(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);

        // Lists:
        ArrayList<String> strs = new ArrayList<String>();
        strs.add("xyz");
//ARGO_PLACEBO
assertEquals(quote("xyz"), writer.writeValueAsString(strs));
        ArrayList<Integer> ints = new ArrayList<Integer>();
        ints.add(13);
//ARGO_PLACEBO
assertEquals("13", writer.writeValueAsString(ints));

        // other Collections, like Sets:
        HashSet<Long> longs = new HashSet<Long>();
        longs.add(42L);
//ARGO_PLACEBO
assertEquals("42", writer.writeValueAsString(longs));
        // [databind#180]
        final String EXP_STRINGS = "{\"values\":\"foo\"}";
//ARGO_PLACEBO
assertEquals(EXP_STRINGS, writer.writeValueAsString(new StringListBean(Collections.singletonList("foo"))));

        final Set<String> SET = new HashSet<String>();
        SET.add("foo");
//ARGO_PLACEBO
assertEquals(EXP_STRINGS, writer.writeValueAsString(new StringListBean(SET)));
        
        // arrays:
//ARGO_PLACEBO
assertEquals("true", writer.writeValueAsString(new boolean[] { true }));
//ARGO_PLACEBO
assertEquals("[true,false]", writer.writeValueAsString(new boolean[] { true, false }));
//ARGO_PLACEBO
assertEquals("true", writer.writeValueAsString(new Boolean[] { Boolean.TRUE }));

//ARGO_PLACEBO
assertEquals("3", writer.writeValueAsString(new short[] { 3 }));
//ARGO_PLACEBO
assertEquals("[3,2]", writer.writeValueAsString(new short[] { 3, 2 }));
        
//ARGO_PLACEBO
assertEquals("3", writer.writeValueAsString(new int[] { 3 }));
//ARGO_PLACEBO
assertEquals("[3,2]", writer.writeValueAsString(new int[] { 3, 2 }));

//ARGO_PLACEBO
assertEquals("1", writer.writeValueAsString(new long[] { 1L }));
//ARGO_PLACEBO
assertEquals("[-1,4]", writer.writeValueAsString(new long[] { -1L, 4L }));

//ARGO_PLACEBO
assertEquals("0.5", writer.writeValueAsString(new double[] { 0.5 }));
//ARGO_PLACEBO
assertEquals("[0.5,2.5]", writer.writeValueAsString(new double[] { 0.5, 2.5 }));

//ARGO_PLACEBO
assertEquals("0.5", writer.writeValueAsString(new float[] { 0.5f }));
//ARGO_PLACEBO
assertEquals("[0.5,2.5]", writer.writeValueAsString(new float[] { 0.5f, 2.5f }));
        
//ARGO_PLACEBO
assertEquals(quote("foo"), writer.writeValueAsString(new String[] { "foo" }));
    }
}
