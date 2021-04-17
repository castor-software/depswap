package com.fasterxml.jackson.databind.interop;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.*;

public class TestFormatDetection extends BaseMapTest
{
    private final ObjectReader READER = objectReader();

    static class POJO {
        public int x, y;
        
        public POJO() { }
        public POJO(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testSimpleWithJSON() throws Exception
    {
        ObjectReader detecting = READER.forType(POJO.class);
        detecting = detecting.withFormatDetection(detecting);
        POJO pojo = detecting.readValue(utf8Bytes("{\"x\":1}"));
//ARGO_PLACEBO
assertNotNull(pojo);
//ARGO_PLACEBO
assertEquals(1, pojo.x);
    }

    public void testSequenceWithJSON() throws Exception
    {
        ObjectReader detecting = READER.forType(POJO.class);
        detecting = detecting.withFormatDetection(detecting);
        MappingIterator<POJO> it = detecting.
                readValues(utf8Bytes(aposToQuotes("{'x':1}\n{'x':2,'y':5}")));

//ARGO_ORIGINAL
assertTrue(it.hasNextValue());
        POJO pojo = it.nextValue();
//ARGO_ORIGINAL
assertEquals(1, pojo.x);

//ARGO_ORIGINAL
assertTrue(it.hasNextValue());
        pojo = it.nextValue();
//ARGO_ORIGINAL
assertEquals(2, pojo.x);
//ARGO_ORIGINAL
assertEquals(5, pojo.y);

//ARGO_ORIGINAL
assertFalse(it.hasNextValue());
        it.close();

        // And again with nodes
        ObjectReader r2 = READER.forType(JsonNode.class);
        r2 = r2.withFormatDetection(r2);
        MappingIterator<JsonNode> nodes = r2.
                readValues(utf8Bytes(aposToQuotes("{'x':1}\n{'x':2,'y':5}")));

//ARGO_ORIGINAL
assertTrue(nodes.hasNextValue());
        JsonNode n = nodes.nextValue();
//ARGO_ORIGINAL
assertEquals(1, n.size());

//ARGO_ORIGINAL
assertTrue(nodes.hasNextValue());
        n = nodes.nextValue();
//ARGO_ORIGINAL
assertEquals(2, n.size());
//ARGO_ORIGINAL
assertEquals(2, n.path("x").asInt());
//ARGO_ORIGINAL
assertEquals(5, n.path("y").asInt());

//ARGO_ORIGINAL
assertFalse(nodes.hasNextValue());
        nodes.close();
    }

    public void testInvalid() throws Exception
    {
        ObjectReader detecting = READER.forType(POJO.class);
        detecting = detecting.withFormatDetection(detecting);
        try {
            detecting.readValue(utf8Bytes("<POJO><x>1</x></POJO>"));
//ARGO_PLACEBO
fail("Should have failed");
        } catch (JsonProcessingException e) {
            verifyException(e, "Cannot detect format from input");
        }
    }
}
