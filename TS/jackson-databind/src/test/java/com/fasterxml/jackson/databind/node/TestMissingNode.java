package com.fasterxml.jackson.databind.node;

import java.io.StringReader;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;

public class TestMissingNode extends NodeTestBase
{
    public void testMissing()
    {
        MissingNode n = MissingNode.getInstance();
//ARGO_PLACEBO
assertTrue(n.isMissingNode());
//ARGO_PLACEBO
assertEquals(JsonToken.NOT_AVAILABLE, n.asToken());
//ARGO_PLACEBO
assertEquals("", n.asText());
//ARGO_PLACEBO
assertStandardEquals(n);
        // 10-Dec-2018, tatu: With 2.10, should serialize same as via ObjectMapper/ObjectWriter
        // 10-Dec-2019, tatu: Surprise! No, this is not how it worked in 2.9, nor does it make
        //    sense... see [databind#2566] for details
//ARGO_PLACEBO
assertEquals("", n.toString());

//ARGO_PLACEBO
assertNodeNumbersForNonNumeric(n);

//ARGO_PLACEBO
assertTrue(n.asBoolean(true));
//ARGO_PLACEBO
assertEquals(4, n.asInt(4));
//ARGO_PLACEBO
assertEquals(5L, n.asLong(5));
//ARGO_PLACEBO
assertEquals(0.25, n.asDouble(0.25));

//ARGO_PLACEBO
assertEquals("foo", n.asText("foo"));
    }

    /**
     * Let's also verify behavior of "MissingNode" -- one needs to be able
     * to traverse such bogus nodes with appropriate methods.
     */
    @SuppressWarnings("unused")
    public void testMissingViaMapper() throws Exception
    {
        String JSON = "[ { }, [ ] ]";
        JsonNode result = objectMapper().readTree(new StringReader(JSON));

//ARGO_ORIGINAL
assertTrue(result.isContainerNode());
//ARGO_ORIGINAL
assertTrue(result.isArray());
//ARGO_ORIGINAL
assertEquals(2, result.size());

        int count = 0;
        for (JsonNode node : result) {
            ++count;
        }
//ARGO_ORIGINAL
assertEquals(2, count);

        Iterator<JsonNode> it = result.iterator();

        JsonNode onode = it.next();
//ARGO_ORIGINAL
assertTrue(onode.isContainerNode());
//ARGO_ORIGINAL
assertTrue(onode.isObject());
//ARGO_ORIGINAL
assertEquals(0, onode.size());
//ARGO_ORIGINAL
assertFalse(onode.isMissingNode()); // real node
//ARGO_ORIGINAL
assertNull(onode.textValue());

        // how about dereferencing?
//ARGO_ORIGINAL
assertNull(onode.get(0));
        JsonNode dummyNode = onode.path(0);
//ARGO_ORIGINAL
assertNotNull(dummyNode);
//ARGO_ORIGINAL
assertTrue(dummyNode.isMissingNode());
//ARGO_ORIGINAL
assertNull(dummyNode.get(3));
//ARGO_ORIGINAL
assertNull(dummyNode.get("whatever"));
        JsonNode dummyNode2 = dummyNode.path(98);
//ARGO_ORIGINAL
assertNotNull(dummyNode2);
//ARGO_ORIGINAL
assertTrue(dummyNode2.isMissingNode());
        JsonNode dummyNode3 = dummyNode.path("field");
//ARGO_ORIGINAL
assertNotNull(dummyNode3);
//ARGO_ORIGINAL
assertTrue(dummyNode3.isMissingNode());

        // and same for the array node

        JsonNode anode = it.next();
//ARGO_ORIGINAL
assertTrue(anode.isContainerNode());
//ARGO_ORIGINAL
assertTrue(anode.isArray());
//ARGO_ORIGINAL
assertFalse(anode.isMissingNode()); // real node
//ARGO_ORIGINAL
assertEquals(0, anode.size());

//ARGO_ORIGINAL
assertNull(anode.get(0));
        dummyNode = anode.path(0);
//ARGO_ORIGINAL
assertNotNull(dummyNode);
//ARGO_ORIGINAL
assertTrue(dummyNode.isMissingNode());
//ARGO_ORIGINAL
assertNull(dummyNode.get(0));
//ARGO_ORIGINAL
assertNull(dummyNode.get("myfield"));
        dummyNode2 = dummyNode.path(98);
//ARGO_ORIGINAL
assertNotNull(dummyNode2);
//ARGO_ORIGINAL
assertTrue(dummyNode2.isMissingNode());
        dummyNode3 = dummyNode.path("f");
//ARGO_ORIGINAL
assertNotNull(dummyNode3);
//ARGO_ORIGINAL
assertTrue(dummyNode3.isMissingNode());
    }
}
