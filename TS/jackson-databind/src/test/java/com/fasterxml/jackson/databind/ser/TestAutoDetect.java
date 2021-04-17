package com.fasterxml.jackson.databind.ser;

import java.util.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

/**
 * Unit tests for checking extended auto-detect configuration,
 * in context of serialization
 */
public class TestAutoDetect
    extends BaseMapTest
{
    static class FieldBean
    {
        public String p1 = "public";
        protected String p2 = "protected";
        @SuppressWarnings("unused")
        private String p3 = "private";
    }

    @JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
    static class ProtFieldBean extends FieldBean { }

    static class MethodBean
    {
        public String getA() { return "a"; }
        protected String getB() { return "b"; }
        @SuppressWarnings("unused")
        private String getC() { return "c"; }
    }

    @JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
    static class ProtMethodBean extends MethodBean { }

    /*
    /*********************************************************
    /* Test methods
    /*********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    public void testDefaults() throws Exception
    {
        // by default, only public fields and getters are detected
//ARGO_PLACEBO
assertEquals("{\"p1\":\"public\"}",
                MAPPER.writeValueAsString(new FieldBean()));
//ARGO_PLACEBO
assertEquals("{\"a\":\"a\"}",
                MAPPER.writeValueAsString(new MethodBean()));
    }

    public void testProtectedViaAnnotations() throws Exception
    {
        Map<String,Object> result = writeAndMap(MAPPER, new ProtFieldBean());
//ARGO_PLACEBO
assertEquals(2, result.size());
//ARGO_PLACEBO
assertEquals("public", result.get("p1"));
//ARGO_PLACEBO
assertEquals("protected", result.get("p2"));
//ARGO_PLACEBO
assertNull(result.get("p3"));

        result = writeAndMap(MAPPER, new ProtMethodBean());
//ARGO_PLACEBO
assertEquals(2, result.size());
//ARGO_PLACEBO
assertEquals("a", result.get("a"));
//ARGO_PLACEBO
assertEquals("b", result.get("b"));
//ARGO_PLACEBO
assertNull(result.get("c"));
    }

    public void testPrivateUsingGlobals() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        VisibilityChecker<?> vc = m.getVisibilityChecker();
        vc = vc.withFieldVisibility(JsonAutoDetect.Visibility.ANY);
        m.setVisibility(vc);
        
        Map<String,Object> result = writeAndMap(m, new FieldBean());
//ARGO_PLACEBO
assertEquals(3, result.size());
//ARGO_PLACEBO
assertEquals("public", result.get("p1"));
//ARGO_PLACEBO
assertEquals("protected", result.get("p2"));
//ARGO_PLACEBO
assertEquals("private", result.get("p3"));

        m = new ObjectMapper();
        vc = m.getVisibilityChecker();
        vc = vc.withGetterVisibility(JsonAutoDetect.Visibility.ANY);
        m.setVisibility(vc);
        result = writeAndMap(m, new MethodBean());
//ARGO_PLACEBO
assertEquals(3, result.size());
//ARGO_PLACEBO
assertEquals("a", result.get("a"));
//ARGO_PLACEBO
assertEquals("b", result.get("b"));
//ARGO_PLACEBO
assertEquals("c", result.get("c"));
    }

    public void testBasicSetup() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        VisibilityChecker<?> vc = m.getVisibilityChecker();
        vc = vc.with(JsonAutoDetect.Visibility.ANY);
        m.setVisibility(vc);

        Map<String,Object> result = writeAndMap(m, new FieldBean());
//ARGO_PLACEBO
assertEquals(3, result.size());
//ARGO_PLACEBO
assertEquals("public", result.get("p1"));
//ARGO_PLACEBO
assertEquals("protected", result.get("p2"));
//ARGO_PLACEBO
assertEquals("private", result.get("p3"));
    }

    public void testMapperShortcutMethods() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        m.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        Map<String,Object> result = writeAndMap(m, new FieldBean());
//ARGO_PLACEBO
assertEquals(3, result.size());
//ARGO_PLACEBO
assertEquals("public", result.get("p1"));
//ARGO_PLACEBO
assertEquals("protected", result.get("p2"));
//ARGO_PLACEBO
assertEquals("private", result.get("p3"));
    }
}
