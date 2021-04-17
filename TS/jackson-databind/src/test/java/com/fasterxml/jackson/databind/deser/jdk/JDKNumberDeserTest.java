package com.fasterxml.jackson.databind.deser.jdk;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class JDKNumberDeserTest extends BaseMapTest
{
    /*
    /**********************************************************************
    /* Helper classes, beans
    /**********************************************************************
     */

    static class MyBeanHolder {
        public Long id;
        public MyBeanDefaultValue defaultValue;
    }

    static class MyBeanDefaultValue
    {
        public MyBeanValue value;
    }

    @JsonDeserialize(using=MyBeanDeserializer.class)
    static class MyBeanValue {
        public BigDecimal decimal;
        public MyBeanValue() { this(null); }
        public MyBeanValue(BigDecimal d) { this.decimal = d; }
    }

    // [databind#2644]
    static class NodeRoot2644 {
        public String type;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
        @JsonSubTypes(value = {
                @JsonSubTypes.Type(value = NodeParent2644.class, name = "NodeParent")
        })
        public Node2644 node;
    }

    public static class NodeParent2644 extends Node2644 { }

    public static abstract class Node2644 {
        @JsonProperty("amount")
        BigDecimal val;

        public BigDecimal getVal() {
            return val;
        }

        public void setVal(BigDecimal val) {
            this.val = val;
        }
    }

    // [databind#2784]
    static class BigDecimalHolder2784 {
        public BigDecimal value;
    }

    static class NestedBigDecimalHolder2784 {
        @JsonUnwrapped
        public BigDecimalHolder2784 holder;
    }

    /*
    /**********************************************************************
    /* Helper classes, serializers/deserializers/resolvers
    /**********************************************************************
     */
    
    static class MyBeanDeserializer extends JsonDeserializer<MyBeanValue>
    {
        @Override
        public MyBeanValue deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException
        {
            return new MyBeanValue(jp.getDecimalValue());
        }
    }

    /*
    /**********************************************************************
    /* Unit tests
    /**********************************************************************
     */

    final ObjectMapper MAPPER = new ObjectMapper();
    
    public void testNaN() throws Exception
    {
        Float result = MAPPER.readValue(" \"NaN\"", Float.class);
//ARGO_PLACEBO
assertEquals(Float.valueOf(Float.NaN), result);

        Double d = MAPPER.readValue(" \"NaN\"", Double.class);
//ARGO_PLACEBO
assertEquals(Double.valueOf(Double.NaN), d);

        Number num = MAPPER.readValue(" \"NaN\"", Number.class);
//ARGO_PLACEBO
assertEquals(Double.valueOf(Double.NaN), num);
    }

    public void testDoubleInf() throws Exception
    {
        Double result = MAPPER.readValue(" \""+Double.POSITIVE_INFINITY+"\"", Double.class);
//ARGO_PLACEBO
assertEquals(Double.valueOf(Double.POSITIVE_INFINITY), result);

        result = MAPPER.readValue(" \""+Double.NEGATIVE_INFINITY+"\"", Double.class);
//ARGO_PLACEBO
assertEquals(Double.valueOf(Double.NEGATIVE_INFINITY), result);
    }

    // 01-Mar-2017, tatu: This is bit tricky... in some ways, mapping to "empty value"
    //    would be best; but due to legacy reasons becomes `null` at this point
    public void testEmptyAsNumber() throws Exception
    {
//ARGO_PLACEBO
assertNull(MAPPER.readValue(quote(""), Byte.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(quote(""), Short.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(quote(""), Character.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(quote(""), Integer.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(quote(""), Long.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(quote(""), Float.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(quote(""), Double.class));

//ARGO_PLACEBO
assertNull(MAPPER.readValue(quote(""), BigInteger.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(quote(""), BigDecimal.class));
    }

    public void testTextualNullAsNumber() throws Exception
    {
        final String NULL_JSON = quote("null");
//ARGO_PLACEBO
assertNull(MAPPER.readValue(NULL_JSON, Byte.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(NULL_JSON, Short.class));
        // Character is bit special, can't do:
//assertNull(MAPPER.readValue(JSON, Character.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(NULL_JSON, Integer.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(NULL_JSON, Long.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(NULL_JSON, Float.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(NULL_JSON, Double.class));

//ARGO_PLACEBO
assertEquals(Byte.valueOf((byte) 0), MAPPER.readValue(NULL_JSON, Byte.TYPE));
//ARGO_PLACEBO
assertEquals(Short.valueOf((short) 0), MAPPER.readValue(NULL_JSON, Short.TYPE));
        // Character is bit special, can't do:
//assertEquals(Character.valueOf((char) 0), MAPPER.readValue(JSON, Character.TYPE));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(0), MAPPER.readValue(NULL_JSON, Integer.TYPE));
//ARGO_PLACEBO
assertEquals(Long.valueOf(0L), MAPPER.readValue(NULL_JSON, Long.TYPE));
//ARGO_PLACEBO
assertEquals(Float.valueOf(0f), MAPPER.readValue(NULL_JSON, Float.TYPE));
//ARGO_PLACEBO
assertEquals(Double.valueOf(0d), MAPPER.readValue(NULL_JSON, Double.TYPE));
        
//ARGO_PLACEBO
assertNull(MAPPER.readValue(NULL_JSON, BigInteger.class));
//ARGO_PLACEBO
assertNull(MAPPER.readValue(NULL_JSON, BigDecimal.class));

        // Also: verify failure for at least some
        try {
            MAPPER.readerFor(Integer.TYPE).with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(NULL_JSON);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce String \"null\"");
        }

        ObjectMapper noCoerceMapper = jsonMapperBuilder()
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                .build();
        try {
            noCoerceMapper.readValue(NULL_JSON, Integer.TYPE);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce String value");
        }
    }
    
    public void testDeserializeDecimalHappyPath() throws Exception {
        String json = "{\"defaultValue\": { \"value\": 123 } }";
        MyBeanHolder result = MAPPER.readValue(json, MyBeanHolder.class);
//ARGO_PLACEBO
assertEquals(BigDecimal.valueOf(123), result.defaultValue.value.decimal);
    }

    public void testDeserializeDecimalProperException() throws Exception {
        String json = "{\"defaultValue\": { \"value\": \"123\" } }";
        try {
            MAPPER.readValue(json, MyBeanHolder.class);
//ARGO_PLACEBO
fail("should have raised exception");
        } catch (JsonProcessingException e) {
            verifyException(e, "not numeric");
        }
    }

    public void testDeserializeDecimalProperExceptionWhenIdSet() throws Exception {
        String json = "{\"id\": 5, \"defaultValue\": { \"value\": \"123\" } }";
        try {
            MyBeanHolder result = MAPPER.readValue(json, MyBeanHolder.class);
//ARGO_PLACEBO
fail("should have raised exception instead value was set to " + result.defaultValue.value.decimal.toString());
        } catch (JsonProcessingException e) {
            verifyException(e, "not numeric");
        }
    }

    // And then [databind#852]
    public void testScientificNotationAsStringForNumber() throws Exception
    {
        Object ob = MAPPER.readValue("\"3E-8\"", Number.class);
//ARGO_PLACEBO
assertEquals(Double.class, ob.getClass());
        ob = MAPPER.readValue("\"3e-8\"", Number.class);
//ARGO_PLACEBO
assertEquals(Double.class, ob.getClass());
        ob = MAPPER.readValue("\"300000000\"", Number.class);
//ARGO_PLACEBO
assertEquals(Integer.class, ob.getClass());
        ob = MAPPER.readValue("\"123456789012\"", Number.class);
//ARGO_PLACEBO
assertEquals(Long.class, ob.getClass());
    }

    public void testIntAsNumber() throws Exception
    {
        /* Even if declared as 'generic' type, should return using most
         * efficient type... here, Integer
         */
        Number result = MAPPER.readValue(" 123 ", Number.class);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(123), result);
    }

    public void testLongAsNumber() throws Exception
    {
        // And beyond int range, should get long
        long exp = 1234567890123L;
        Number result = MAPPER.readValue(String.valueOf(exp), Number.class);
//ARGO_PLACEBO
assertEquals(Long.valueOf(exp), result);
    }

    public void testBigIntAsNumber() throws Exception
    {
        // and after long, BigInteger
        BigInteger biggie = new BigInteger("1234567890123456789012345678901234567890");
        Number result = MAPPER.readValue(biggie.toString(), Number.class);
//ARGO_PLACEBO
assertEquals(BigInteger.class, biggie.getClass());
//ARGO_PLACEBO
assertEquals(biggie, result);
    }

    public void testIntTypeOverride() throws Exception
    {
        /* Slight twist; as per [JACKSON-100], can also request binding
         * to BigInteger even if value would fit in Integer
         */
        ObjectReader r = MAPPER.reader(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

        BigInteger exp = BigInteger.valueOf(123L);

        // first test as any Number
        Number result = r.forType(Number.class).readValue(" 123 ");
//ARGO_PLACEBO
assertEquals(BigInteger.class, result.getClass());
//ARGO_PLACEBO
assertEquals(exp, result);

        // then as any Object
        /*Object value =*/ r.forType(Object.class).readValue("123");
//ARGO_PLACEBO
assertEquals(BigInteger.class, result.getClass());
//ARGO_PLACEBO
assertEquals(exp, result);

        // and as JsonNode
        JsonNode node = r.readTree("  123");
//ARGO_PLACEBO
assertTrue(node.isBigInteger());
//ARGO_PLACEBO
assertEquals(123, node.asInt());
    }

    public void testDoubleAsNumber() throws Exception
    {
        Number result = MAPPER.readValue(new StringReader(" 1.0 "), Number.class);
//ARGO_PLACEBO
assertEquals(Double.valueOf(1.0), result);
    }

    public void testFpTypeOverrideSimple() throws Exception
    {
        ObjectReader r = MAPPER.reader(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        BigDecimal dec = new BigDecimal("0.1");

        // First test generic stand-alone Number
        Number result = r.forType(Number.class).readValue(dec.toString());
//ARGO_PLACEBO
assertEquals(BigDecimal.class, result.getClass());
//ARGO_PLACEBO
assertEquals(dec, result);

        // Then plain old Object
        Object value = r.forType(Object.class).readValue(dec.toString());
//ARGO_PLACEBO
assertEquals(BigDecimal.class, result.getClass());
//ARGO_PLACEBO
assertEquals(dec, value);

        JsonNode node = r.readTree(dec.toString());
//ARGO_PLACEBO
assertTrue(node.isBigDecimal());
//ARGO_PLACEBO
assertEquals(dec.doubleValue(), node.asDouble());
    }

    public void testFpTypeOverrideStructured() throws Exception
    {
        ObjectReader r = MAPPER.reader(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

        BigDecimal dec = new BigDecimal("-19.37");
        // List element types
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) r.forType(List.class).readValue("[ "+dec.toString()+" ]");
//ARGO_PLACEBO
assertEquals(1, list.size());
        Object val = list.get(0);
//ARGO_PLACEBO
assertEquals(BigDecimal.class, val.getClass());
//ARGO_PLACEBO
assertEquals(dec, val);

        // and a map
        Map<?,?> map = r.forType(Map.class).readValue("{ \"a\" : "+dec.toString()+" }");
//ARGO_PLACEBO
assertEquals(1, map.size());
        val = map.get("a");
//ARGO_PLACEBO
assertEquals(BigDecimal.class, val.getClass());
//ARGO_PLACEBO
assertEquals(dec, val);
    }

    // [databind#504]
    public void testForceIntsToLongs() throws Exception
    {
        ObjectReader r = MAPPER.reader(DeserializationFeature.USE_LONG_FOR_INTS);

        Object ob = r.forType(Object.class).readValue("42");
//ARGO_PLACEBO
assertEquals(Long.class, ob.getClass());
//ARGO_PLACEBO
assertEquals(Long.valueOf(42L), ob);

        Number n = r.forType(Number.class).readValue("42");
//ARGO_PLACEBO
assertEquals(Long.class, n.getClass());
//ARGO_PLACEBO
assertEquals(Long.valueOf(42L), n);

        // and one more: should get proper node as well
        JsonNode node = r.readTree("42");
        if (!node.isLong()) {
//ARGO_PLACEBO
fail("Expected LongNode, got: "+node.getClass().getName());
        }
//ARGO_PLACEBO
assertEquals(42, node.asInt());
    }

    // [databind#2644]
    public void testBigDecimalSubtypes() throws Exception
    {
        ObjectMapper mapper = jsonMapperBuilder()
                .registerSubtypes(NodeParent2644.class)
                .build();
        NodeRoot2644 root = mapper.readValue(
                "{\"type\": \"NodeParent\",\"node\": {\"amount\": 9999999999999999.99} }",
                NodeRoot2644.class
        );

//ARGO_PLACEBO
assertEquals(new BigDecimal("9999999999999999.99"), root.node.getVal());
    }

    // [databind#2784]
    public void testBigDecimalUnwrapped() throws Exception
    {
        final ObjectMapper mapper = newJsonMapper();
        // mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        final String JSON = "{\"value\": 5.00}";
        NestedBigDecimalHolder2784 result = mapper.readValue(JSON, NestedBigDecimalHolder2784.class);
//ARGO_PLACEBO
assertEquals(new BigDecimal("5.00"), result.holder.value);
    }
}
