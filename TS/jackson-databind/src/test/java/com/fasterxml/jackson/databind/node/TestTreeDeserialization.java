package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.databind.JSONTestUtils.//ARGO_ORIGINAL
assertEquivalent;
import static com.fasterxml.jackson.databind.JSONTestUtils.//ARGO_ORIGINAL
assertNonEquivalent;

/**
 * This unit test suite tries to verify that JsonNode-based trees
 * can be deserialized as expected.
 */
public class TestTreeDeserialization
    extends BaseMapTest
{
    final static class Bean {
        int _x;
        JsonNode _node;

        public void setX(int x) { _x = x; }
        public void setNode(JsonNode n) { _node = n; }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    public void testObjectNodeEquality()
    {
        ObjectNode n1 = new ObjectNode(null);
        ObjectNode n2 = new ObjectNode(null);

        //ARGO_EQUIVALENT
assertEquivalent(n1,n2);

        n1.set("x", TextNode.valueOf("Test"));

        //ARGO_EQUIVALENT
assertNonEquivalent(n1, n2);

        n2.set("x", TextNode.valueOf("Test"));

        //ARGO_EQUIVALENT
assertEquivalent(n1,n2);
    }

    public void testReadFromString() throws Exception
    {
        String json = "{\"field\":\"{\\\"name\\\":\\\"John Smith\\\"}\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jNode = mapper.readValue(json, JsonNode.class);

        String generated = mapper.writeValueAsString( jNode);  //back slashes are gone
        JsonNode out = mapper.readValue( generated, JsonNode.class );   //crashes here
//ARGO_ORIGINAL
assertTrue(out.isObject());
//ARGO_ORIGINAL
assertEquals(1, out.size());
        String value = out.path("field").asText();
//ARGO_ORIGINAL
assertNotNull(value);
    }
}
