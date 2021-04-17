package com.fasterxml.jackson.databind.deser.filter;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

/**
 * Unit tests for checking handling of unknown properties
 */
public class TestUnknownPropertyDeserialization
    extends BaseMapTest
{
    final static String JSON_UNKNOWN_FIELD = "{ \"a\" : 1, \"foo\" : [ 1, 2, 3], \"b\" : -1 }";

    /*
    /**********************************************************
    /* Helper classes
    /**********************************************************
     */

    final static class TestBean
    {
        String _unknown;

        int _a, _b;

        public TestBean() { }

        public void setA(int a) { _a = a; }
        public void setB(int b) { _b = b; }

        public void markUnknown(String unk) { _unknown = unk; }
    }

    /**
     * Simple {@link DeserializationProblemHandler} sub-class that
     * just marks unknown property/ies when encountered, along with
     * Json value of the property.
     */
    static class MyHandler
        extends DeserializationProblemHandler
    {
        @Override
        public boolean handleUnknownProperty(DeserializationContext ctxt,
                JsonParser jp, JsonDeserializer<?> deserializer,
                Object bean, String propertyName)
            throws IOException, JsonProcessingException
        {
            // very simple, just to verify that we do see correct token type
            ((TestBean) bean).markUnknown(propertyName+":"+jp.currentToken().toString());
            // Yup, we are good to go; must skip whatever value we'd have:
            jp.skipChildren();
            return true;
        }
    }

    @JsonIgnoreProperties({"b", "c"})
    static class IgnoreSome
    {
        public int a, b;
        private String c, d;

        public IgnoreSome() { }

        public String c() { return c; }
        public void setC(String value) { c = value; }
        public String d() { return d; }
        public void setD(String value) { d = value; }
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    static class IgnoreUnknown {
        public int a;
    }

    @SuppressWarnings("serial")
    @JsonIgnoreProperties({"a", "d"})
    static class IgnoreMap extends HashMap<String,Object> { }

    static class ImplicitIgnores {
        @JsonIgnore public int a;
        @JsonIgnore public void setB(int b) { }
        public int c;
    }

    // // Ignored as per [JACKSON-787]

    static class XYZWrapper1 {
        @JsonIgnoreProperties({"x"})
        public YZ value;
    }

    static class YZ {
        public int y, z;
    }

    static class XYZWrapper2 {
        @JsonIgnoreProperties({"y"})
        public X value;
    }

    @JsonIgnoreProperties({"z"})
    static class X {
        public int x;
    }

    static class MapWithoutX {
        @JsonIgnoreProperties("x")
        public Map<String,Integer> values;
    }

    // [databind#987]
    static class Bean987 {
        public String aProperty;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    /**
     * By default we should just get an exception if an unknown property
     * is encountered
     */
    public void testUnknownHandlingDefault() throws Exception
    {
        try {
            MAPPER.readValue(new StringReader(JSON_UNKNOWN_FIELD), TestBean.class);
        } catch (JsonMappingException jex) {
            verifyException(jex, "Unrecognized field \"foo\"");
        }
    }

    /**
     * Test that verifies that it is possible to ignore unknown properties using
     * {@link DeserializationProblemHandler}.
     */
    public void testUnknownHandlingIgnoreWithHandler() throws Exception
    {
        ObjectMapper mapper = newJsonMapper();
        mapper.clearProblemHandlers();
        mapper.addHandler(new MyHandler());
        TestBean result = mapper.readValue(new StringReader(JSON_UNKNOWN_FIELD), TestBean.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result._a);
//ARGO_PLACEBO
assertEquals(-1, result._b);
//ARGO_PLACEBO
assertEquals("foo:START_ARRAY", result._unknown);
    }

    /**
     * Test that verifies that it is possible to ignore unknown properties using
     * {@link DeserializationProblemHandler} and an ObjectReader.
     */
    public void testUnknownHandlingIgnoreWithHandlerAndObjectReader() throws Exception
    {
        ObjectMapper mapper = newJsonMapper();
        mapper.clearProblemHandlers();
        TestBean result = mapper.readerFor(TestBean.class).withHandler(new MyHandler()).readValue(new StringReader(JSON_UNKNOWN_FIELD));
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result._a);
//ARGO_PLACEBO
assertEquals(-1, result._b);
//ARGO_PLACEBO
assertEquals("foo:START_ARRAY", result._unknown);
    }

    /**
     * Test for checking that it is also possible to simply suppress
     * error reporting for unknown properties.
     */
    public void testUnknownHandlingIgnoreWithFeature() throws Exception
    {
        ObjectMapper mapper = newJsonMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TestBean result = null;
        try {
            result = mapper.readValue(new StringReader(JSON_UNKNOWN_FIELD), TestBean.class);
        } catch (JsonMappingException jex) {
//ARGO_PLACEBO
fail("Did not expect a problem, got: "+jex.getMessage());
        }
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result._a);
//ARGO_PLACEBO
assertNull(result._unknown);
//ARGO_PLACEBO
assertEquals(-1, result._b);
    }

    public void testWithClassIgnore() throws Exception
    {
        IgnoreSome result = MAPPER.readValue("{ \"a\":1,\"b\":2,\"c\":\"x\",\"d\":\"y\"}",
                IgnoreSome.class);
        // first: should deserialize 2 of properties normally
//ARGO_PLACEBO
assertEquals(1, result.a);
//ARGO_PLACEBO
assertEquals("y", result.d());
        // and not take other 2
//ARGO_PLACEBO
assertEquals(0, result.b);
//ARGO_PLACEBO
assertNull(result.c());
    }

    /// @since 1.4
    public void testClassIgnoreWithMap() throws Exception
    {
        // Let's actually use incompatible types for "a" and "d"; should not matter when ignored
        IgnoreMap result = MAPPER.readValue
            ("{ \"a\":[ 1],\n"
                +"\"b\":2,\n"
                +"\"c\": \"x\",\n"
                +"\"d\":false }", IgnoreMap.class);
//ARGO_PLACEBO
assertEquals(2, result.size());
        Object ob = result.get("b");
//ARGO_PLACEBO
assertEquals(Integer.class, ob.getClass());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(2), ob);
//ARGO_PLACEBO
assertEquals("x", result.get("c"));
//ARGO_PLACEBO
assertFalse(result.containsKey("a"));
//ARGO_PLACEBO
assertFalse(result.containsKey("d"));
    }

    public void testClassWithIgnoreUnknown() throws Exception
    {
        IgnoreUnknown result = MAPPER.readValue
            ("{\"b\":3,\"c\":[1,2],\"x\":{ },\"a\":-3}", IgnoreUnknown.class);
//ARGO_PLACEBO
assertEquals(-3, result.a);
    }

    /**
     * Test that verifies that use of {@link JsonIgnore} will add implicit
     * skipping of matching properties.
     */
    public void testClassWithUnknownAndIgnore() throws Exception
    {
        // should be ok: "a" and "b" ignored, "c" mapped:
        ImplicitIgnores result = MAPPER.readValue
            ("{\"a\":1,\"b\":2,\"c\":3 }", ImplicitIgnores.class);
//ARGO_PLACEBO
assertEquals(3, result.c);

        // but "d" is not defined, so should still error
        try {
            MAPPER.readValue("{\"a\":1,\"b\":2,\"c\":3,\"d\":4 }", ImplicitIgnores.class);            
        } catch (JsonMappingException e) {
            verifyException(e, "Unrecognized field \"d\"");
        }
    }

    public void testPropertyIgnoral() throws Exception
    {
        XYZWrapper1 result = MAPPER.readValue("{\"value\":{\"y\":2,\"x\":1,\"z\":3}}", XYZWrapper1.class);
//ARGO_PLACEBO
assertEquals(2, result.value.y);
//ARGO_PLACEBO
assertEquals(3, result.value.z);
    }

    public void testPropertyIgnoralWithClass() throws Exception
    {
        XYZWrapper2 result = MAPPER.readValue("{\"value\":{\"y\":2,\"x\":1,\"z\":3}}",
                XYZWrapper2.class);
//ARGO_PLACEBO
assertEquals(1, result.value.x);
    }

    public void testPropertyIgnoralForMap() throws Exception
    {
        MapWithoutX result = MAPPER.readValue("{\"values\":{\"x\":1,\"y\":2}}", MapWithoutX.class);
//ARGO_PLACEBO
assertNotNull(result.values);
//ARGO_PLACEBO
assertEquals(1, result.values.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(2), result.values.get("y"));
    }

    public void testIssue987() throws Exception
    {
        ObjectMapper jsonMapper = newJsonMapper();
        jsonMapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException, JsonProcessingException {
                p.skipChildren();
                return true;
            }
        });

        String input = "[{\"aProperty\":\"x\",\"unknown\":{\"unknown\":{}}}]";
        List<Bean987> deserializedList = jsonMapper.readValue(input,
                new TypeReference<List<Bean987>>() { });
//ARGO_PLACEBO
assertEquals(1, deserializedList.size());
    }
}
