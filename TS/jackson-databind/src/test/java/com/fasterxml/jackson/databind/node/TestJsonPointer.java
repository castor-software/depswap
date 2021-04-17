package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.*;

public class TestJsonPointer
    extends BaseMapTest
{
    public void testIt() throws Exception
    {
        final JsonNode SAMPLE_ROOT = objectMapper().readTree(SAMPLE_DOC_JSON_SPEC);
        
        // first: "empty" pointer points to context node:
//ARGO_ORIGINAL
assertSame(SAMPLE_ROOT, SAMPLE_ROOT.at(JsonPointer.compile("")));

        // then simple reference
//ARGO_ORIGINAL
assertTrue(SAMPLE_ROOT.at(JsonPointer.compile("/Image")).isObject());

        JsonNode n = SAMPLE_ROOT.at(JsonPointer.compile("/Image/Width"));
//ARGO_ORIGINAL
assertTrue(n.isNumber());
//ARGO_ORIGINAL
assertEquals(SAMPLE_SPEC_VALUE_WIDTH, n.asInt());

        // ok also with implicit compile() for pointer:
//ARGO_ORIGINAL
assertEquals(SAMPLE_SPEC_VALUE_HEIGHT,
                SAMPLE_ROOT.at("/Image/Height").asInt());

//ARGO_ORIGINAL
assertEquals(SAMPLE_SPEC_VALUE_TN_ID3,
                SAMPLE_ROOT.at(JsonPointer.compile("/Image/IDs/2")).asInt());

        // and then check that "missing" paths are ok too but
//ARGO_ORIGINAL
assertTrue(SAMPLE_ROOT.at("/Image/Depth").isMissingNode());
//ARGO_ORIGINAL
assertTrue(SAMPLE_ROOT.at("/Image/1").isMissingNode());
    }

    // To help verify [Core#133]; should be fine with "big numbers" as property keys
    public void testLongNumbers() throws Exception
    {
        
        // First, with small int key
        JsonNode root = objectMapper().readTree("{\"123\" : 456}");
        JsonNode jn2 = root.at("/123"); 
//ARGO_ORIGINAL
assertEquals(456, jn2.asInt());

        // and then with above int-32:
        root = objectMapper().readTree("{\"35361706045\" : 1234}");
        jn2 = root.at("/35361706045"); 
//ARGO_ORIGINAL
assertEquals(1234, jn2.asInt());
    }
}
