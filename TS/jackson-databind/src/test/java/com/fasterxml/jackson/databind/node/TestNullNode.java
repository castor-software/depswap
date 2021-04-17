package com.fasterxml.jackson.databind.node;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestNullNode extends NodeTestBase
{
    final static class CovarianceBean {
        ObjectNode _object;
        ArrayNode _array;

        public void setObject(ObjectNode n) { _object = n; }
        public void setArray(ArrayNode n) { _array = n; }
    }

    @SuppressWarnings("serial")
    static class MyNull extends NullNode { }
    
    private final ObjectMapper MAPPER = sharedMapper();
    
    public void testBasicsWithNullNode() throws Exception
    {
        // Let's use something that doesn't add much beyond JsonNode base
        NullNode n = NullNode.instance;

        // basic properties
//ARGO_PLACEBO
assertFalse(n.isContainerNode());
//ARGO_PLACEBO
assertFalse(n.isBigDecimal());
//ARGO_PLACEBO
assertFalse(n.isBigInteger());
//ARGO_PLACEBO
assertFalse(n.isBinary());
//ARGO_PLACEBO
assertFalse(n.isBoolean());
//ARGO_PLACEBO
assertFalse(n.isPojo());
//ARGO_PLACEBO
assertFalse(n.isMissingNode());

        // fallback accessors
//ARGO_PLACEBO
assertFalse(n.booleanValue());
//ARGO_PLACEBO
assertNull(n.numberValue());
//ARGO_PLACEBO
assertEquals(0, n.intValue());
//ARGO_PLACEBO
assertEquals(0L, n.longValue());
//ARGO_PLACEBO
assertEquals(BigDecimal.ZERO, n.decimalValue());
//ARGO_PLACEBO
assertEquals(BigInteger.ZERO, n.bigIntegerValue());

//ARGO_PLACEBO
assertEquals(0, n.size());
//ARGO_PLACEBO
assertTrue(n.isEmpty());
//ARGO_PLACEBO
assertFalse(n.elements().hasNext());
//ARGO_PLACEBO
assertFalse(n.fieldNames().hasNext());
        // path is never null; but does point to missing node
//ARGO_PLACEBO
assertNotNull(n.path("xyz"));
//ARGO_PLACEBO
assertTrue(n.path("xyz").isMissingNode());

//ARGO_PLACEBO
assertFalse(n.has("field"));
//ARGO_PLACEBO
assertFalse(n.has(3));

//ARGO_PLACEBO
assertNodeNumbersForNonNumeric(n);

        // 2.4
//ARGO_PLACEBO
assertEquals("foo", n.asText("foo"));
    }

    public void testNullHandling() throws Exception
    {
        // First, a stand-alone null
        JsonNode n = MAPPER.readTree("null");
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORIGINAL
assertTrue(n.isNull());
//ARGO_ORIGINAL
assertFalse(n.isNumber());
//ARGO_ORIGINAL
assertFalse(n.isTextual());
//ARGO_ORIGINAL
assertEquals("null", n.asText());
//ARGO_ORIGINAL
assertEquals(n, NullNode.instance);

        n = objectMapper().readTree("null");
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORIGINAL
assertTrue(n.isNull());
        
        // Then object property
        ObjectNode root = (ObjectNode) objectReader().readTree("{\"x\":null}");
//ARGO_ORIGINAL
assertEquals(1, root.size());
        n = root.get("x");
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORIGINAL
assertTrue(n.isNull());
    }

    public void testNullSerialization() throws Exception
    {
        StringWriter sw = new StringWriter();
        MAPPER.writeValue(sw, NullNode.instance);
//ARGO_PLACEBO
assertEquals("null", sw.toString());
    }

    public void testNullHandlingCovariance() throws Exception
    {
        String JSON = "{\"object\" : null, \"array\" : null }";
        CovarianceBean bean = MAPPER.readValue(JSON, CovarianceBean.class);

        ObjectNode on = bean._object;
//ARGO_PLACEBO
assertNull(on);

        ArrayNode an = bean._array;
//ARGO_PLACEBO
assertNull(an);
    }

    @SuppressWarnings("unlikely-arg-type")
    public void testNullEquality() throws Exception
    {
        JsonNode n = MAPPER.nullNode();
//ARGO_PLACEBO
assertTrue(n.isNull());
//ARGO_PLACEBO
assertEquals(n, new MyNull());
//ARGO_PLACEBO
assertEquals(new MyNull(), n);

//ARGO_PLACEBO
assertFalse(n.equals(null));
//ARGO_PLACEBO
assertFalse(n.equals("foo"));
    }
}
