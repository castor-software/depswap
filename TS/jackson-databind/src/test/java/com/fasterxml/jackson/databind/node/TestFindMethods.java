package com.fasterxml.jackson.databind.node;

import java.util.*;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JsonNode;

public class TestFindMethods
    extends BaseMapTest
{
    public void testNonMatching() throws Exception
    {
        JsonNode root = _buildTree();

//ARGO_ORIGINAL
assertNull(root.findValue("boogaboo"));
//ARGO_ORIGINAL
assertNull(root.findParent("boogaboo"));
        JsonNode n = root.findPath("boogaboo");
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORIGINAL
assertTrue(n.isMissingNode());

//ARGO_ORIGINAL
assertTrue(root.findValues("boogaboo").isEmpty());
//ARGO_ORIGINAL
assertTrue(root.findParents("boogaboo").isEmpty());
    }

    public void testMatchingSingle() throws Exception
    {
        JsonNode root = _buildTree();

        JsonNode node = root.findValue("b");
//ARGO_ORIGINAL
assertNotNull(node);
//ARGO_ORIGINAL
assertEquals(3, node.intValue());
        node = root.findParent("b");
//ARGO_ORIGINAL
assertNotNull(node);
//ARGO_ORIGINAL
assertTrue(node.isObject());
//ARGO_ORIGINAL
assertEquals(1, ((ObjectNode) node).size());
//ARGO_ORIGINAL
assertEquals(3, node.path("b").intValue());
    }

    public void testMatchingMultiple() throws Exception
    {
        JsonNode root = _buildTree();

        List<JsonNode> nodes = root.findValues("value");
//ARGO_ORIGINAL
assertEquals(2, nodes.size());
        // here we count on nodes being returned in order; true with Jackson:
//ARGO_ORIGINAL
assertEquals(3, nodes.get(0).intValue());
//ARGO_ORIGINAL
assertEquals(42, nodes.get(1).intValue());

        nodes = root.findParents("value");
//ARGO_ORIGINAL
assertEquals(2, nodes.size());
        // should only return JSON Object nodes:
//ARGO_ORIGINAL
assertTrue(nodes.get(0).isObject());
//ARGO_ORIGINAL
assertTrue(nodes.get(1).isObject());
//ARGO_ORIGINAL
assertEquals(3, nodes.get(0).path("value").intValue());
//ARGO_ORIGINAL
assertEquals(42, nodes.get(1).path("value").intValue());

        // and finally, convenience conversion method
        List<String> values = root.findValuesAsText("value");
//ARGO_ORIGINAL
assertEquals(2, values.size());
//ARGO_ORIGINAL
assertEquals("3", values.get(0));
//ARGO_ORIGINAL
assertEquals("42", values.get(1));
    }
    
    private JsonNode _buildTree() throws Exception
    {
        final String SAMPLE = "{ \"a\" : { \"value\" : 3 },"
            +"\"array\" : [ { \"b\" : 3 }, {\"value\" : 42}, { \"other\" : true } ]"
            +"}";
        return objectMapper().readTree(SAMPLE);
    }
}
