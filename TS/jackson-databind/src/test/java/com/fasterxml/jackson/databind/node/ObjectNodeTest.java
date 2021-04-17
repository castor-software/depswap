package com.fasterxml.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import static com.fasterxml.jackson.databind.JSONTestUtils.//ARGO_ORIGINAL
assertEquivalent;

/**
 * Additional tests for {@link ObjectNode} container class.
 */
public class ObjectNodeTest
    extends BaseMapTest
{
    @JsonDeserialize(as = DataImpl.class)
    public interface Data {
    }

    public static class DataImpl implements Data
    {
        protected JsonNode root;

        @JsonCreator
        public DataImpl(JsonNode n) {
            root = n;
        }
        
        @JsonValue
        public JsonNode value() { return root; }
        
        /*
        public Wrapper(ObjectNode n) { root = n; }
        
        @JsonValue
        public ObjectNode value() { return root; }
        */
    }

    static class ObNodeWrapper {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public ObjectNode node;

        protected ObNodeWrapper() { }
        public ObNodeWrapper(ObjectNode n) {
            node = n;
        }
    }

    // [databind#941]
    static class MyValue
    {
        private final ObjectNode object;

        @JsonCreator
        public MyValue(ObjectNode object) { this.object = object; }

        @JsonValue
        public ObjectNode getObject() { return object; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = sharedMapper();

    public void testSimpleObject() throws Exception
    {
        String JSON = "{ \"key\" : 1, \"b\" : \"x\" }";
        JsonNode root = MAPPER.readTree(JSON);

        // basic properties first:
//ARGO_ORIGINAL
assertFalse(root.isValueNode());
//ARGO_ORIGINAL
assertTrue(root.isContainerNode());
//ARGO_ORIGINAL
assertFalse(root.isArray());
//ARGO_ORIGINAL
assertTrue(root.isObject());
//ARGO_ORIGINAL
assertEquals(2, root.size());
//ARGO_ORIGINAL
assertFalse(root.isEmpty());

        Iterator<JsonNode> it = root.iterator();
//ARGO_ORIGINAL
assertNotNull(it);
//ARGO_ORIGINAL
assertTrue(it.hasNext());
        JsonNode n = it.next();
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORDER
        //assertEquivalent(IntNode.valueOf(1), n);
//ARGO_ORIGINAL
assertTrue(n.asText("ERROR").equals("x") || n.asInt(0) == 1);

//ARGO_ORIGINAL
assertTrue(it.hasNext());
        n = it.next();
//ARGO_ORIGINAL
assertNotNull(n);
        //ARGO_ORDER
//assertEquals(TextNode.valueOf("x"), n);
//ARGO_ORIGINAL
assertTrue(n.asText("ERROR").equals("x") || n.asInt(0) == 1);

//ARGO_ORIGINAL
assertFalse(it.hasNext());

        // Ok, then, let's traverse via extended interface
        ObjectNode obNode = (ObjectNode) root;
        Iterator<Map.Entry<String,JsonNode>> fit = obNode.fields();
        // we also know that LinkedHashMap is used, i.e. order preserved
//ARGO_ORIGINAL
assertTrue(fit.hasNext());
        Map.Entry<String,JsonNode> en = fit.next();
//ARGO_ORIGINAL
assertTrue(
                (en.getKey().equals("key") && en.getValue().equals(IntNode.valueOf(1))) ||
                (en.getKey().equals("b") && en.getValue().equals(TextNode.valueOf("x")))
            );
        //ARGO_ORDER
//assertEquals("key", en.getKey());
        //ARGO_ORDER
//assertEquals(IntNode.valueOf(1), en.getValue());

//ARGO_ORIGINAL
assertTrue(fit.hasNext());
        en = fit.next();
//ARGO_ORIGINAL
assertTrue(
                (en.getKey().equals("key") && en.getValue().equals(IntNode.valueOf(1))) ||
                        (en.getKey().equals("b") && en.getValue().equals(TextNode.valueOf("x")))
        );
        //ARGO_ORDER
//assertEquals("b", en.getKey());
        //ARGO_ORDER
//assertEquals(TextNode.valueOf("x"), en.getValue());

        // Plus: we should be able to modify the node via iterator too:
        fit.remove();
//ARGO_ORIGINAL
assertEquals(1, obNode.size());
        //ARGO_ORDER
//assertEquals(IntNode.valueOf(1), root.get("key"));
//ARGO_ORIGINAL
assertTrue(
                (IntNode.valueOf(1).equals(root.get("key")) && root.get("b") == null) ||
                        (TextNode.valueOf("x").equals(root.get("b")) && root.get("key") == null)
        );
        //ARGO_ORDER
//assertNull(root.get("b"));
    }    
    // for [databind#346]
    public void testEmptyNodeAsValue() throws Exception
    {
        Data w = MAPPER.readValue("{}", Data.class);
//ARGO_ORIGINAL
assertNotNull(w);
    }
    
    public void testBasics()
    {
        ObjectNode n = new ObjectNode(JsonNodeFactory.instance);
//ARGO_ORIGINAL
assertStandardEquals(n);
//ARGO_ORIGINAL
assertTrue(n.isEmpty());
        
//ARGO_ORIGINAL
assertFalse(n.elements().hasNext());
//ARGO_ORIGINAL
assertFalse(n.fields().hasNext());
//ARGO_ORIGINAL
assertFalse(n.fieldNames().hasNext());
//ARGO_ORIGINAL
assertNull(n.get("a"));
//ARGO_ORIGINAL
assertTrue(n.path("a").isMissingNode());

        TextNode text = TextNode.valueOf("x");
//ARGO_ORIGINAL
assertSame(n, n.set("a", text));
        
//ARGO_ORIGINAL
assertEquals(1, n.size());
//ARGO_ORIGINAL
assertTrue(n.elements().hasNext());
//ARGO_ORIGINAL
assertTrue(n.fields().hasNext());
//ARGO_ORIGINAL
assertTrue(n.fieldNames().hasNext());
//ARGO_ORIGINAL
assertEquals(text.asText(), n.get("a").asText());
//ARGO_ORIGINAL
assertEquals(text.asText(), n.path("a").asText());
//ARGO_EQUIVALENT
        assertEquivalent(text, n.get("a"));
        //ARGO_EQUIVALENT
        assertEquivalent(text, n.path("a"));
//ARGO_ORIGINAL
assertNull(n.get("b"));
//ARGO_ORIGINAL
assertNull(n.get(0)); // not used with objects

//ARGO_ORIGINAL
assertFalse(n.has(0));
//ARGO_ORIGINAL
assertFalse(n.hasNonNull(0));
//ARGO_ORIGINAL
assertTrue(n.has("a"));
//ARGO_ORIGINAL
assertTrue(n.hasNonNull("a"));
//ARGO_ORIGINAL
assertFalse(n.has("b"));
//ARGO_ORIGINAL
assertFalse(n.hasNonNull("b"));

        ObjectNode n2 = new ObjectNode(JsonNodeFactory.instance);
        n2.put("b", 13);
//ARGO_ORIGINAL
assertFalse(n.equals(n2));
        n.setAll(n2);
        
//ARGO_ORIGINAL
assertEquals(2, n.size());
        n.set("null", (JsonNode)null);
//ARGO_ORIGINAL
assertEquals(3, n.size());
        // may be non-intuitive, but explicit nulls do exist in tree:
//ARGO_ORIGINAL
assertTrue(n.has("null"));
//ARGO_ORIGINAL
assertFalse(n.hasNonNull("null"));
        // should replace, not add
        n.put("null", "notReallNull");
//ARGO_ORIGINAL
assertEquals(3, n.size());
//ARGO_ORIGINAL
assertNotNull(n.remove("null"));
//ARGO_ORIGINAL
assertEquals(2, n.size());

        Map<String,JsonNode> nodes = new HashMap<String,JsonNode>();
        nodes.put("d", text);
        n.setAll(nodes);
//ARGO_ORIGINAL
assertEquals(3, n.size());

        n.removeAll();
//ARGO_ORIGINAL
assertEquals(0, n.size());
    }

    public void testBigNumbers()
    {
        ObjectNode n = new ObjectNode(JsonNodeFactory.instance);
//ARGO_ORIGINAL
assertStandardEquals(n);
        BigInteger I = BigInteger.valueOf(3);
        BigDecimal DEC = new BigDecimal("0.1");

        n.put("a", DEC);
        n.put("b", I);

//ARGO_ORIGINAL
assertEquals(2, n.size());

//ARGO_ORIGINAL
assertTrue(n.path("a").isBigDecimal());
//ARGO_ORIGINAL
assertEquals(DEC, n.get("a").decimalValue());
//ARGO_ORIGINAL
assertTrue(n.path("b").isBigInteger());
//ARGO_ORIGINAL
assertEquals(I, n.get("b").bigIntegerValue());
    }

    /**
     * Verify null handling
     */
    public void testNullChecking()
    {
        ObjectNode o1 = JsonNodeFactory.instance.objectNode();
        ObjectNode o2 = JsonNodeFactory.instance.objectNode();
        // used to throw NPE before fix:
        o1.setAll(o2);
//ARGO_ORIGINAL
assertEquals(0, o1.size());
//ARGO_ORIGINAL
assertEquals(0, o2.size());

        // also: nulls should be converted to NullNodes...
        o1.set("x", null);
        JsonNode n = o1.get("x");
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORIGINAL
assertSame(n, NullNode.instance);

        o1.put("str", (String) null);
        n = o1.get("str");
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORIGINAL
assertSame(n, NullNode.instance);

        o1.put("d", (BigDecimal) null);
        n = o1.get("d");
//ARGO_ORIGINAL
assertNotNull(n);
//ARGO_ORIGINAL
assertSame(n, NullNode.instance);

        o1.put("3", (BigInteger) null);
        n = o1.get("3");
//ARGO_ORIGINAL
assertNotNull(3);
//ARGO_ORIGINAL
assertSame(n, NullNode.instance);

//ARGO_ORIGINAL
assertEquals(4, o1.size());
    }

    /**
     * Another test to verify [JACKSON-227]...
     */
    public void testNullChecking2()
    {
        ObjectNode src = MAPPER.createObjectNode();
        ObjectNode dest = MAPPER.createObjectNode();
        src.put("a", "b");
        dest.setAll(src);
    }

    public void testRemove()
    {
        ObjectNode ob = MAPPER.createObjectNode();
        ob.put("a", "a");
        ob.put("b", "b");
        ob.put("c", "c");
//ARGO_ORIGINAL
assertEquals(3, ob.size());
//ARGO_ORIGINAL
assertSame(ob, ob.without(Arrays.asList("a", "c")));
//ARGO_ORIGINAL
assertEquals(1, ob.size());
//ARGO_ORIGINAL
assertEquals("b", ob.get("b").textValue());
    }

    public void testRetain()
    {
        ObjectNode ob = MAPPER.createObjectNode();
        ob.put("a", "a");
        ob.put("b", "b");
        ob.put("c", "c");
//ARGO_ORIGINAL
assertEquals(3, ob.size());
//ARGO_ORIGINAL
assertSame(ob, ob.retain("a", "c"));
//ARGO_ORIGINAL
assertEquals(2, ob.size());
//ARGO_ORIGINAL
assertEquals("a", ob.get("a").textValue());
//ARGO_ORIGINAL
assertNull(ob.get("b"));
//ARGO_ORIGINAL
assertEquals("c", ob.get("c").textValue());
    }

    public void testValidWith() throws Exception
    {
        ObjectNode root = MAPPER.createObjectNode();
//ARGO_ORIGINAL
assertEquals("{}", MAPPER.writeValueAsString(root));
        JsonNode child = root.with("prop");
//ARGO_ORIGINAL
assertTrue(child instanceof ObjectNode);
//ARGO_ORIGINAL
assertEquals("{\"prop\":{}}", MAPPER.writeValueAsString(root));
    }

    public void testValidWithArray() throws Exception
    {
        JsonNode root = MAPPER.createObjectNode();
//ARGO_ORIGINAL
assertEquals("{}", MAPPER.writeValueAsString(root));
        ArrayNode child = root.withArray("arr");
//ARGO_ORIGINAL
assertTrue(child instanceof ArrayNode);
//ARGO_ORIGINAL
assertEquals("{\"arr\":[]}", MAPPER.writeValueAsString(root));
    }

    public void testInvalidWith() throws Exception
    {
        JsonNode root = MAPPER.createArrayNode();
        try { // should not work for non-ObjectNode nodes:
            root.with("prop");
//ARGO_ORIGINAL
fail("Expected exception");
        } catch (UnsupportedOperationException e) {
            verifyException(e, "not of type ObjectNode");
        }
        // also: should fail of we already have non-object property
        ObjectNode root2 = MAPPER.createObjectNode();
        root2.put("prop", 13);
        try { // should not work for non-ObjectNode nodes:
            root2.with("prop");
//ARGO_ORIGINAL
fail("Expected exception");
        } catch (UnsupportedOperationException e) {
            verifyException(e, "has value that is not");
        }
    }

    public void testInvalidWithArray() throws Exception
    {
        JsonNode root = MAPPER.createArrayNode();
        try { // should not work for non-ObjectNode nodes:
            root.withArray("prop");
//ARGO_ORIGINAL
fail("Expected exception");
        } catch (UnsupportedOperationException e) {
            verifyException(e, "not of type ObjectNode");
        }
        // also: should fail of we already have non-Array property
        ObjectNode root2 = MAPPER.createObjectNode();
        root2.put("prop", 13);
        try { // should not work for non-ObjectNode nodes:
            root2.withArray("prop");
//ARGO_ORIGINAL
fail("Expected exception");
        } catch (UnsupportedOperationException e) {
            verifyException(e, "has value that is not");
        }
    }

    public void testSetAll() throws Exception
    {
        ObjectNode root = MAPPER.createObjectNode();
//ARGO_ORIGINAL
assertEquals(0, root.size());
        HashMap<String,JsonNode> map = new HashMap<String,JsonNode>();
        map.put("a", root.numberNode(1));
        root.setAll(map);
//ARGO_ORIGINAL
assertEquals(1, root.size());
//ARGO_ORIGINAL
assertTrue(root.has("a"));
//ARGO_ORIGINAL
assertFalse(root.has("b"));

        map.put("b", root.numberNode(2));
        root.setAll(map);
//ARGO_ORIGINAL
assertEquals(2, root.size());
//ARGO_ORIGINAL
assertTrue(root.has("a"));
//ARGO_ORIGINAL
assertTrue(root.has("b"));
//ARGO_ORIGINAL
assertEquals(2, root.path("b").intValue());

        // Then with ObjectNodes...
        ObjectNode root2 = MAPPER.createObjectNode();
        root2.setAll(root);
//ARGO_ORIGINAL
assertEquals(2, root.size());
//ARGO_ORIGINAL
assertEquals(2, root2.size());

        root2.setAll(root);
//ARGO_ORIGINAL
assertEquals(2, root.size());
//ARGO_ORIGINAL
assertEquals(2, root2.size());

        ObjectNode root3 = MAPPER.createObjectNode();
        root3.put("a", 2);
        root3.put("c", 3);
//ARGO_ORIGINAL
assertEquals(2, root3.path("a").intValue());
        root3.setAll(root2);
//ARGO_ORIGINAL
assertEquals(3, root3.size());
//ARGO_ORIGINAL
assertEquals(1, root3.path("a").intValue());
    }

    // [databind#237] (databind): support DeserializationFeature#FAIL_ON_READING_DUP_TREE_KEY
    public void testFailOnDupKeys() throws Exception
    {
        final String DUP_JSON = "{ \"a\":1, \"a\":2 }";
        
        // first: verify defaults:
//ARGO_ORIGINAL
assertFalse(MAPPER.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY));
        ObjectNode root = (ObjectNode) MAPPER.readTree(DUP_JSON);
//ARGO_ORIGINAL
assertEquals(2, root.path("a").asInt());
        
        // and then enable checks:
        try {
            MAPPER.reader(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY).readTree(DUP_JSON);
//ARGO_ORIGINAL
fail("Should have thrown exception!");
        } catch (JsonMappingException e) {
            verifyException(e, "duplicate field 'a'");
        }
    }

    public void testFailOnDupNestedKeys() throws Exception
    {
        final String DOC = aposToQuotes(
                "{'node' : { 'data' : [ 1, 2, { 'a':3 }, { 'foo' : 1, 'bar' : 2, 'foo': 3}]}}"
        );
        try {
            MAPPER.readerFor(ObNodeWrapper.class)
                .with(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
                .readValue(DOC);
//ARGO_ORIGINAL
fail("Should have thrown exception!");
        } catch (JsonMappingException e) {
            //verifyException(e, "duplicate field 'foo'");
        }
    }

    public void testEqualityWrtOrder() throws Exception
    {
        ObjectNode ob1 = MAPPER.createObjectNode();
        ObjectNode ob2 = MAPPER.createObjectNode();

        // same contents, different insertion order; should not matter
        
        ob1.put("a", 1);
        ob1.put("b", 2);
        ob1.put("c", 3);

        ob2.put("b", 2);
        ob2.put("c", 3);
        ob2.put("a", 1);

        //ARGO_EQUIVALENT
assertEquivalent(ob1,ob2);
    }

    public void testSimplePath() throws Exception
    {
        JsonNode root = MAPPER.readTree("{ \"results\" : { \"a\" : 3 } }");
//ARGO_ORIGINAL
assertTrue(root.isObject());
        JsonNode rnode = root.path("results");
//ARGO_ORIGINAL
assertNotNull(rnode);
//ARGO_ORIGINAL
assertTrue(rnode.isObject());
//ARGO_ORIGINAL
assertEquals(3, rnode.path("a").intValue());
    }

    public void testNonEmptySerialization() throws Exception
    {
        ObNodeWrapper w = new ObNodeWrapper(MAPPER.createObjectNode()
                .put("a", 3));
//ARGO_ORIGINAL
assertEquals("{\"node\":{\"a\":3}}", MAPPER.writeValueAsString(w));
        w = new ObNodeWrapper(MAPPER.createObjectNode());
//ARGO_ORIGINAL
assertEquals("{}", MAPPER.writeValueAsString(w));
    }

    public void testIssue941() throws Exception
    {
        ObjectNode object = MAPPER.createObjectNode();

        String json = MAPPER.writeValueAsString(object);
//        System.out.println("json: "+json);

        ObjectNode de1 = MAPPER.readValue(json, ObjectNode.class);  // this works
//        System.out.println("Deserialized to ObjectNode: "+de1);
//ARGO_ORIGINAL
assertNotNull(de1);

        MyValue de2 = MAPPER.readValue(json, MyValue.class);  // but this throws exception
//        System.out.println("Deserialized to MyValue: "+de2);
//ARGO_ORIGINAL
assertNotNull(de2);
    }

    public void testSimpleMismatch() throws Exception
    {
        ObjectMapper mapper = objectMapper();
        try {
            mapper.readValue("[ 1, 2, 3 ]", ObjectNode.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "from Array value (token `JsonToken.START_ARRAY`)");
        }
    }
}
