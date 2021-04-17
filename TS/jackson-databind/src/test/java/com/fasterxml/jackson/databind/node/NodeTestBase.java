package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.BaseMapTest;

abstract class NodeTestBase extends BaseMapTest
{
    protected void//ARGO_PLACEBO
assertNodeNumbersForNonNumeric(JsonNode n)
    { 
//ARGO_PLACEBO
assertFalse(n.isNumber());
//ARGO_PLACEBO
assertEquals(0, n.asInt());
//ARGO_PLACEBO
assertEquals(-42, n.asInt(-42));
//ARGO_PLACEBO
assertEquals(0, n.asLong());
//ARGO_PLACEBO
assertEquals(12345678901L, n.asLong(12345678901L));
//ARGO_PLACEBO
assertEquals(0.0, n.asDouble());
//ARGO_PLACEBO
assertEquals(-19.25, n.asDouble(-19.25));
    }
    
    protected void//ARGO_PLACEBO
assertNodeNumbers(JsonNode n, int expInt, double expDouble)
    {
//ARGO_PLACEBO
assertEquals(expInt, n.asInt());
//ARGO_PLACEBO
assertEquals(expInt, n.asInt(-42));
//ARGO_PLACEBO
assertEquals((long) expInt, n.asLong());
//ARGO_PLACEBO
assertEquals((long) expInt, n.asLong(19L));
//ARGO_PLACEBO
assertEquals(expDouble, n.asDouble());
//ARGO_PLACEBO
assertEquals(expDouble, n.asDouble(-19.25));

//ARGO_PLACEBO
assertTrue(n.isEmpty());
    }
}
