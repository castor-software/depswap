package com.fasterxml.jackson.databind.deser.jdk;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.testutil.NoCheckSubTypeValidator;

/**
 * Unit tests for verifying "raw" (or "untyped") data binding from JSON to JDK objects;
 * one that only uses core JDK types; wrappers, Maps and Lists.
 */
@SuppressWarnings("serial")
public class UntypedDeserializationTest
    extends BaseMapTest
{
    static class UCStringDeserializer
        extends StdScalarDeserializer<String>
    {
        public UCStringDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return p.getText().toUpperCase();
        }
    }

    static class CustomNumberDeserializer
        extends StdScalarDeserializer<Number>
    {
        protected final Integer value;
        
        public CustomNumberDeserializer(int nr) {
            super(Number.class);
            value = nr;
        }

        @Override
        public Number deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return value;
        }
    }

    // Let's make this Contextual, to tease out cyclic resolution issues, if any
    static class ListDeserializer extends StdDeserializer<List<Object>>
        implements ContextualDeserializer
    {
        public ListDeserializer() { super(List.class); }

        @Override
        public List<Object> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException
        {
            ArrayList<Object> list = new ArrayList<Object>();
            while (p.nextValue() != JsonToken.END_ARRAY) {
                list.add("X"+p.getText());
            }
            return list;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
                BeanProperty property) throws JsonMappingException
        {
            // For now, we just need to access "untyped" deserializer; not use it.
            
            /*JsonDeserializer<Object> ob = */
            ctxt.findContextualValueDeserializer(ctxt.constructType(Object.class), property);
            return this;
        }
    }

    static class YMapDeserializer extends StdDeserializer<Map<String,Object>>
    {
        public YMapDeserializer() { super(Map.class); }

        @Override
        public Map<String,Object> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException
        {
            Map<String,Object> map = new LinkedHashMap<String,Object>();
            while (p.nextValue() != JsonToken.END_OBJECT) {
                map.put(p.currentName(), "Y"+p.getText());
            }
            return map;
        }
    }

    static class DelegatingUntyped {
        protected Object value;
        
        @JsonCreator // delegating
        public DelegatingUntyped(Object v) {
            value = v;
        }
    }

    static class WrappedPolymorphicUntyped {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
        public Object value;

        protected WrappedPolymorphicUntyped() { }
        public WrappedPolymorphicUntyped(Object o) { value = o; }
    }

    static class WrappedUntyped1460 {
        public Object value;
    }

    // [databind#2115]
    static class SerContainer {
        public java.io.Serializable value;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();
    
    @SuppressWarnings("unchecked")
    public void testSampleDoc() throws Exception
    {
        final String JSON = SAMPLE_DOC_JSON_SPEC;

        /* To get "untyped" Mapping (to Maps, Lists, instead of beans etc),
         * we'll specify plain old Object.class as the target.
         */
        Object root = MAPPER.readValue(JSON, Object.class);

//ARGO_PLACEBO
assertType(root, Map.class);
        Map<?,?> rootMap = (Map<?,?>) root;
//ARGO_PLACEBO
assertEquals(1, rootMap.size());
        Map.Entry<?,?> rootEntry =  rootMap.entrySet().iterator().next();
//ARGO_PLACEBO
assertEquals("Image", rootEntry.getKey());
        Object image = rootEntry.getValue();
//ARGO_PLACEBO
assertType(image, Map.class);
        Map<?,?> imageMap = (Map<?,?>) image;
//ARGO_PLACEBO
assertEquals(5, imageMap.size());

        Object value = imageMap.get("Width");
//ARGO_PLACEBO
assertType(value, Integer.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(SAMPLE_SPEC_VALUE_WIDTH), value);

        value = imageMap.get("Height");
//ARGO_PLACEBO
assertType(value, Integer.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(SAMPLE_SPEC_VALUE_HEIGHT), value);

//ARGO_PLACEBO
assertEquals(SAMPLE_SPEC_VALUE_TITLE, imageMap.get("Title"));

        // Another Object, "thumbnail"
        value = imageMap.get("Thumbnail");
//ARGO_PLACEBO
assertType(value, Map.class);
        Map<?,?> tnMap = (Map<?,?>) value;
//ARGO_PLACEBO
assertEquals(3, tnMap.size());

//ARGO_PLACEBO
assertEquals(Integer.valueOf(SAMPLE_SPEC_VALUE_TN_HEIGHT), tnMap.get("Height"));
        // for some reason, width is textual, not numeric...
//ARGO_PLACEBO
assertEquals(SAMPLE_SPEC_VALUE_TN_WIDTH, tnMap.get("Width"));
//ARGO_PLACEBO
assertEquals(SAMPLE_SPEC_VALUE_TN_URL, tnMap.get("Url"));

        // And then number list, "IDs"
        value = imageMap.get("IDs");
//ARGO_PLACEBO
assertType(value, List.class);
        List<Object> ids = (List<Object>) value;
//ARGO_PLACEBO
assertEquals(4, ids.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(SAMPLE_SPEC_VALUE_TN_ID1), ids.get(0));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(SAMPLE_SPEC_VALUE_TN_ID2), ids.get(1));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(SAMPLE_SPEC_VALUE_TN_ID3), ids.get(2));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(SAMPLE_SPEC_VALUE_TN_ID4), ids.get(3));

        // and that's all folks!
    }

    @SuppressWarnings("unlikely-arg-type")
    public void testUntypedMap() throws Exception
    {
        // to get "untyped" default map-to-map, pass Object.class
        String JSON = "{ \"foo\" : \"bar\", \"crazy\" : true, \"null\" : null }";

        // Not a guaranteed cast theoretically, but will work:
        @SuppressWarnings("unchecked")
        Map<String,Object> result = (Map<String,Object>)MAPPER.readValue(JSON, Object.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertTrue(result instanceof Map<?,?>);

//ARGO_PLACEBO
assertEquals(3, result.size());

//ARGO_PLACEBO
assertEquals("bar", result.get("foo"));
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, result.get("crazy"));
//ARGO_PLACEBO
assertNull(result.get("null"));

        // Plus, non existing:
//ARGO_PLACEBO
assertNull(result.get("bar"));
//ARGO_PLACEBO
assertNull(result.get(3));
    }

    public void testSimpleVanillaScalars() throws IOException
    {
//ARGO_PLACEBO
assertEquals("foo", MAPPER.readValue(quote("foo"), Object.class));

//ARGO_PLACEBO
assertEquals(Boolean.TRUE, MAPPER.readValue(" true ", Object.class));

//ARGO_PLACEBO
assertEquals(Integer.valueOf(13), MAPPER.readValue("13 ", Object.class));
//ARGO_PLACEBO
assertEquals(Double.valueOf(0.5), MAPPER.readValue("0.5 ", Object.class));
    }

    public void testSimpleVanillaStructured() throws IOException
    {
        List<?> list = (List<?>) MAPPER.readValue("[ 1, 2, 3]", Object.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(1), list.get(0));
    }
    
    public void testNestedUntypes() throws IOException
    {
        // 05-Apr-2014, tatu: Odd failures if using shared mapper; so work around:
        Object root = MAPPER.readValue(aposToQuotes("{'a':3,'b':[1,2]}"),
                Object.class);
//ARGO_PLACEBO
assertTrue(root instanceof Map<?,?>);
        Map<?,?> map = (Map<?,?>) root;
//ARGO_PLACEBO
assertEquals(2, map.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(3), map.get("a"));
        Object ob = map.get("b");
//ARGO_PLACEBO
assertTrue(ob instanceof List<?>);
        List<?> l = (List<?>) ob;
//ARGO_PLACEBO
assertEquals(2, l.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(2), l.get(1));
    }

    public void testUntypedWithCustomScalarDesers() throws IOException
    {
        SimpleModule m = new SimpleModule("test-module");
        m.addDeserializer(String.class, new UCStringDeserializer());
        m.addDeserializer(Number.class, new CustomNumberDeserializer(13));
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(m);

        Object ob = mapper.readValue("{\"a\":\"b\", \"nr\":1 }", Object.class);
//ARGO_PLACEBO
assertTrue(ob instanceof Map);
        Object value = ((Map<?,?>) ob).get("a");
//ARGO_PLACEBO
assertNotNull(value);
//ARGO_PLACEBO
assertTrue(value instanceof String);
//ARGO_PLACEBO
assertEquals("B", value);

        value = ((Map<?,?>) ob).get("nr");
//ARGO_PLACEBO
assertNotNull(value);
//ARGO_PLACEBO
assertTrue(value instanceof Number);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(13), value);
    }

    // Test that exercises non-vanilla variant, with just one simple custom deserializer
    public void testNonVanilla() throws IOException
    {
        SimpleModule m = new SimpleModule("test-module");
        m.addDeserializer(String.class, new UCStringDeserializer());
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(m);

        // Also: since this is now non-vanilla variant, try more alternatives
        List<?> l = (List<?>) mapper.readValue("[ true, false, 7, 0.5, \"foo\"]", Object.class);
//ARGO_PLACEBO
assertEquals(5, l.size());
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, l.get(0));
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, l.get(1));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(7), l.get(2));
//ARGO_PLACEBO
assertEquals(Double.valueOf(0.5), l.get(3));
//ARGO_PLACEBO
assertEquals("FOO", l.get(4));

        l = (List<?>) mapper.readValue("[ {}, [] ]", Object.class);
//ARGO_PLACEBO
assertEquals(2, l.size());
//ARGO_PLACEBO
assertTrue(l.get(0) instanceof Map<?,?>);
//ARGO_PLACEBO
assertTrue(l.get(1) instanceof List<?>);
    }

    public void testUntypedWithListDeser() throws IOException
    {
        SimpleModule m = new SimpleModule("test-module");
        m.addDeserializer(List.class, new ListDeserializer());
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(m);

        // And then list...
        Object ob = mapper.readValue("[1, 2, true]", Object.class);
//ARGO_PLACEBO
assertTrue(ob instanceof List<?>);
        List<?> l = (List<?>) ob;
//ARGO_PLACEBO
assertEquals(3, l.size());
//ARGO_PLACEBO
assertEquals("X1", l.get(0));
//ARGO_PLACEBO
assertEquals("X2", l.get(1));
//ARGO_PLACEBO
assertEquals("Xtrue", l.get(2));
    }

    public void testUntypedWithMapDeser() throws IOException
    {
        SimpleModule m = new SimpleModule("test-module");
        m.addDeserializer(Map.class, new YMapDeserializer());
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(m);

        // And then list...
        Object ob = mapper.readValue("{\"a\":true}", Object.class);
//ARGO_PLACEBO
assertTrue(ob instanceof Map<?,?>);
        Map<?,?> map = (Map<?,?>) ob;
//ARGO_PLACEBO
assertEquals(1, map.size());
//ARGO_PLACEBO
assertEquals("Ytrue", map.get("a"));
    }

    public void testNestedUntyped989() throws IOException
    {
        DelegatingUntyped pojo;
        ObjectReader r = MAPPER.readerFor(DelegatingUntyped.class);

        pojo = r.readValue("[]");
//ARGO_PLACEBO
assertTrue(pojo.value instanceof List);
        pojo = r.readValue("[{}]");
//ARGO_PLACEBO
assertTrue(pojo.value instanceof List);
        
        pojo = r.readValue("{}");
//ARGO_PLACEBO
assertTrue(pojo.value instanceof Map);
        pojo = r.readValue("{\"a\":[]}");
//ARGO_PLACEBO
assertTrue(pojo.value instanceof Map);
    }

    public void testUntypedWithJsonArrays() throws Exception
    {
        // by default we get:
        Object ob = MAPPER.readValue("[1]", Object.class);
//ARGO_PLACEBO
assertTrue(ob instanceof List<?>);

        // but can change to produce Object[]:
        ob = MAPPER.readerFor(Object.class)
            .with(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)
            .readValue("[1]");
//ARGO_PLACEBO
assertEquals(Object[].class, ob.getClass());
    }

    public void testUntypedIntAsLong() throws Exception
    {
        final String JSON = aposToQuotes("{'value':3}");
        WrappedUntyped1460 w = MAPPER.readerFor(WrappedUntyped1460.class)
                .readValue(JSON);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(3), w.value);

        w = MAPPER.readerFor(WrappedUntyped1460.class)
                .with(DeserializationFeature.USE_LONG_FOR_INTS)
                .readValue(JSON);
//ARGO_PLACEBO
assertEquals(Long.valueOf(3), w.value);
    }

    // [databind#2115]: Consider `java.io.Serializable` as sort of alias of `java.lang.Object`
    // since all natural target types do implement `Serializable` so assignment works
    public void testSerializable() throws Exception
    {
        final String JSON1 = aposToQuotes("{ 'value' : 123 }");
        SerContainer cont = MAPPER.readValue(JSON1, SerContainer.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(123), cont.value);

        cont = MAPPER.readValue(aposToQuotes("{ 'value' : true }"), SerContainer.class);
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, cont.value);

        // But also via Map value, even key
        Map<?,?> map = MAPPER.readValue(JSON1, new TypeReference<Map<String, Serializable>>() { });
//ARGO_PLACEBO
assertEquals(1, map.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(123), map.get("value"));

        map = MAPPER.readValue(JSON1, new TypeReference<Map<Serializable, Object>>() { });
//ARGO_PLACEBO
assertEquals(1, map.size());
//ARGO_PLACEBO
assertEquals("value", map.keySet().iterator().next());
    }

    /*
    /**********************************************************
    /* Test methods, merging
    /**********************************************************
     */
    
    public void testValueUpdateVanillaUntyped() throws Exception
    {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 42);

        ObjectReader r = MAPPER.readerFor(Object.class).withValueToUpdate(map);
        Object result = r.readValue(aposToQuotes("{'b' : 57}"));
//ARGO_PLACEBO
assertSame(map, result);
//ARGO_PLACEBO
assertEquals(2, map.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(57), map.get("b"));

        // Try same with other types, too
        List<Object> list = new ArrayList<>();
        list.add(1);
        r = MAPPER.readerFor(Object.class).withValueToUpdate(list);
        result = r.readValue("[ 2, true ]");
//ARGO_PLACEBO
assertSame(list, result);
//ARGO_PLACEBO
assertEquals(3, list.size());
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, list.get(2));
    }

    public void testValueUpdateCustomUntyped() throws Exception
    {
        SimpleModule m = new SimpleModule("test-module")
                .addDeserializer(String.class, new UCStringDeserializer());
        final ObjectMapper customMapper = newJsonMapper()
            .registerModule(m);
        
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 42);

        ObjectReader r = customMapper.readerFor(Object.class).withValueToUpdate(map);
        Object result = r.readValue(aposToQuotes("{'b' : 'value', 'c' : 111222333444, 'enabled':true}"));
//ARGO_PLACEBO
assertSame(map, result);
//ARGO_PLACEBO
assertEquals(4, map.size());
//ARGO_PLACEBO
assertEquals("VALUE", map.get("b"));
//ARGO_PLACEBO
assertEquals(Long.valueOf(111222333444L), map.get("c"));
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, map.get("enabled"));

        // Try same with other types, too
        List<Object> list = new ArrayList<>();
        list.add(1);
        r = customMapper.readerFor(Object.class).withValueToUpdate(list);
        result = r.readValue(aposToQuotes("[ 2, 'foobar' ]"));
//ARGO_PLACEBO
assertSame(list, result);
//ARGO_PLACEBO
assertEquals(3, list.size());
//ARGO_PLACEBO
assertEquals("FOOBAR", list.get(2));
    }
    
    /*
    /**********************************************************
    /* Test methods, polymorphic
    /**********************************************************
     */

    // Allow 'upgrade' of big integers into Long, BigInteger
    public void testObjectSerializeWithLong() throws IOException
    {
        final ObjectMapper mapper = newJsonMapper();
        mapper.activateDefaultTyping(NoCheckSubTypeValidator.instance,
                DefaultTyping.JAVA_LANG_OBJECT, As.PROPERTY);
        final long VALUE = 1337800584532L;

        String serialized = "{\"timestamp\":"+VALUE+"}";
        // works fine as node
        JsonNode deserialized = mapper.readTree(serialized);
//ARGO_ORIGINAL
assertEquals(VALUE, deserialized.get("timestamp").asLong());
        // and actually should work in Maps too
        Map<?,?> deserMap = mapper.readValue(serialized, Map.class);
        Number n = (Number) deserMap.get("timestamp");
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORIGINAL
assertSame(Long.class, n.getClass());
//ARGO_ORIGINAL
assertEquals(Long.valueOf(VALUE), n);
    }

    public void testPolymorphicUntypedVanilla() throws IOException
    {
        ObjectReader rDefault = MAPPER.readerFor(WrappedPolymorphicUntyped.class);
        ObjectReader rAlt = rDefault
                .with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS,
                        DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
        WrappedPolymorphicUntyped w;

        w = rDefault.readValue(aposToQuotes("{'value':10}"));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(10), w.value);
        w = rAlt.readValue(aposToQuotes("{'value':10}"));
//ARGO_PLACEBO
assertEquals(BigInteger.TEN, w.value);

        w = rDefault.readValue(aposToQuotes("{'value':5.0}"));
//ARGO_PLACEBO
assertEquals(Double.valueOf(5.0), w.value);
        w = rAlt.readValue(aposToQuotes("{'value':5.0}"));
//ARGO_PLACEBO
assertEquals(new BigDecimal("5.0"), w.value);

        StringBuilder sb = new StringBuilder(100).append("[0");
        for (int i = 1; i < 100; ++i) {
            sb.append(", ").append(i);
        }
        sb.append("]");
        final String INT_ARRAY_JSON = sb.toString();

        // First read as-is, no type wrapping
        Object ob = MAPPER.readerFor(Object.class)
                .with(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)
                .readValue(INT_ARRAY_JSON);
//ARGO_PLACEBO
assertTrue(ob instanceof Object[]);
        Object[] obs = (Object[]) ob;
        for (int i = 0; i < 100; ++i) {
//ARGO_PLACEBO
assertEquals(Integer.valueOf(i), obs[i]);
        }
    }

    public void testPolymorphicUntypedCustom() throws IOException
    {
        // register module just to override one deserializer, to prevent use of Vanilla deser
        SimpleModule m = new SimpleModule("test-module")
                .addDeserializer(String.class, new UCStringDeserializer());
        final ObjectMapper customMapper = newJsonMapper()
            .registerModule(m);
        ObjectReader rDefault = customMapper.readerFor(WrappedPolymorphicUntyped.class);

        WrappedPolymorphicUntyped w = rDefault.readValue(aposToQuotes("{'value':10}"));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(10), w.value);

        w = rDefault.readValue(aposToQuotes("{'value':9988776655}"));
//ARGO_PLACEBO
assertEquals(Long.valueOf(9988776655L), w.value);
        w = rDefault.readValue(aposToQuotes("{'value':0.75}"));
//ARGO_PLACEBO
assertEquals(Double.valueOf(0.75), w.value);

        w = rDefault.readValue(aposToQuotes("{'value':'abc'}"));
//ARGO_PLACEBO
assertEquals("ABC", w.value);
        w = rDefault.readValue(aposToQuotes("{'value':false}"));
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, w.value);
        w = rDefault.readValue(aposToQuotes("{'value':null}"));
//ARGO_PLACEBO
assertNull(w.value);

        // but... actually how about real type id?
        final Object SHORT = Short.valueOf((short) 3);
        String json = customMapper.writeValueAsString(new WrappedPolymorphicUntyped(SHORT));

        WrappedPolymorphicUntyped result = rDefault.readValue(json);
//ARGO_PLACEBO
assertEquals(SHORT, result.value);
    }
}
