package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;

public class TypeDeserializerTest extends BaseMapTest
{
    public void testUtilMethods() throws Exception
    {
        final JsonFactory f = new JsonFactory();

        JsonParser p = f.createParser("true");
//ARGO_PLACEBO
assertNull(TypeDeserializer.deserializeIfNatural(p, null, Object.class));
        p.nextToken();
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, TypeDeserializer.deserializeIfNatural(p, null, Object.class));
        p.close();

        p = f.createParser("false ");
        p.nextToken();
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, TypeDeserializer.deserializeIfNatural(p, null, Object.class));
        p.close();

        p = f.createParser("1");
        p.nextToken();
//ARGO_PLACEBO
assertEquals(Integer.valueOf(1), TypeDeserializer.deserializeIfNatural(p, null, Object.class));
        p.close();

        p = f.createParser("0.5 ");
        p.nextToken();
//ARGO_PLACEBO
assertEquals(Double.valueOf(0.5), TypeDeserializer.deserializeIfNatural(p, null, Object.class));
        p.close();

        p = f.createParser("\"foo\" [ ] ");
        p.nextToken();
//ARGO_PLACEBO
assertEquals("foo", TypeDeserializer.deserializeIfNatural(p, null, Object.class));

        p.nextToken();
//ARGO_PLACEBO
assertNull(TypeDeserializer.deserializeIfNatural(p, null, Object.class));

        p.close();
    }
}
