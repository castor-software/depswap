package com.fasterxml.jackson.databind.jsontype.deftyping;

import java.util.*;

import static org.junit.Assert.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.testutil.NoCheckSubTypeValidator;

/**
 * Unit tests to verify that Java/JSON scalar values (non-structured values)
 * are handled properly with respect to additional type information.
 */
public class TestDefaultForScalars
    extends BaseMapTest
{
    static class Jackson417Bean {
        public String foo = "bar";
        public java.io.Serializable bar = Integer.valueOf(13);
    }

    // [databind#1395]: prevent attempts at including type info for primitives
    static class Data {
        public long key;
    }

    // Basic `ObjectWrapper` from base uses delegating ctor, won't work well; should
    // figure out why, but until then we'll use separate impl
    protected static class ObjectWrapperForPoly {
        Object object;

        protected ObjectWrapperForPoly() { }
        public ObjectWrapperForPoly(final Object o) {
            object = o;
        }
        public Object getObject() { return object; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper DEFAULT_TYPING_MAPPER = jsonMapperBuilder()
        .activateDefaultTyping(NoCheckSubTypeValidator.instance)
        .build();

    /**
     * Unit test to verify that limited number of core types do NOT include
     * type information, even if declared as Object. This is only done for types
     * that JSON scalar values natively map to: String, Integer and Boolean (and
     * nulls never have type information)
     */
    public void testNumericScalars() throws Exception
    {
        // no typing for Integer, Double, yes for others
//ARGO_PLACEBO
assertEquals("[123]", DEFAULT_TYPING_MAPPER.writeValueAsString(new Object[] { Integer.valueOf(123) }));
//ARGO_PLACEBO
assertEquals("[[\"java.lang.Long\",37]]", DEFAULT_TYPING_MAPPER.writeValueAsString(new Object[] { Long.valueOf(37) }));
//ARGO_PLACEBO
assertEquals("[0.25]", DEFAULT_TYPING_MAPPER.writeValueAsString(new Object[] { Double.valueOf(0.25) }));
//ARGO_PLACEBO
assertEquals("[[\"java.lang.Float\",0.5]]", DEFAULT_TYPING_MAPPER.writeValueAsString(new Object[] { Float.valueOf(0.5f) }));
    }

    public void testDateScalars() throws Exception
    {
        long ts = 12345678L;
//ARGO_PLACEBO
assertEquals("[[\"java.util.Date\","+ts+"]]",
                DEFAULT_TYPING_MAPPER.writeValueAsString(new Object[] { new Date(ts) }));

        // Calendar is trickier... hmmh. Need to ensure round-tripping
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        String json = DEFAULT_TYPING_MAPPER.writeValueAsString(new Object[] { c });
//ARGO_PLACEBO
assertEquals("[[\""+c.getClass().getName()+"\","+ts+"]]", json);
        // and let's make sure it also comes back same way:
        Object[] result = DEFAULT_TYPING_MAPPER.readValue(json, Object[].class);
//ARGO_PLACEBO
assertEquals(1, result.length);
//ARGO_PLACEBO
assertTrue(result[0] instanceof Calendar);
//ARGO_PLACEBO
assertEquals(ts, ((Calendar) result[0]).getTimeInMillis());
    }

    public void testMiscScalars() throws Exception
    {
        // no typing for Strings, booleans
//ARGO_PLACEBO
assertEquals("[\"abc\"]", DEFAULT_TYPING_MAPPER.writeValueAsString(new Object[] { "abc" }));
//ARGO_PLACEBO
assertEquals("[true,null,false]", DEFAULT_TYPING_MAPPER.writeValueAsString(new Boolean[] { true, null, false }));
    }

    /**
     * Test for verifying that contents of "untyped" homogenous arrays are properly
     * handled,
     */
    public void testScalarArrays() throws Exception
    {
        ObjectMapper m = jsonMapperBuilder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT)
                .build();
        Object[] input = new Object[] {
                "abc", new Date(1234567), null, Integer.valueOf(456)
        };
        String json = m.writeValueAsString(input);
//ARGO_PLACEBO
assertEquals("[\"abc\",[\"java.util.Date\",1234567],null,456]", json);

        // and should deserialize back as well:
        Object[] output = m.readValue(json, Object[].class);
//ARGO_PLACEBO
assertArrayEquals(input, output);
    }

    public void test417() throws Exception
    {
        Jackson417Bean input = new Jackson417Bean();
        String json = DEFAULT_TYPING_MAPPER.writeValueAsString(input);
        Jackson417Bean result = DEFAULT_TYPING_MAPPER.readValue(json, Jackson417Bean.class);
//ARGO_PLACEBO
assertEquals(input.foo, result.foo);
//ARGO_PLACEBO
assertEquals(input.bar, result.bar);
    }

    // [databind#1395]: prevent attempts at including type info for primitives
    public void testDefaultTypingWithLong() throws Exception
    {
        Data data = new Data();
        data.key = 1L;
        Map<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("longInMap", 2L);
        mapData.put("longAsField", data);

        // Configure Jackson to preserve types
        StdTypeResolverBuilder resolver = new StdTypeResolverBuilder();
        resolver.init(JsonTypeInfo.Id.CLASS, null);
        resolver.inclusion(JsonTypeInfo.As.PROPERTY);
        resolver.typeProperty("__t");
        ObjectMapper mapper = jsonMapperBuilder()
                .setDefaultTyping(resolver)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        // Serialize
        String json = mapper.writeValueAsString(mapData);

        // Deserialize
        Map<?,?> result = mapper.readValue(json, Map.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(2, result.size());
    }

    // [databind#2236]: do need type info for NaN
    public void testDefaultTypingWithNaN() throws Exception
    {
        final ObjectWrapperForPoly INPUT = new ObjectWrapperForPoly(Double.POSITIVE_INFINITY);
        final String json = DEFAULT_TYPING_MAPPER.writeValueAsString(INPUT);
        final ObjectWrapperForPoly result = DEFAULT_TYPING_MAPPER.readValue(json, ObjectWrapperForPoly.class);
//ARGO_PLACEBO
assertEquals(Double.class, result.getObject().getClass());
//ARGO_PLACEBO
assertEquals(INPUT.getObject().toString(), result.getObject().toString());
//ARGO_PLACEBO
assertTrue(((Double) result.getObject()).isInfinite());
    }
}
