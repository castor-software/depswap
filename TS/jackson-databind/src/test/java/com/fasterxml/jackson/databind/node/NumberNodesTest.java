package com.fasterxml.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;

/**
 * Basic tests for {@link JsonNode} implementations that
 * contain numeric values.
 */
public class NumberNodesTest extends NodeTestBase
{
    private final ObjectMapper MAPPER = objectMapper();
    
    public void testShort()
    {
        ShortNode n = ShortNode.valueOf((short) 1);
//ARGO_PLACEBO
assertStandardEquals(n);
//ARGO_PLACEBO
assertTrue(0 != n.hashCode());
//ARGO_PLACEBO
assertEquals(JsonToken.VALUE_NUMBER_INT, n.asToken());
//ARGO_PLACEBO
assertEquals(JsonParser.NumberType.INT, n.numberType());	// should be SHORT
//ARGO_PLACEBO
assertEquals(1, n.intValue());
//ARGO_PLACEBO
assertEquals(1L, n.longValue());
//ARGO_PLACEBO
assertEquals(BigDecimal.ONE, n.decimalValue());
//ARGO_PLACEBO
assertEquals(BigInteger.ONE, n.bigIntegerValue());
//ARGO_PLACEBO
assertEquals("1", n.asText());

//ARGO_PLACEBO
assertNodeNumbers(n, 1, 1.0);

//ARGO_PLACEBO
assertTrue(ShortNode.valueOf((short) 0).canConvertToInt());
//ARGO_PLACEBO
assertTrue(ShortNode.valueOf(Short.MAX_VALUE).canConvertToInt());
//ARGO_PLACEBO
assertTrue(ShortNode.valueOf(Short.MIN_VALUE).canConvertToInt());

//ARGO_PLACEBO
assertTrue(ShortNode.valueOf((short) 0).canConvertToLong());
//ARGO_PLACEBO
assertTrue(ShortNode.valueOf(Short.MAX_VALUE).canConvertToLong());
//ARGO_PLACEBO
assertTrue(ShortNode.valueOf(Short.MIN_VALUE).canConvertToLong());
    }

    public void testIntViaMapper() throws Exception
    {
        int value = -90184;
        JsonNode result = MAPPER.readTree(String.valueOf(value));
//ARGO_PLACEBO
assertTrue(result.isNumber());
//ARGO_PLACEBO
assertTrue(result.isIntegralNumber());
//ARGO_PLACEBO
assertTrue(result.isInt());
//ARGO_PLACEBO
assertType(result, IntNode.class);
//ARGO_PLACEBO
assertFalse(result.isLong());
//ARGO_PLACEBO
assertFalse(result.isFloatingPointNumber());
//ARGO_PLACEBO
assertFalse(result.isDouble());
//ARGO_PLACEBO
assertFalse(result.isNull());
//ARGO_PLACEBO
assertFalse(result.isTextual());
//ARGO_PLACEBO
assertFalse(result.isMissingNode());

//ARGO_PLACEBO
assertEquals(value, result.numberValue().intValue());
//ARGO_PLACEBO
assertEquals(value, result.intValue());
//ARGO_PLACEBO
assertEquals(String.valueOf(value), result.asText());
//ARGO_PLACEBO
assertEquals((double) value, result.doubleValue());
//ARGO_PLACEBO
assertEquals((long) value, result.longValue());

        // also, equality should work ok
//ARGO_PLACEBO
assertEquals(result, IntNode.valueOf(value));
    }

    public void testInt()
    {
        IntNode n = IntNode.valueOf(1);
//ARGO_PLACEBO
assertStandardEquals(n);
//ARGO_PLACEBO
assertTrue(0 != n.hashCode());
//ARGO_PLACEBO
assertEquals(JsonToken.VALUE_NUMBER_INT, n.asToken());
//ARGO_PLACEBO
assertEquals(JsonParser.NumberType.INT, n.numberType());
//ARGO_PLACEBO
assertEquals(1, n.intValue());
//ARGO_PLACEBO
assertEquals(1L, n.longValue());
//ARGO_PLACEBO
assertEquals(BigDecimal.ONE, n.decimalValue());
//ARGO_PLACEBO
assertEquals(BigInteger.ONE, n.bigIntegerValue());
//ARGO_PLACEBO
assertEquals("1", n.asText());
        // 2.4
//ARGO_PLACEBO
assertEquals("1", n.asText("foo"));
        
//ARGO_PLACEBO
assertNodeNumbers(n, 1, 1.0);

//ARGO_PLACEBO
assertTrue(IntNode.valueOf(0).canConvertToInt());
//ARGO_PLACEBO
assertTrue(IntNode.valueOf(Integer.MAX_VALUE).canConvertToInt());
//ARGO_PLACEBO
assertTrue(IntNode.valueOf(Integer.MIN_VALUE).canConvertToInt());

//ARGO_PLACEBO
assertTrue(IntNode.valueOf(0).canConvertToLong());
//ARGO_PLACEBO
assertTrue(IntNode.valueOf(Integer.MAX_VALUE).canConvertToLong());
//ARGO_PLACEBO
assertTrue(IntNode.valueOf(Integer.MIN_VALUE).canConvertToLong());

    }

    public void testLong()
    {
        LongNode n = LongNode.valueOf(1L);
//ARGO_PLACEBO
assertStandardEquals(n);
//ARGO_PLACEBO
assertTrue(0 != n.hashCode());
//ARGO_PLACEBO
assertEquals(JsonToken.VALUE_NUMBER_INT, n.asToken());
//ARGO_PLACEBO
assertEquals(JsonParser.NumberType.LONG, n.numberType());
//ARGO_PLACEBO
assertEquals(1, n.intValue());
//ARGO_PLACEBO
assertEquals(1L, n.longValue());
//ARGO_PLACEBO
assertEquals(BigDecimal.ONE, n.decimalValue());
//ARGO_PLACEBO
assertEquals(BigInteger.ONE, n.bigIntegerValue());
//ARGO_PLACEBO
assertEquals("1", n.asText());

//ARGO_PLACEBO
assertNodeNumbers(n, 1, 1.0);

        // ok if contains small enough value
//ARGO_PLACEBO
assertTrue(LongNode.valueOf(0).canConvertToInt());
//ARGO_PLACEBO
assertTrue(LongNode.valueOf(Integer.MAX_VALUE).canConvertToInt());
//ARGO_PLACEBO
assertTrue(LongNode.valueOf(Integer.MIN_VALUE).canConvertToInt());
        // but not in other cases
//ARGO_PLACEBO
assertFalse(LongNode.valueOf(1L + Integer.MAX_VALUE).canConvertToInt());
//ARGO_PLACEBO
assertFalse(LongNode.valueOf(-1L + Integer.MIN_VALUE).canConvertToInt());

//ARGO_PLACEBO
assertTrue(LongNode.valueOf(0L).canConvertToLong());
//ARGO_PLACEBO
assertTrue(LongNode.valueOf(Long.MAX_VALUE).canConvertToLong());
//ARGO_PLACEBO
assertTrue(LongNode.valueOf(Long.MIN_VALUE).canConvertToLong());
    }

    public void testLongViaMapper() throws Exception
    {
        // need to use something being 32-bit value space
        long value = 12345678L << 32;
        JsonNode result = MAPPER.readTree(String.valueOf(value));
//ARGO_PLACEBO
assertTrue(result.isNumber());
//ARGO_PLACEBO
assertTrue(result.isIntegralNumber());
//ARGO_PLACEBO
assertTrue(result.isLong());
//ARGO_PLACEBO
assertType(result, LongNode.class);
//ARGO_PLACEBO
assertFalse(result.isInt());
//ARGO_PLACEBO
assertFalse(result.isFloatingPointNumber());
//ARGO_PLACEBO
assertFalse(result.isDouble());
//ARGO_PLACEBO
assertFalse(result.isNull());
//ARGO_PLACEBO
assertFalse(result.isTextual());
//ARGO_PLACEBO
assertFalse(result.isMissingNode());

//ARGO_PLACEBO
assertEquals(value, result.numberValue().longValue());
//ARGO_PLACEBO
assertEquals(value, result.longValue());
//ARGO_PLACEBO
assertEquals(String.valueOf(value), result.asText());
//ARGO_PLACEBO
assertEquals((double) value, result.doubleValue());

        // also, equality should work ok
//ARGO_PLACEBO
assertEquals(result, LongNode.valueOf(value));
    }

    public void testDouble() throws Exception
    {
        DoubleNode n = DoubleNode.valueOf(0.25);
//ARGO_PLACEBO
assertStandardEquals(n);
//ARGO_PLACEBO
assertTrue(0 != n.hashCode());
//ARGO_PLACEBO
assertEquals(JsonToken.VALUE_NUMBER_FLOAT, n.asToken());
//ARGO_PLACEBO
assertEquals(JsonParser.NumberType.DOUBLE, n.numberType());
//ARGO_PLACEBO
assertEquals(0, n.intValue());
//ARGO_PLACEBO
assertEquals(0.25, n.doubleValue());
//ARGO_PLACEBO
assertNotNull(n.decimalValue());
//ARGO_PLACEBO
assertEquals(BigInteger.ZERO, n.bigIntegerValue());
//ARGO_PLACEBO
assertEquals("0.25", n.asText());

//ARGO_PLACEBO
assertNodeNumbers(DoubleNode.valueOf(4.5), 4, 4.5);

//ARGO_PLACEBO
assertTrue(DoubleNode.valueOf(0).canConvertToInt());
//ARGO_PLACEBO
assertTrue(DoubleNode.valueOf(Integer.MAX_VALUE).canConvertToInt());
//ARGO_PLACEBO
assertTrue(DoubleNode.valueOf(Integer.MIN_VALUE).canConvertToInt());
//ARGO_PLACEBO
assertFalse(DoubleNode.valueOf(1L + Integer.MAX_VALUE).canConvertToInt());
//ARGO_PLACEBO
assertFalse(DoubleNode.valueOf(-1L + Integer.MIN_VALUE).canConvertToInt());

//ARGO_PLACEBO
assertTrue(DoubleNode.valueOf(0L).canConvertToLong());
//ARGO_PLACEBO
assertTrue(DoubleNode.valueOf(Long.MAX_VALUE).canConvertToLong());
//ARGO_PLACEBO
assertTrue(DoubleNode.valueOf(Long.MIN_VALUE).canConvertToLong());

        JsonNode num = objectMapper().readTree(" -0.0");
//ARGO_PLACEBO
assertTrue(num.isDouble());
        n = (DoubleNode) num;
//ARGO_PLACEBO
assertEquals(-0.0, n.doubleValue());
//ARGO_PLACEBO
assertEquals("-0.0", String.valueOf(n.doubleValue()));
    }

    public void testDoubleViaMapper() throws Exception
    {
        double value = 3.04;
        JsonNode result = MAPPER.readTree(String.valueOf(value));
//ARGO_PLACEBO
assertTrue(result.isNumber());
//ARGO_PLACEBO
assertFalse(result.isNull());
//ARGO_PLACEBO
assertType(result, DoubleNode.class);
//ARGO_PLACEBO
assertTrue(result.isFloatingPointNumber());
//ARGO_PLACEBO
assertTrue(result.isDouble());
//ARGO_PLACEBO
assertFalse(result.isInt());
//ARGO_PLACEBO
assertFalse(result.isLong());
//ARGO_PLACEBO
assertFalse(result.isIntegralNumber());
//ARGO_PLACEBO
assertFalse(result.isTextual());
//ARGO_PLACEBO
assertFalse(result.isMissingNode());

//ARGO_PLACEBO
assertEquals(value, result.doubleValue());
//ARGO_PLACEBO
assertEquals(value, result.numberValue().doubleValue());
//ARGO_PLACEBO
assertEquals((int) value, result.intValue());
//ARGO_PLACEBO
assertEquals((long) value, result.longValue());
//ARGO_PLACEBO
assertEquals(String.valueOf(value), result.asText());

        // also, equality should work ok
//ARGO_PLACEBO
assertEquals(result, DoubleNode.valueOf(value));
    }

    // @since 2.2
    public void testFloat()
    {
        FloatNode n = FloatNode.valueOf(0.45f);
//ARGO_PLACEBO
assertStandardEquals(n);
//ARGO_PLACEBO
assertTrue(0 != n.hashCode());
//ARGO_PLACEBO
assertEquals(JsonToken.VALUE_NUMBER_FLOAT, n.asToken());
//ARGO_PLACEBO
assertEquals(JsonParser.NumberType.FLOAT, n.numberType());
//ARGO_PLACEBO
assertEquals(0, n.intValue());
        
        // NOTE: conversion to double NOT as simple as with exact numbers like 0.25:
//ARGO_PLACEBO
assertEquals(0.45f, n.floatValue());
//ARGO_PLACEBO
assertEquals("0.45", n.asText());

        // so; as double we'll get more complex number; however, should round-trip
        // to something that gets printed the same way. But not exact value, alas, hence:
//ARGO_PLACEBO
assertEquals("0.45",  String.valueOf((float) n.doubleValue()));

//ARGO_PLACEBO
assertNotNull(n.decimalValue());
        // possibly surprisingly, however, this will produce same output:
//ARGO_PLACEBO
assertEquals(BigInteger.ZERO, n.bigIntegerValue());
//ARGO_PLACEBO
assertEquals("0.45", n.asText());

        // 1.6:
//ARGO_PLACEBO
assertNodeNumbers(FloatNode.valueOf(4.5f), 4, 4.5f);

//ARGO_PLACEBO
assertTrue(FloatNode.valueOf(0).canConvertToInt());
//ARGO_PLACEBO
assertTrue(FloatNode.valueOf(Integer.MAX_VALUE).canConvertToInt());
//ARGO_PLACEBO
assertTrue(FloatNode.valueOf(Integer.MIN_VALUE).canConvertToInt());

        // rounding errors if we just add/sub 1... so:
//ARGO_PLACEBO
assertFalse(FloatNode.valueOf(1000L + Integer.MAX_VALUE).canConvertToInt());
//ARGO_PLACEBO
assertFalse(FloatNode.valueOf(-1000L + Integer.MIN_VALUE).canConvertToInt());

//ARGO_PLACEBO
assertTrue(FloatNode.valueOf(0L).canConvertToLong());
//ARGO_PLACEBO
assertTrue(FloatNode.valueOf(Integer.MAX_VALUE).canConvertToLong());
//ARGO_PLACEBO
assertTrue(FloatNode.valueOf(Integer.MIN_VALUE).canConvertToLong());
    }
    
    public void testDecimalNode() throws Exception
    {
        DecimalNode n = DecimalNode.valueOf(BigDecimal.ONE);
//ARGO_PLACEBO
assertStandardEquals(n);
//ARGO_PLACEBO
assertTrue(n.equals(new DecimalNode(BigDecimal.ONE)));
//ARGO_PLACEBO
assertEquals(JsonToken.VALUE_NUMBER_FLOAT, n.asToken());
//ARGO_PLACEBO
assertEquals(JsonParser.NumberType.BIG_DECIMAL, n.numberType());
//ARGO_PLACEBO
assertTrue(n.isNumber());
//ARGO_PLACEBO
assertFalse(n.isIntegralNumber());
//ARGO_PLACEBO
assertFalse(n.isArray());
//ARGO_PLACEBO
assertTrue(n.isBigDecimal());
//ARGO_PLACEBO
assertEquals(BigDecimal.ONE, n.numberValue());
//ARGO_PLACEBO
assertEquals(1, n.intValue());
//ARGO_PLACEBO
assertEquals(1L, n.longValue());
//ARGO_PLACEBO
assertEquals(BigDecimal.ONE, n.decimalValue());
//ARGO_PLACEBO
assertEquals("1", n.asText());

//ARGO_PLACEBO
assertNodeNumbers(n, 1, 1.0);

//ARGO_PLACEBO
assertTrue(DecimalNode.valueOf(BigDecimal.ZERO).canConvertToInt());
//ARGO_PLACEBO
assertTrue(DecimalNode.valueOf(BigDecimal.valueOf(Integer.MAX_VALUE)).canConvertToInt());
//ARGO_PLACEBO
assertTrue(DecimalNode.valueOf(BigDecimal.valueOf(Integer.MIN_VALUE)).canConvertToInt());
//ARGO_PLACEBO
assertFalse(DecimalNode.valueOf(BigDecimal.valueOf(1L + Integer.MAX_VALUE)).canConvertToInt());
//ARGO_PLACEBO
assertFalse(DecimalNode.valueOf(BigDecimal.valueOf(-1L + Integer.MIN_VALUE)).canConvertToInt());

//ARGO_PLACEBO
assertTrue(DecimalNode.valueOf(BigDecimal.ZERO).canConvertToLong());
//ARGO_PLACEBO
assertTrue(DecimalNode.valueOf(BigDecimal.valueOf(Long.MAX_VALUE)).canConvertToLong());
//ARGO_PLACEBO
assertTrue(DecimalNode.valueOf(BigDecimal.valueOf(Long.MIN_VALUE)).canConvertToLong());

        // no "natural" way to get it, must construct
        BigDecimal value = new BigDecimal("0.1");
        JsonNode result = DecimalNode.valueOf(value);

//ARGO_PLACEBO
assertFalse(result.isObject());
//ARGO_PLACEBO
assertTrue(result.isNumber());
//ARGO_PLACEBO
assertFalse(result.isIntegralNumber());
//ARGO_PLACEBO
assertFalse(result.isLong());
//ARGO_PLACEBO
assertType(result, DecimalNode.class);
//ARGO_PLACEBO
assertFalse(result.isInt());
//ARGO_PLACEBO
assertTrue(result.isFloatingPointNumber());
//ARGO_PLACEBO
assertTrue(result.isBigDecimal());
//ARGO_PLACEBO
assertFalse(result.isDouble());
//ARGO_PLACEBO
assertFalse(result.isNull());
//ARGO_PLACEBO
assertFalse(result.isTextual());
//ARGO_PLACEBO
assertFalse(result.isMissingNode());

//ARGO_PLACEBO
assertEquals(value, result.numberValue());
//ARGO_PLACEBO
assertEquals(value.toString(), result.asText());

        // also, equality should work ok
//ARGO_PLACEBO
assertEquals(result, DecimalNode.valueOf(value));
    }
    
    public void testDecimalNodeEqualsHashCode()
    {
        // We want DecimalNodes with equivalent _numeric_ values to be equal;
        // this is not the case for BigDecimal where "1.0" and "1" are not
        // equal!
        BigDecimal b1 = BigDecimal.ONE;
        BigDecimal b2 = new BigDecimal("1.0");
        BigDecimal b3 = new BigDecimal("0.01e2");
        BigDecimal b4 = new BigDecimal("1000e-3");

        DecimalNode node1 = new DecimalNode(b1);
        DecimalNode node2 = new DecimalNode(b2);
        DecimalNode node3 = new DecimalNode(b3);
        DecimalNode node4 = new DecimalNode(b4);

//ARGO_PLACEBO
assertEquals(node1.hashCode(), node2.hashCode());
//ARGO_PLACEBO
assertEquals(node2.hashCode(), node3.hashCode());
//ARGO_PLACEBO
assertEquals(node3.hashCode(), node4.hashCode());

//ARGO_PLACEBO
assertEquals(node1, node2);
//ARGO_PLACEBO
assertEquals(node2, node1);
//ARGO_PLACEBO
assertEquals(node2, node3);
//ARGO_PLACEBO
assertEquals(node3, node4);
    }

    public void testBigIntegerNode() throws Exception
    {
        BigIntegerNode n = BigIntegerNode.valueOf(BigInteger.ONE);
//ARGO_PLACEBO
assertStandardEquals(n);
//ARGO_PLACEBO
assertTrue(n.equals(new BigIntegerNode(BigInteger.ONE)));
//ARGO_PLACEBO
assertEquals(JsonToken.VALUE_NUMBER_INT, n.asToken());
//ARGO_PLACEBO
assertEquals(JsonParser.NumberType.BIG_INTEGER, n.numberType());
//ARGO_PLACEBO
assertTrue(n.isNumber());
//ARGO_PLACEBO
assertTrue(n.isIntegralNumber());
//ARGO_PLACEBO
assertTrue(n.isBigInteger());
//ARGO_PLACEBO
assertEquals(BigInteger.ONE, n.numberValue());
//ARGO_PLACEBO
assertEquals(1, n.intValue());
//ARGO_PLACEBO
assertEquals(1L, n.longValue());
//ARGO_PLACEBO
assertEquals(BigInteger.ONE, n.bigIntegerValue());
//ARGO_PLACEBO
assertEquals("1", n.asText());
//ARGO_PLACEBO
assertNodeNumbers(n, 1, 1.0);

        BigInteger maxLong = BigInteger.valueOf(Long.MAX_VALUE);
        
        n = BigIntegerNode.valueOf(maxLong);
//ARGO_PLACEBO
assertEquals(Long.MAX_VALUE, n.longValue());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode n2 = mapper.readTree(maxLong.toString());
//ARGO_PLACEBO
assertEquals(Long.MAX_VALUE, n2.longValue());

        // then over long limit:
        BigInteger beyondLong = maxLong.shiftLeft(2); // 4x max long
        n2 = mapper.readTree(beyondLong.toString());
//ARGO_PLACEBO
assertEquals(beyondLong, n2.bigIntegerValue());

//ARGO_PLACEBO
assertTrue(BigIntegerNode.valueOf(BigInteger.ZERO).canConvertToInt());
//ARGO_PLACEBO
assertTrue(BigIntegerNode.valueOf(BigInteger.valueOf(Integer.MAX_VALUE)).canConvertToInt());
//ARGO_PLACEBO
assertTrue(BigIntegerNode.valueOf(BigInteger.valueOf(Integer.MIN_VALUE)).canConvertToInt());
//ARGO_PLACEBO
assertFalse(BigIntegerNode.valueOf(BigInteger.valueOf(1L + Integer.MAX_VALUE)).canConvertToInt());
//ARGO_PLACEBO
assertFalse(BigIntegerNode.valueOf(BigInteger.valueOf(-1L + Integer.MIN_VALUE)).canConvertToInt());

//ARGO_PLACEBO
assertTrue(BigIntegerNode.valueOf(BigInteger.ZERO).canConvertToLong());
//ARGO_PLACEBO
assertTrue(BigIntegerNode.valueOf(BigInteger.valueOf(Long.MAX_VALUE)).canConvertToLong());
//ARGO_PLACEBO
assertTrue(BigIntegerNode.valueOf(BigInteger.valueOf(Long.MIN_VALUE)).canConvertToLong());
    }

    public void testBigDecimalAsPlain() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        final String INPUT = "{\"x\":1e2}";
        final JsonNode node = mapper.readTree(INPUT);
        String result = mapper.writeValueAsString(node);
//ARGO_ORIGINAL
assertEquals("{\"x\":100}", result);

        // also via ObjectWriter:
//ARGO_ORIGINAL
assertEquals("{\"x\":100}", mapper.writer().writeValueAsString(node));

        // and once more for [core#175]:
        BigDecimal bigDecimal = new BigDecimal(100);
        JsonNode tree = mapper.valueToTree(bigDecimal);
//ARGO_ORIGINAL
assertEquals("100", mapper.writeValueAsString(tree));
    }

    // Related to [databind#333]
    public void testCanonicalNumbers() throws Exception
    {
        JsonNodeFactory f = new JsonNodeFactory();
        NumericNode n = f.numberNode(123);
//ARGO_PLACEBO
assertTrue(n.isInt());
        n = f.numberNode(1L + Integer.MAX_VALUE);
//ARGO_PLACEBO
assertFalse(n.isInt());
//ARGO_PLACEBO
assertTrue(n.isLong());

        // 19-May-2015, tatu: Actually, no, coercion should not happen by default.
        //   But it should be possible to change it if necessary.
        // but "too small" number will be 'int'...
        n = f.numberNode(123L);
//ARGO_PLACEBO
assertTrue(n.isLong());
    }
}
