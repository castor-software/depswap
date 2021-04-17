package com.fasterxml.jackson.databind.deser.jdk;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

public class MapRelatedTypesDeserTest
    extends BaseMapTest
{
    private final ObjectMapper MAPPER = newJsonMapper();

    /*
    /**********************************************************
    /* Test methods, Map.Entry
    /**********************************************************
     */

    public void testMapEntrySimpleTypes() throws Exception
    {
        List<Map.Entry<String,Long>> stuff = MAPPER.readValue(aposToQuotes("[{'a':15},{'b':42}]"),
                new TypeReference<List<Map.Entry<String,Long>>>() { });
//ARGO_PLACEBO
assertNotNull(stuff);
//ARGO_PLACEBO
assertEquals(2, stuff.size());
//ARGO_PLACEBO
assertNotNull(stuff.get(1));
//ARGO_PLACEBO
assertEquals("b", stuff.get(1).getKey());
//ARGO_PLACEBO
assertEquals(Long.valueOf(42), stuff.get(1).getValue());
    }

    public void testMapEntryWithStringBean() throws Exception
    {
        List<Map.Entry<Integer,StringWrapper>> stuff = MAPPER.readValue(aposToQuotes("[{'28':'Foo'},{'13':'Bar'}]"),
                new TypeReference<List<Map.Entry<Integer,StringWrapper>>>() { });
//ARGO_PLACEBO
assertNotNull(stuff);
//ARGO_PLACEBO
assertEquals(2, stuff.size());
//ARGO_PLACEBO
assertNotNull(stuff.get(1));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(13), stuff.get(1).getKey());
        
        StringWrapper sw = stuff.get(1).getValue();
//ARGO_PLACEBO
assertEquals("Bar", sw.str);
    }

    public void testMapEntryFail() throws Exception
    {
        try {
            /*List<Map.Entry<Integer,StringWrapper>> stuff =*/ MAPPER.readValue(aposToQuotes("[{'28':'Foo','13':'Bar'}]"),
                    new TypeReference<List<Map.Entry<Integer,StringWrapper>>>() { });
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (Exception e) {
            verifyException(e, "more than one entry in JSON");
        }
    }

    /*
    /**********************************************************
    /* Test methods, other exotic Map types
    /**********************************************************
     */
    
    // [databind#810]
    public void testReadProperties() throws Exception
    {
        Properties props = MAPPER.readValue(aposToQuotes("{'a':'foo', 'b':123, 'c':true}"),
                Properties.class);
//ARGO_PLACEBO
assertEquals(3, props.size());
//ARGO_PLACEBO
assertEquals("foo", props.getProperty("a"));
//ARGO_PLACEBO
assertEquals("123", props.getProperty("b"));
//ARGO_PLACEBO
assertEquals("true", props.getProperty("c"));
    }

    // JDK singletonMap
    public void testSingletonMapRoundtrip() throws Exception
    {
        final TypeReference<Map<String,IntWrapper>> type = new TypeReference<Map<String,IntWrapper>>() { };

        String json = MAPPER.writeValueAsString(Collections.singletonMap("value", new IntWrapper(5)));
        Map<String,IntWrapper> result = MAPPER.readValue(json, type);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result.size());
        IntWrapper w = result.get("value");
//ARGO_PLACEBO
assertNotNull(w);
//ARGO_PLACEBO
assertEquals(5, w.i);
    }
}
