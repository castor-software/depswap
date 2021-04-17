package com.fasterxml.jackson.databind.node;

import java.util.Comparator;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.testutil.NoCheckSubTypeValidator;
import com.fasterxml.jackson.databind.util.RawValue;

/**
 * Basic tests for {@link JsonNode} base class and some features
 * of implementation classes
 */
public class TestJsonNode extends NodeTestBase
{
    private final ObjectMapper MAPPER = objectMapper();

    public void testBoolean() throws Exception
    {
        BooleanNode f = BooleanNode.getFalse();
//ARGO_PLACEBO
assertNotNull(f);
//ARGO_PLACEBO
assertTrue(f.isBoolean());
//ARGO_PLACEBO
assertSame(f, BooleanNode.valueOf(false));
//ARGO_PLACEBO
assertStandardEquals(f);
//ARGO_PLACEBO
assertFalse(f.booleanValue());
//ARGO_PLACEBO
assertFalse(f.asBoolean());
//ARGO_PLACEBO
assertEquals("false", f.asText());
//ARGO_PLACEBO
assertEquals(JsonToken.VALUE_FALSE, f.asToken());

        // and ditto for true
        BooleanNode t = BooleanNode.getTrue();
//ARGO_PLACEBO
assertNotNull(t);
//ARGO_PLACEBO
assertTrue(t.isBoolean());
//ARGO_PLACEBO
assertSame(t, BooleanNode.valueOf(true));
//ARGO_PLACEBO
assertStandardEquals(t);
//ARGO_PLACEBO
assertTrue(t.booleanValue());
//ARGO_PLACEBO
assertTrue(t.asBoolean());
//ARGO_PLACEBO
assertEquals("true", t.asText());
//ARGO_PLACEBO
assertEquals(JsonToken.VALUE_TRUE, t.asToken());

//ARGO_PLACEBO
assertNodeNumbers(f, 0, 0.0);
//ARGO_PLACEBO
assertNodeNumbers(t, 1, 1.0);
    
        JsonNode result = objectMapper().readTree("true\n");
//ARGO_PLACEBO
assertFalse(result.isNull());
//ARGO_PLACEBO
assertFalse(result.isNumber());
//ARGO_PLACEBO
assertFalse(result.isTextual());
//ARGO_PLACEBO
assertTrue(result.isBoolean());
//ARGO_PLACEBO
assertType(result, BooleanNode.class);
//ARGO_PLACEBO
assertTrue(result.booleanValue());
//ARGO_PLACEBO
assertEquals("true", result.asText());
//ARGO_PLACEBO
assertFalse(result.isMissingNode());

        // also, equality should work ok
//ARGO_PLACEBO
assertEquals(result, BooleanNode.valueOf(true));
//ARGO_PLACEBO
assertEquals(result, BooleanNode.getTrue());
    }

    public void testBinary() throws Exception
    {
//ARGO_PLACEBO
assertNull(BinaryNode.valueOf(null));
//ARGO_PLACEBO
assertNull(BinaryNode.valueOf(null, 0, 0));

        BinaryNode empty = BinaryNode.valueOf(new byte[1], 0, 0);
//ARGO_PLACEBO
assertSame(BinaryNode.EMPTY_BINARY_NODE, empty);
//ARGO_PLACEBO
assertStandardEquals(empty);

        byte[] data = new byte[3];
        data[1] = (byte) 3;
        BinaryNode n = BinaryNode.valueOf(data, 1, 1);
        data[2] = (byte) 3;
        BinaryNode n2 = BinaryNode.valueOf(data, 2, 1);
//ARGO_PLACEBO
assertTrue(n.equals(n2));
//ARGO_PLACEBO
assertEquals("\"Aw==\"", n.toString());

//ARGO_PLACEBO
assertEquals("AAMD", new BinaryNode(data).asText());
//ARGO_PLACEBO
assertNodeNumbersForNonNumeric(n);
    }

    public void testPOJO()
    {
        POJONode n = new POJONode("x"); // not really a pojo but that's ok
//ARGO_PLACEBO
assertStandardEquals(n);
//ARGO_PLACEBO
assertEquals(n, new POJONode("x"));
//ARGO_PLACEBO
assertEquals("x", n.asText());
        // 10-Dec-2018, tatu: With 2.10, should serialize same as via ObjectMapper/ObjectWriter
//ARGO_PLACEBO
assertEquals("\"x\"", n.toString());

//ARGO_PLACEBO
assertEquals(new POJONode(null), new POJONode(null));

        // default; non-numeric
//ARGO_PLACEBO
assertNodeNumbersForNonNumeric(n);
        // but if wrapping actual number, use it
//ARGO_PLACEBO
assertNodeNumbers(new POJONode(Integer.valueOf(123)), 123, 123.0);
    }

    // [databind#743]
    public void testRawValue() throws Exception
    {
        ObjectNode root = MAPPER.createObjectNode();
        root.putRawValue("a", new RawValue(new SerializedString("[1, 2, 3]")));

//ARGO_ORIGINAL
assertEquals("{\"a\":[1, 2, 3]}", MAPPER.writeValueAsString(root));
    }

    // [databind#790]
    public void testCustomComparators() throws Exception
    {
        ObjectNode nestedObject1 = MAPPER.createObjectNode();
        nestedObject1.put("value", 6);
        ArrayNode nestedArray1 = MAPPER.createArrayNode();
        nestedArray1.add(7);
        ObjectNode root1 = MAPPER.createObjectNode();
        root1.put("value", 5);
        root1.set("nested_object", nestedObject1);
        root1.set("nested_array", nestedArray1);

        ObjectNode nestedObject2 = MAPPER.createObjectNode();
        nestedObject2.put("value", 6.9);
        ArrayNode nestedArray2 = MAPPER.createArrayNode();
        nestedArray2.add(7.0);
        ObjectNode root2 = MAPPER.createObjectNode();
        root2.put("value", 5.0);
        root2.set("nested_object", nestedObject2);
        root2.set("nested_array", nestedArray2);

        // default equals(): not strictly equal
//ARGO_ORIGINAL
assertFalse(root1.equals(root2));
//ARGO_ORIGINAL
assertFalse(root2.equals(root1));
//ARGO_ORIGINAL
assertTrue(root1.equals(root1));
//ARGO_ORIGINAL
assertTrue(root2.equals(root2));

//ARGO_ORIGINAL
assertTrue(nestedArray1.equals(nestedArray1));
//ARGO_ORIGINAL
assertFalse(nestedArray1.equals(nestedArray2));
//ARGO_ORIGINAL
assertFalse(nestedArray2.equals(nestedArray1));

        // but. Custom comparator can make all the difference
        Comparator<JsonNode> cmp = new Comparator<JsonNode>() {

            @Override
            public int compare(JsonNode o1, JsonNode o2) {
                if (o1 instanceof ContainerNode || o2 instanceof ContainerNode) {
//ARGO_ORIGINAL
fail("container nodes should be traversed, comparator should not be invoked");
                }
                if (o1.equals(o2)) {
                    return 0;
                }
                if ((o1 instanceof NumericNode) && (o2 instanceof NumericNode)) {
                    int d1 = ((NumericNode) o1).asInt();
                    int d2 = ((NumericNode) o2).asInt();
                    if (d1 == d2) { // strictly equals because it's integral value
                        return 0;
                    }
                    if (d1 < d2) {
                        return -1;
                    }
                    return 1;
                }
                return 0;
            }
        };
//ARGO_ORIGINAL
assertTrue(root1.equals(cmp, root2));
//ARGO_ORIGINAL
assertTrue(root2.equals(cmp, root1));
//ARGO_ORIGINAL
assertTrue(root1.equals(cmp, root1));
//ARGO_ORIGINAL
assertTrue(root2.equals(cmp, root2));

        ArrayNode array3 = MAPPER.createArrayNode();
        array3.add(123);

//ARGO_ORIGINAL
assertFalse(root2.equals(cmp, nestedArray1));
//ARGO_ORIGINAL
assertTrue(nestedArray1.equals(cmp, nestedArray1));
//ARGO_ORIGINAL
assertFalse(nestedArray1.equals(cmp, root2));
//ARGO_ORIGINAL
assertFalse(nestedArray1.equals(cmp, array3));
    }

    // [databind#793]
    public void testArrayWithDefaultTyping() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper()
            .activateDefaultTyping(NoCheckSubTypeValidator.instance);

        JsonNode array = mapper.readTree("[ 1, 2 ]");
//ARGO_ORIGINAL
assertTrue(array.isArray());
//ARGO_ORIGINAL
assertEquals(2, array.size());

        JsonNode obj = mapper.readTree("{ \"a\" : 2 }");
//ARGO_ORIGINAL
assertTrue(obj.isObject());
//ARGO_ORIGINAL
assertEquals(1, obj.size());
//ARGO_ORIGINAL
assertEquals(2, obj.path("a").asInt());
    }
}
