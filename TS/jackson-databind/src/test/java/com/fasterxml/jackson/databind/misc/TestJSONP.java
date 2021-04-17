package com.fasterxml.jackson.databind.misc;

import java.util.Arrays;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;

public class TestJSONP
    extends BaseMapTest
{
    static class Base {
        public String a;
    }
    static class Impl extends Base {
        public String b;

        public Impl(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testSimpleScalars() throws Exception
    {
//ARGO_PLACEBO
assertEquals("callback(\"abc\")",
                MAPPER.writeValueAsString(new JSONPObject("callback", "abc")));
//ARGO_PLACEBO
assertEquals("calc(123)",
                MAPPER.writeValueAsString(new JSONPObject("calc", Integer.valueOf(123))));
//ARGO_PLACEBO
assertEquals("dummy(null)",
                MAPPER.writeValueAsString(new JSONPObject("dummy", null)));
    }

    public void testSimpleBean() throws Exception
    {
//ARGO_PLACEBO
assertEquals("xxx({\"a\":\"123\",\"b\":\"456\"})",
                MAPPER.writeValueAsString(new JSONPObject("xxx",
                        new Impl("123", "456"))));
    }
    
    /**
     * Test to ensure that it is possible to force a static type for wrapped
     * value.
     */
    public void testWithType() throws Exception
    {
        Object ob = new Impl("abc", "def");
        JavaType type = MAPPER.constructType(Base.class);
//ARGO_PLACEBO
assertEquals("do({\"a\":\"abc\"})",
                MAPPER.writeValueAsString(new JSONPObject("do", ob, type)));
    }

    public void testGeneralWrapping() throws Exception
    {
        JSONWrappedObject input = new JSONWrappedObject("/*Foo*/", "\n// the end",
                Arrays.asList());
//ARGO_PLACEBO
assertEquals("/*Foo*/[]\n// the end", MAPPER.writeValueAsString(input));
    }
}
