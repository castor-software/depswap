package com.fasterxml.jackson.databind.node;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.TestTreeDeserialization.Bean;

/**
 * This unit test suite tries to verify that ObjectMapper
 * can properly parse JSON and bind contents into appropriate
 * JsonNode instances.
 */
public class TreeReadViaMapperTest extends BaseMapTest
{
    private final ObjectMapper MAPPER = objectMapper();

    public void testSimple() throws Exception
    {
        final String JSON = SAMPLE_DOC_JSON_SPEC;

        for (int type = 0; type < 2; ++type) {
            JsonNode result;

            if (type == 0) {
                result = MAPPER.readTree(new StringReader(JSON));
            } else {
                result = MAPPER.readTree(JSON);
            }

//ARGO_ORIGINAL
assertType(result, ObjectNode.class);
//ARGO_ORIGINAL
assertEquals(1, result.size());
//ARGO_ORIGINAL
assertTrue(result.isObject());
            
            ObjectNode main = (ObjectNode) result;
//ARGO_ORIGINAL
assertEquals("Image", main.fieldNames().next());
            JsonNode ob = main.elements().next();
//ARGO_ORIGINAL
assertType(ob, ObjectNode.class);
            ObjectNode imageMap = (ObjectNode) ob;
            
//ARGO_ORIGINAL
assertEquals(5, imageMap.size());
            ob = imageMap.get("Width");
//ARGO_ORIGINAL
assertTrue(ob.isIntegralNumber());
//ARGO_ORIGINAL
assertFalse(ob.isFloatingPointNumber());
//ARGO_ORIGINAL
assertEquals(SAMPLE_SPEC_VALUE_WIDTH, ob.intValue());
            ob = imageMap.get("Height");
//ARGO_ORIGINAL
assertTrue(ob.isIntegralNumber());
//ARGO_ORIGINAL
assertEquals(SAMPLE_SPEC_VALUE_HEIGHT, ob.intValue());
            
            ob = imageMap.get("Title");
//ARGO_ORIGINAL
assertTrue(ob.isTextual());
//ARGO_ORIGINAL
assertEquals(SAMPLE_SPEC_VALUE_TITLE, ob.textValue());
            
            ob = imageMap.get("Thumbnail");
//ARGO_ORIGINAL
assertType(ob, ObjectNode.class);
            ObjectNode tn = (ObjectNode) ob;
            ob = tn.get("Url");
//ARGO_ORIGINAL
assertTrue(ob.isTextual());
//ARGO_ORIGINAL
assertEquals(SAMPLE_SPEC_VALUE_TN_URL, ob.textValue());
            ob = tn.get("Height");
//ARGO_ORIGINAL
assertTrue(ob.isIntegralNumber());
//ARGO_ORIGINAL
assertEquals(SAMPLE_SPEC_VALUE_TN_HEIGHT, ob.intValue());
            ob = tn.get("Width");
//ARGO_ORIGINAL
assertTrue(ob.isTextual());
//ARGO_ORIGINAL
assertEquals(SAMPLE_SPEC_VALUE_TN_WIDTH, ob.textValue());
            
            ob = imageMap.get("IDs");
//ARGO_ORIGINAL
assertTrue(ob.isArray());
            ArrayNode idList = (ArrayNode) ob;
//ARGO_ORIGINAL
assertEquals(4, idList.size());
//ARGO_ORIGINAL
assertEquals(4, calcLength(idList.elements()));
//ARGO_ORIGINAL
assertEquals(4, calcLength(idList.iterator()));
            {
                int[] values = new int[] {
                    SAMPLE_SPEC_VALUE_TN_ID1,
                    SAMPLE_SPEC_VALUE_TN_ID2,
                    SAMPLE_SPEC_VALUE_TN_ID3,
                    SAMPLE_SPEC_VALUE_TN_ID4
                };
                for (int i = 0; i < values.length; ++i) {
//ARGO_ORIGINAL
assertEquals(values[i], idList.get(i).intValue());
                }
                int i = 0;
                for (JsonNode n : idList) {
//ARGO_ORIGINAL
assertEquals(values[i], n.intValue());
                    ++i;
                }
            }
        }
    }

    public void testMixed() throws IOException
    {
        String JSON = "{\"node\" : { \"a\" : 3 }, \"x\" : 9 }";
        Bean bean = MAPPER.readValue(JSON, Bean.class);

//ARGO_ORIGINAL
assertEquals(9, bean._x);
        JsonNode n = bean._node;
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORIGINAL
assertEquals(1, n.size());
        ObjectNode on = (ObjectNode) n;
//ARGO_ORIGINAL
assertEquals(3, on.get("a").intValue());
    }

    /**
     * Type mappers should be able to gracefully deal with end of
     * input.
     */
    public void testEOF() throws Exception
    {
        String JSON =
            "{ \"key\": [ { \"a\" : { \"name\": \"foo\",  \"type\": 1\n"
            +"},  \"type\": 3, \"url\": \"http://www.google.com\" } ],\n"
            +"\"name\": \"xyz\", \"type\": 1, \"url\" : null }\n  "
            ;
        JsonFactory jf = new JsonFactory();
        JsonParser p = jf.createParser(new StringReader(JSON));
        JsonNode result = MAPPER.readTree(p);

//ARGO_ORIGINAL
assertTrue(result.isObject());
//ARGO_ORIGINAL
assertEquals(4, result.size());

//ARGO_ORIGINAL
assertNull(MAPPER.readTree(p));
        p.close();
    }

    public void testNullViaParser() throws Exception
    {
        final String JSON = " null ";
        JsonFactory jf = new JsonFactory();

        try (JsonParser p = jf.createParser(new StringReader(JSON))) {
            final JsonNode result = MAPPER.readTree(p);
//ARGO_PLACEBO
assertTrue(result.isNull());
        }
    }

    public void testMultiple() throws Exception
    {
        String JSON = "12  \"string\" [ 1, 2, 3 ]";
        JsonFactory jf = new JsonFactory();
        JsonParser p = jf.createParser(new StringReader(JSON));
        final ObjectMapper mapper = objectMapper();
        JsonNode result = mapper.readTree(p);

//ARGO_ORIGINAL
assertTrue(result.isIntegralNumber());
//ARGO_ORIGINAL
assertTrue(result.isInt());
//ARGO_ORIGINAL
assertFalse(result.isTextual());
//ARGO_ORIGINAL
assertEquals(12, result.intValue());

        result = mapper.readTree(p);
//ARGO_ORIGINAL
assertTrue(result.isTextual());
//ARGO_ORIGINAL
assertFalse(result.isIntegralNumber());
//ARGO_ORIGINAL
assertFalse(result.isInt());
//ARGO_ORIGINAL
assertEquals("string", result.textValue());

        result = mapper.readTree(p);
//ARGO_ORIGINAL
assertTrue(result.isArray());
//ARGO_ORIGINAL
assertEquals(3, result.size());

//ARGO_ORIGINAL
assertNull(mapper.readTree(p));
        p.close();
    }

    /*
    /**********************************************
    /* Helper methods
    /**********************************************
     */

    private int calcLength(Iterator<JsonNode> it)
    {
        int count = 0;
        while (it.hasNext()) {
            it.next();
            ++count;
        }
        return count;
    }
}

