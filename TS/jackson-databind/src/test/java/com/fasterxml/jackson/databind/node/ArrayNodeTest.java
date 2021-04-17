package com.fasterxml.jackson.databind.node;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import static com.fasterxml.jackson.databind.JSONTestUtils.//ARGO_ORIGINAL
assertEquivalent;
import static com.fasterxml.jackson.databind.JSONTestUtils.//ARGO_ORIGINAL
assertNonEquivalent;
import static java.util.Arrays.asList;

/**
 * Additional tests for {@link ArrayNode} container class.
 */
public class ArrayNodeTest
    extends BaseMapTest
{
    public void testDirectCreation() throws IOException
    {
        ArrayNode n = new ArrayNode(JsonNodeFactory.instance);
//ARGO_ORIGINAL
assertStandardEquals(n);
//ARGO_ORIGINAL
assertFalse(n.elements().hasNext());
//ARGO_ORIGINAL
assertFalse(n.fieldNames().hasNext());
//ARGO_ORIGINAL
assertTrue(n.isEmpty());
        TextNode text = TextNode.valueOf("x");
        n.add(text);
//ARGO_ORIGINAL
assertEquals(1, n.size());
//ARGO_ORIGINAL
assertFalse(n.isEmpty());
//ARGO_ORIGINAL
assertFalse(0 == n.hashCode());
//ARGO_ORIGINAL
assertTrue(n.elements().hasNext());
        // no field names for arrays
//ARGO_ORIGINAL
assertFalse(n.fieldNames().hasNext());
//ARGO_ORIGINAL
assertNull(n.get("x")); // not used with arrays
//ARGO_ORIGINAL
assertTrue(n.path("x").isMissingNode());
//ARGO_EQUIVALENT
        assertEquivalent(text, n.get(0));
//ARGO_ORIGINAL
assertEquals(text.asText(), n.get(0).asText());

        // single element, so:
//ARGO_ORIGINAL
assertFalse(n.has("field"));
//ARGO_ORIGINAL
assertFalse(n.hasNonNull("field"));
//ARGO_ORIGINAL
assertTrue(n.has(0));
//ARGO_ORIGINAL
assertTrue(n.hasNonNull(0));
//ARGO_ORIGINAL
assertFalse(n.has(1));
//ARGO_ORIGINAL
assertFalse(n.hasNonNull(1));
        
        // add null node too
        n.add((JsonNode) null);
//ARGO_ORIGINAL
assertEquals(2, n.size());
//ARGO_ORIGINAL
assertTrue(n.get(1).isNull());
//ARGO_ORIGINAL
assertTrue(n.has(1));
//ARGO_ORIGINAL
assertFalse(n.hasNonNull(1));
        // change to text
        n.set(1, text);
//ARGO_EQUIVALENT
        assertEquivalent(text, n.get(1));
//ARGO_ORIGINAL
assertEquals(text.asText(), n.get(1).asText());
        n.set(0, null);
//ARGO_ORIGINAL
assertTrue(n.get(0).isNull());

        // and finally, clear it all
        ArrayNode n2 = new ArrayNode(JsonNodeFactory.instance);
        n2.add("foobar");
//ARGO_ORIGINAL
assertFalse(n.equals(n2));
        n.addAll(n2);
//ARGO_ORIGINAL
assertEquals(3, n.size());

//ARGO_ORIGINAL
assertFalse(n.get(0).isTextual());
//ARGO_ORIGINAL
assertNotNull(n.remove(0));
//ARGO_ORIGINAL
assertEquals(2, n.size());
//ARGO_ORIGINAL
assertTrue(n.get(0).isTextual());
//ARGO_ORIGINAL
assertNull(n.remove(-1));
//ARGO_ORIGINAL
assertNull(n.remove(100));
//ARGO_ORIGINAL
assertEquals(2, n.size());

        ArrayList<JsonNode> nodes = new ArrayList<JsonNode>();
        nodes.add(text);
        n.addAll(nodes);
//ARGO_ORIGINAL
assertEquals(3, n.size());
//ARGO_ORIGINAL
assertNull(n.get(10000));
//ARGO_ORIGINAL
assertNull(n.remove(-4));

        TextNode text2 = TextNode.valueOf("b");
        n.insert(0, text2);
//ARGO_ORIGINAL
assertEquals(4, n.size());
//ARGO_EQUIVALENT
        assertEquivalent(text2, n.get(0));
//ARGO_ORIGINAL
assertEquals(text2.asText(), n.get(0).asText());

//ARGO_ORIGINAL
assertNotNull(n.addArray());
//ARGO_ORIGINAL
assertEquals(5, n.size());
        n.addPOJO("foo");
//ARGO_ORIGINAL
assertEquals(6, n.size());

        n.removeAll();
//ARGO_ORIGINAL
assertEquals(0, n.size());
    }

    public void testDirectCreation2() throws IOException
    {
        JsonNodeFactory f = objectMapper().getNodeFactory();
        ArrayList<JsonNode> list = new ArrayList<>();
        list.add(f.booleanNode(true));
        list.add(f.textNode("foo"));
        ArrayNode n = new ArrayNode(f, list);
//ARGO_ORIGINAL
assertEquals(2, n.size());
//ARGO_ORIGINAL
assertTrue(n.get(0).isBoolean());
//ARGO_ORIGINAL
assertTrue(n.get(1).isTextual());

        // also, should fail with invalid set attempt
        try {
            n.set(2, f.nullNode());
//ARGO_ORIGINAL
fail("Should not pass");
        } catch (IndexOutOfBoundsException e) {
            verifyException(e, "illegal index");
        }
        n.insert(1, (String) null);
//ARGO_ORIGINAL
assertEquals(3, n.size());
//ARGO_ORIGINAL
assertTrue(n.get(0).isBoolean());
//ARGO_ORIGINAL
assertTrue(n.get(1).isNull());
//ARGO_ORIGINAL
assertTrue(n.get(2).isTextual());

        n.removeAll();
        n.insert(0, (JsonNode) null);
//ARGO_ORIGINAL
assertEquals(1, n.size());
//ARGO_ORIGINAL
assertTrue(n.get(0).isNull());
    }

    public void testArrayViaMapper() throws Exception
    {
        final String JSON = "[[[-0.027512,51.503221],[-0.008497,51.503221],[-0.008497,51.509744],[-0.027512,51.509744]]]";

        JsonNode n = objectMapper().readTree(JSON);
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORIGINAL
assertTrue(n.isArray());
        ArrayNode an = (ArrayNode) n;
//ARGO_ORIGINAL
assertEquals(1, an.size());
        ArrayNode an2 = (ArrayNode) n.get(0);
//ARGO_ORIGINAL
assertTrue(an2.isArray());
//ARGO_ORIGINAL
assertEquals(4, an2.size());
    }

    public void testAdds()
    {
        ArrayNode n = new ArrayNode(JsonNodeFactory.instance);
//ARGO_ORIGINAL
assertNotNull(n.addArray());
//ARGO_ORIGINAL
assertNotNull(n.addObject());
        n.addPOJO("foobar");
        n.add(1);
        n.add(1L);
        n.add(0.5);
        n.add(0.5f);
        n.add(new BigDecimal("0.2"));
        n.add(BigInteger.TEN);
//ARGO_ORIGINAL
assertEquals(9, n.size());
//ARGO_ORIGINAL
assertFalse(n.isEmpty());

//ARGO_ORIGINAL
assertNotNull(n.insertArray(0));
//ARGO_ORIGINAL
assertNotNull(n.insertObject(0));
        n.insertPOJO(2, "xxx");
//ARGO_ORIGINAL
assertEquals(12, n.size());

        n.insert(0, BigInteger.ONE);
        n.insert(0, new BigDecimal("0.1"));
//ARGO_ORIGINAL
assertEquals(14, n.size());
    }

    public void testNullAdds()
    {
        JsonNodeFactory f = objectMapper().getNodeFactory();
        ArrayNode array = f.arrayNode(14);

        array.add((BigDecimal) null);
        array.add((BigInteger) null);
        array.add((Boolean) null);
        array.add((byte[]) null);
        array.add((Double) null);
        array.add((Float) null);
        array.add((Integer) null);
        array.add((JsonNode) null);
        array.add((Long) null);
        array.add((String) null);

//ARGO_ORIGINAL
assertEquals(10, array.size());
        
        for (JsonNode node : array) {
//ARGO_ORIGINAL
assertTrue(node.isNull());
        }
    }

    public void testAddAllWithNullInCollection()
    {
        // preparation
        final ArrayNode array = JsonNodeFactory.instance.arrayNode();

        // test
        array.addAll(asList(null, JsonNodeFactory.instance.objectNode()));

        //assertions
//ARGO_ORIGINAL
assertEquals(2, array.size());

        for (JsonNode node : array) {
//ARGO_ORIGINAL
assertNotNull(node);
        }
//ARGO_ORIGINAL
assertEquals(NullNode.getInstance(), array.get(0));
    }

    public void testNullInserts()
    {
        JsonNodeFactory f = objectMapper().getNodeFactory();
        ArrayNode array = f.arrayNode(3);

        array.insert(0, (BigDecimal) null);
        array.insert(0, (BigInteger) null);
        array.insert(0, (Boolean) null);
        // Offsets out of the range are fine; negative become 0;
        // super big just add at the end
        array.insert(-56, (byte[]) null);
        array.insert(0, (Double) null);
        array.insert(200, (Float) null);
        array.insert(0, (Integer) null);
        array.insert(1, (JsonNode) null);
        array.insert(array.size(), (Long) null);
        array.insert(1, (String) null);

//ARGO_ORIGINAL
assertEquals(10, array.size());
        
        for (JsonNode node : array) {
//ARGO_ORIGINAL
assertTrue(node.isNull());
        }
    }
    
    public void testNullChecking()
    {
        ArrayNode a1 = JsonNodeFactory.instance.arrayNode();
        ArrayNode a2 = JsonNodeFactory.instance.arrayNode();
        // used to throw NPE before fix:
        a1.addAll(a2);
//ARGO_ORIGINAL
assertEquals(0, a1.size());
//ARGO_ORIGINAL
assertEquals(0, a2.size());

        a2.addAll(a1);
//ARGO_ORIGINAL
assertEquals(0, a1.size());
//ARGO_ORIGINAL
assertEquals(0, a2.size());
    }

    public void testNullChecking2()
    {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode src = mapper.createArrayNode();
        ArrayNode dest = mapper.createArrayNode();
        src.add("element");
        dest.addAll(src);
    }
    
    public void testParser() throws Exception
    {
        ArrayNode n = new ArrayNode(JsonNodeFactory.instance);
        n.add(123);
        TreeTraversingParser p = new TreeTraversingParser(n, null);
        p.setCodec(null);
//ARGO_ORIGINAL
assertNull(p.getCodec());
//ARGO_ORIGINAL
assertNotNull(p.getParsingContext());
//ARGO_ORIGINAL
assertTrue(p.getParsingContext().inRoot());
//ARGO_ORIGINAL
assertNotNull(p.getTokenLocation());
//ARGO_ORIGINAL
assertNotNull(p.getCurrentLocation());
//ARGO_ORIGINAL
assertNull(p.getEmbeddedObject());
//ARGO_ORIGINAL
assertNull(p.currentNode());

        //assertNull(p.getNumberType());

//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertNotNull(p.getParsingContext());
//ARGO_ORIGINAL
assertTrue(p.getParsingContext().inArray());
        p.skipChildren();
//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.currentToken());
        p.close();

        p = new TreeTraversingParser(n, null);
        p.nextToken();
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertEquals(JsonParser.NumberType.INT, p.getNumberType());
        p.close();
    }

    public void testArrayNodeEquality()
    {
        ArrayNode n1 = new ArrayNode(null);
        ArrayNode n2 = new ArrayNode(null);

        //ARGO_EQUIVALENT
assertEquivalent(n1,n2);

        n1.add(TextNode.valueOf("Test"));

        //ARGO_EQUIVALENT
assertNonEquivalent(n1, n2);

        n2.add(TextNode.valueOf("Test"));

        //ARGO_EQUIVALENT
assertEquivalent(n1,n2);
    }

    public void testSimpleArray() throws Exception
    {
        ArrayNode result = objectMapper().createArrayNode();

//ARGO_ORIGINAL
assertTrue(result.isArray());
//ARGO_ORIGINAL
assertType(result, ArrayNode.class);

//ARGO_ORIGINAL
assertFalse(result.isObject());
//ARGO_ORIGINAL
assertFalse(result.isNumber());
//ARGO_ORIGINAL
assertFalse(result.isNull());
//ARGO_ORIGINAL
assertFalse(result.isTextual());

        // and let's add stuff...
        result.add(false);
        result.insertNull(0);

        // should be equal to itself no matter what
//ARGO_ORIGINAL
assertEquals(result, result);
//ARGO_ORIGINAL
assertFalse(result.equals(null)); // but not to null

        // plus see that we can access stuff
//ARGO_ORIGINAL
assertEquals(NullNode.instance, result.path(0));
//ARGO_ORIGINAL
assertEquals(NullNode.instance, result.get(0));
//ARGO_ORIGINAL
assertEquals(BooleanNode.FALSE, result.path(1));
//ARGO_ORIGINAL
assertEquals(BooleanNode.FALSE, result.get(1));
//ARGO_ORIGINAL
assertEquals(2, result.size());

//ARGO_ORIGINAL
assertNull(result.get(-1));
//ARGO_ORIGINAL
assertNull(result.get(2));
        JsonNode missing = result.path(2);
//ARGO_ORIGINAL
assertTrue(missing.isMissingNode());
//ARGO_ORIGINAL
assertTrue(result.path(-100).isMissingNode());

        // then construct and compare
        ArrayNode array2 = objectMapper().createArrayNode();
        array2.addNull();
        array2.add(false);
        //ARGO_EQUIVALENT
assertEquivalent(result, array2);

        // plus remove entries
        JsonNode rm1 = array2.remove(0);
//ARGO_ORIGINAL
assertEquals(NullNode.instance, rm1);
//ARGO_ORIGINAL
assertEquals(1, array2.size());
//ARGO_ORIGINAL
assertEquals(BooleanNode.FALSE, array2.get(0));
//ARGO_ORIGINAL
assertFalse(result.equals(array2));

        JsonNode rm2 = array2.remove(0);
//ARGO_ORIGINAL
assertEquals(BooleanNode.FALSE, rm2);
//ARGO_ORIGINAL
assertEquals(0, array2.size());
    }

    public void testSimpleMismatch() throws Exception
    {
        ObjectMapper mapper = objectMapper();
        try {
            mapper.readValue(" 123 ", ArrayNode.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "from Integer value (token `JsonToken.VALUE_NUMBER_INT`)");
        }
    }
}
