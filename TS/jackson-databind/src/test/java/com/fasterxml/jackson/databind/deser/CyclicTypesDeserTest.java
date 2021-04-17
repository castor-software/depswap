package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.*;

/**
 * Simple unit tests to verify that it is possible to handle
 * potentially cyclic structures, as long as object graph itself
 * is not cyclic. This is the case for directed hierarchies like
 * trees and DAGs.
 */
public class CyclicTypesDeserTest
    extends BaseMapTest
{
    static class Bean
    {
        Bean _next;
        String _name;

        public Bean() { }

        public void setNext(Bean b) { _next = b; }
        public void setName(String n) { _name = n; }

    }

    static class LinkA {
        public LinkB next;
    }

    static class LinkB {
        protected LinkA a;

        public void setA(LinkA a) { this.a = a; }
        public LinkA getA() { return a; }
    }

    static class GenericLink<T> {
        public GenericLink<T> next;
    }

    static class StringLink extends GenericLink<String> {
    }

    @JsonPropertyOrder({ "id", "parent" })
    static class Selfie405 {
        public int id;

        @JsonIgnoreProperties({ "parent" })
        public Selfie405 parent;
        
        public Selfie405(int id) { this.id = id; }
    }
    
    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();
    
    public void testLinked() throws Exception
    {
        Bean first = MAPPER.readValue
            ("{\"name\":\"first\", \"next\": { "
             +" \"name\":\"last\", \"next\" : null }}",
             Bean.class);

//ARGO_PLACEBO
assertNotNull(first);
//ARGO_PLACEBO
assertEquals("first", first._name);
        Bean last = first._next;
//ARGO_PLACEBO
assertNotNull(last);
//ARGO_PLACEBO
assertEquals("last", last._name);
//ARGO_PLACEBO
assertNull(last._next);
    }

    public void testLinkedGeneric() throws Exception
    {
        StringLink link = MAPPER.readValue("{\"next\":null}", StringLink.class);
//ARGO_PLACEBO
assertNotNull(link);
//ARGO_PLACEBO
assertNull(link.next);
    }

    public void testCycleWith2Classes() throws Exception
    {
        LinkA a = MAPPER.readValue("{\"next\":{\"a\":null}}", LinkA.class);
//ARGO_PLACEBO
assertNotNull(a.next);
        LinkB b = a.next;
//ARGO_PLACEBO
assertNull(b.a);
    }

    // [Issue#405]: Should be possible to ignore cyclic ref
    public void testIgnoredCycle() throws Exception
    {
        Selfie405 self1 = new Selfie405(1);
        self1.parent = self1;

        // First: exception with default settings:
//ARGO_PLACEBO
assertTrue(MAPPER.isEnabled(SerializationFeature.FAIL_ON_SELF_REFERENCES));
        try {
            MAPPER.writeValueAsString(self1);
//ARGO_PLACEBO
fail("Should fail with direct self-ref");
        } catch (JsonMappingException e) {
            verifyException(e, "Direct self-reference");
        }
        
        ObjectWriter w = MAPPER.writer()
                .without(SerializationFeature.FAIL_ON_SELF_REFERENCES);
        String json = w.writeValueAsString(self1);
//ARGO_PLACEBO
assertNotNull(json);
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'id':1,'parent':{'id':1}}"), json);
    }
}
