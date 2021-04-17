package com.fasterxml.jackson.databind.node;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class TestTreeTraversingParser
    extends BaseMapTest
{
    static class Person {
        public String name;
        public int magicNumber;
        public List<String> kids;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Jackson370Bean {
        public Inner inner;
    }

    public static class Inner {
        public String value;
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    public void testSimple() throws Exception
    {
        // For convenience, parse tree from JSON first
        final String JSON =
            "{ \"a\" : 123, \"list\" : [ 12.25, null, true, { }, [ ] ] }";
        JsonNode tree = MAPPER.readTree(JSON);
        JsonParser p = tree.traverse();

//ARGO_ORIGINAL
assertNull(p.currentToken());
//ARGO_ORIGINAL
assertNull(p.currentName());

//ARGO_ORIGINAL
assertToken(JsonToken.START_OBJECT, p.nextToken());
//ARGO_ORIGINAL
assertNull(p.currentName());
//ARGO_ORIGINAL
assertEquals("Expected START_OBJECT", JsonToken.START_OBJECT.asString(), p.getText());

//ARGO_ORIGINAL
assertToken(JsonToken.FIELD_NAME, p.nextToken());
//ARGO_ORIGINAL
assertEquals("a", p.currentName());
//ARGO_ORIGINAL
assertEquals("a", p.getText());

//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertEquals("a", p.currentName());
//ARGO_ORIGINAL
assertEquals(123, p.getIntValue());
//ARGO_ORIGINAL
assertEquals("123", p.getText());

//ARGO_ORIGINAL
assertToken(JsonToken.FIELD_NAME, p.nextToken());
//ARGO_ORIGINAL
assertEquals("list", p.currentName());
//ARGO_ORIGINAL
assertEquals("list", p.getText());

//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertEquals("list", p.currentName());
//ARGO_ORIGINAL
assertEquals(JsonToken.START_ARRAY.asString(), p.getText());

//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
//ARGO_ORIGINAL
assertNull(p.currentName());
//ARGO_ORIGINAL
assertEquals(12.25, p.getDoubleValue(), 0);
//ARGO_ORIGINAL
assertEquals("12.25", p.getText());

//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NULL, p.nextToken());
//ARGO_ORIGINAL
assertNull(p.currentName());
//ARGO_ORIGINAL
assertEquals(JsonToken.VALUE_NULL.asString(), p.getText());

//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_TRUE, p.nextToken());
//ARGO_ORIGINAL
assertNull(p.currentName());
//ARGO_ORIGINAL
assertTrue(p.getBooleanValue());
//ARGO_ORIGINAL
assertEquals(JsonToken.VALUE_TRUE.asString(), p.getText());

//ARGO_ORIGINAL
assertToken(JsonToken.START_OBJECT, p.nextToken());
//ARGO_ORIGINAL
assertNull(p.currentName());
//ARGO_ORIGINAL
assertToken(JsonToken.END_OBJECT, p.nextToken());
//ARGO_ORIGINAL
assertNull(p.currentName());

//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertNull(p.currentName());
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertNull(p.currentName());

//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());

//ARGO_ORIGINAL
assertToken(JsonToken.END_OBJECT, p.nextToken());
//ARGO_ORIGINAL
assertNull(p.currentName());

//ARGO_ORIGINAL
assertNull(p.nextToken());

        p.close();
//ARGO_ORIGINAL
assertTrue(p.isClosed());
    }

    public void testArray() throws Exception
    {
        // For convenience, parse tree from JSON first
        JsonParser p = MAPPER.readTree("[]").traverse();
//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());
        p.close();

        p = MAPPER.readTree("[[]]").traverse();
//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());
        p.close();

        p = MAPPER.readTree("[[ 12.1 ]]").traverse();
//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());
        p.close();
    }
    
    public void testNested() throws Exception
    {
        // For convenience, parse tree from JSON first
        final String JSON =
            "{\"coordinates\":[[[-3,\n1],[179.859681,51.175092]]]}"
            ;
        JsonNode tree = MAPPER.readTree(JSON);
        JsonParser p = tree.traverse();
//ARGO_ORIGINAL
assertToken(JsonToken.START_OBJECT, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.FIELD_NAME, p.nextToken());

//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());

//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());

//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());
        
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());

//ARGO_ORIGINAL
assertToken(JsonToken.END_OBJECT, p.nextToken());
        p.close();
    }
    
    /**
     * Unit test that verifies that we can (re)parse sample document
     * from JSON specification.
     */
    public void testSpecDoc() throws Exception
    {
        JsonNode tree = MAPPER.readTree(SAMPLE_DOC_JSON_SPEC);
        JsonParser p = tree.traverse();
        //verifyJsonSpecSampleDoc(p, true);
        p.close();
    }

    public void testBinaryPojo() throws Exception
    {
        byte[] inputBinary = new byte[] { 1, 2, 100 };
        POJONode n = new POJONode(inputBinary);
        JsonParser p = n.traverse();

//ARGO_PLACEBO
assertNull(p.currentToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
        byte[] data = p.getBinaryValue();
//ARGO_PLACEBO
assertNotNull(data);
//ARGO_PLACEBO
assertArrayEquals(inputBinary, data);
        Object pojo = p.getEmbeddedObject();
//ARGO_PLACEBO
assertSame(data, pojo);
        p.close();
    }

    public void testBinaryNode() throws Exception
    {
        byte[] inputBinary = new byte[] { 0, -5 };
        BinaryNode n = new BinaryNode(inputBinary);
        JsonParser p = n.traverse();

//ARGO_PLACEBO
assertNull(p.currentToken());
        // exposed as POJO... not as VALUE_STRING
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
        byte[] data = p.getBinaryValue();
//ARGO_PLACEBO
assertNotNull(data);
//ARGO_PLACEBO
assertArrayEquals(inputBinary, data);

        // but as importantly, can be viewed as base64 encoded thing:
//ARGO_PLACEBO
assertEquals("APs=", p.getText());

//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();
    }

    public void testTextAsBinary() throws Exception
    {
        TextNode n = new TextNode("   APs=\n");
        JsonParser p = n.traverse();
//ARGO_PLACEBO
assertNull(p.currentToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_STRING, p.nextToken());
        byte[] data = p.getBinaryValue();
//ARGO_PLACEBO
assertNotNull(data);
//ARGO_PLACEBO
assertArrayEquals(new byte[] { 0, -5 }, data);

//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();
//ARGO_PLACEBO
assertTrue(p.isClosed());

        // Also: let's verify we get an exception for garbage...
        n = new TextNode("?!??");
        p = n.traverse();
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_STRING, p.nextToken());
        try {
            p.getBinaryValue();
        } catch (InvalidFormatException e) {
            verifyException(e, "Illegal character");
        }
        p.close();
    }

    /**
     * Very simple test case to verify that tree-to-POJO
     * conversion works ok
     */
    public void testDataBind() throws Exception
    {
        JsonNode tree = MAPPER.readTree
            ("{ \"name\" : \"Tatu\", \n"
             +"\"magicNumber\" : 42,"
             +"\"kids\" : [ \"Leo\", \"Lila\", \"Leia\" ] \n"
             +"}");
        Person tatu = MAPPER.treeToValue(tree, Person.class);
//ARGO_ORIGINAL
assertNotNull(tatu);
//ARGO_ORIGINAL
assertEquals(42, tatu.magicNumber);
//ARGO_ORIGINAL
assertEquals("Tatu", tatu.name);
//ARGO_ORIGINAL
assertNotNull(tatu.kids);
//ARGO_ORIGINAL
assertEquals(3, tatu.kids.size());
//ARGO_ORIGINAL
assertEquals("Leo", tatu.kids.get(0));
//ARGO_ORIGINAL
assertEquals("Lila", tatu.kids.get(1));
//ARGO_ORIGINAL
assertEquals("Leia", tatu.kids.get(2));
    }

    public void testSkipChildrenWrt370() throws Exception
    {
        ObjectNode n = MAPPER.createObjectNode();
        n.putObject("inner").put("value", "test");
        n.putObject("unknown").putNull("inner");
        Jackson370Bean obj = MAPPER.readValue(n.traverse(), Jackson370Bean.class);
//ARGO_ORIGINAL
assertNotNull(obj.inner);
//ARGO_ORIGINAL
assertEquals("test", obj.inner.value);        
    }

    // // // Numeric coercion checks, [databind#2189]

    public void testNumberOverflowInt() throws IOException
    {
        final long tooBig = 1L + Integer.MAX_VALUE;
        try (final JsonParser p = MAPPER.readTree("[ "+tooBig+" ]").traverse()) {
//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertEquals(NumberType.LONG, p.getNumberType());
            try {
                p.getIntValue();
//ARGO_ORIGINAL
fail("Expected failure for `int` overflow");
            } catch (InputCoercionException e) {
                verifyException(e, "Numeric value ("+tooBig+") out of range of int");
            }
        }
        try (final JsonParser p = MAPPER.readTree("{ \"value\" : "+tooBig+" }").traverse()) {
//ARGO_ORIGINAL
assertToken(JsonToken.START_OBJECT, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.FIELD_NAME, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertEquals(NumberType.LONG, p.getNumberType());
            try {
                p.getIntValue();
//ARGO_ORIGINAL
fail("Expected failure for `int` overflow");
            } catch (InputCoercionException e) {
                verifyException(e, "Numeric value ("+tooBig+") out of range of int");
            }
        }
        // But also from floating-point
        final String tooBig2 = "1.0e10";
        try (final JsonParser p = MAPPER.readTree("[ "+tooBig2+" ]").traverse()) {
//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
//ARGO_ORIGINAL
assertEquals(NumberType.DOUBLE, p.getNumberType());
            try {
                p.getIntValue();
//ARGO_ORIGINAL
fail("Expected failure for `int` overflow");
            } catch (InputCoercionException e) {
                verifyException(e, "Numeric value ("+tooBig2+") out of range of int");
            }
        }
    }

    public void testNumberOverflowLong() throws IOException
    {
        final BigInteger tooBig = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        try (final JsonParser p = MAPPER.readTree("[ "+tooBig+" ]").traverse()) {
//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertEquals(NumberType.BIG_INTEGER, p.getNumberType());
            try {
                p.getLongValue();
//ARGO_ORIGINAL
fail("Expected failure for `long` overflow");
            } catch (InputCoercionException e) {
                verifyException(e, "Numeric value ("+tooBig+") out of range of long");
            }
        }
        try (final JsonParser p = MAPPER.readTree("{ \"value\" : "+tooBig+" }").traverse()) {
//ARGO_ORIGINAL
assertToken(JsonToken.START_OBJECT, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.FIELD_NAME, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertEquals(NumberType.BIG_INTEGER, p.getNumberType());
            try {
                p.getLongValue();
//ARGO_ORIGINAL
fail("Expected failure for `long` overflow");
            } catch (InputCoercionException e) {
                verifyException(e, "Numeric value ("+tooBig+") out of range of long");
            }
        }
        // But also from floating-point
        final String tooBig2 = "1.0e30";
        try (final JsonParser p = MAPPER.readTree("[ "+tooBig2+" ]").traverse()) {
//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
//ARGO_ORIGINAL
assertEquals(NumberType.DOUBLE, p.getNumberType());
            try {
                p.getLongValue();
//ARGO_ORIGINAL
fail("Expected failure for `long` overflow");
            } catch (InputCoercionException e) {
                verifyException(e, "Numeric value ("+tooBig2+") out of range of long");
            }
        }

        // Plus, wrt [databind#2393], NON-failing cases
        final long[] okValues = new long[] { 1L+Integer.MAX_VALUE, -1L + Integer.MIN_VALUE,
                Long.MAX_VALUE, Long.MIN_VALUE };
        for (long okValue : okValues) {
            try (final JsonParser p = MAPPER.readTree("{ \"value\" : "+okValue+" }").traverse()) {
//ARGO_ORIGINAL
assertToken(JsonToken.START_OBJECT, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.FIELD_NAME, p.nextToken());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertEquals(NumberType.LONG, p.getNumberType());
//ARGO_ORIGINAL
assertEquals(okValue, p.getLongValue());
//ARGO_ORIGINAL
assertToken(JsonToken.END_OBJECT, p.nextToken());
            }
        }
    }
}
