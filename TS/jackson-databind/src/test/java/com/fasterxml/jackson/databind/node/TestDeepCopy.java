package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple tests to verify that [JACKSON-707] is implemented correctly.
 */
public class TestDeepCopy extends BaseMapTest
{
    private final ObjectMapper mapper = new ObjectMapper();
    
    public void testWithObjectSimple()
    {
        ObjectNode root = mapper.createObjectNode();
        root.put("a", 3);
//ARGO_ORIGINAL
assertEquals(1, root.size());
        
        ObjectNode copy = root.deepCopy();
//ARGO_ORIGINAL
assertEquals(1, copy.size());

        // adding to root won't change copy:
        root.put("b", 7);
//ARGO_ORIGINAL
assertEquals(2, root.size());
//ARGO_ORIGINAL
assertEquals(1, copy.size());

        // nor vice versa
        copy.put("c", 3);
//ARGO_ORIGINAL
assertEquals(2, root.size());
//ARGO_ORIGINAL
assertEquals(2, copy.size());
    }

    public void testWithArraySimple()
    {
        ArrayNode root = mapper.createArrayNode();
        root.add("a");
//ARGO_ORIGINAL
assertEquals(1, root.size());
        
        ArrayNode copy = root.deepCopy();
//ARGO_ORIGINAL
assertEquals(1, copy.size());

        // adding to root won't change copy:
        root.add( 7);
//ARGO_ORIGINAL
assertEquals(2, root.size());
//ARGO_ORIGINAL
assertEquals(1, copy.size());

        // nor vice versa
        copy.add(3);
//ARGO_ORIGINAL
assertEquals(2, root.size());
//ARGO_ORIGINAL
assertEquals(2, copy.size());
    }

    public void testWithNested()
    {
        ObjectNode root = mapper.createObjectNode();
        ObjectNode leafObject = root.putObject("ob");
        ArrayNode leafArray = root.putArray("arr");
//ARGO_ORIGINAL
assertEquals(2, root.size());

        leafObject.put("a", 3);
//ARGO_ORIGINAL
assertEquals(1, leafObject.size());
        leafArray.add(true);
//ARGO_ORIGINAL
assertEquals(1, leafArray.size());
        
        ObjectNode copy = root.deepCopy();
//ARGO_ORIGINAL
assertNotSame(copy, root);
//ARGO_ORIGINAL
assertEquals(2, copy.size());

        // should be detached, once again

        leafObject.put("x", 9);
//ARGO_ORIGINAL
assertEquals(2, leafObject.size());
//ARGO_ORIGINAL
assertEquals(1, copy.get("ob").size());

        leafArray.add("foobar");
//ARGO_ORIGINAL
assertEquals(2, leafArray.size());
//ARGO_ORIGINAL
assertEquals(1, copy.get("arr").size());

        // nor vice versa
        ((ObjectNode) copy.get("ob")).put("c", 3);
//ARGO_ORIGINAL
assertEquals(2, leafObject.size());
//ARGO_ORIGINAL
assertEquals(2, copy.get("ob").size());

        ((ArrayNode) copy.get("arr")).add(13);
//ARGO_ORIGINAL
assertEquals(2, leafArray.size());
//ARGO_ORIGINAL
assertEquals(2, copy.get("arr").size());
    }
}
