package com.fasterxml.jackson.databind.node;

public class TextNodeTest extends NodeTestBase
{
    public void testText()
    {
//ARGO_PLACEBO
assertNull(TextNode.valueOf(null));
        TextNode empty = TextNode.valueOf("");
//ARGO_PLACEBO
assertStandardEquals(empty);
//ARGO_PLACEBO
assertSame(TextNode.EMPTY_STRING_NODE, empty);

//ARGO_PLACEBO
assertEquals(0, empty.size());
//ARGO_PLACEBO
assertTrue(empty.isEmpty());

//ARGO_PLACEBO
assertNodeNumbers(TextNode.valueOf("-3"), -3, -3.0);
//ARGO_PLACEBO
assertNodeNumbers(TextNode.valueOf("17.75"), 17, 17.75);
    
        long value = 127353264013893L;
        TextNode n = TextNode.valueOf(String.valueOf(value));
//ARGO_PLACEBO
assertEquals(value, n.asLong());
        
        // and then with non-numeric input
        n = TextNode.valueOf("foobar");
//ARGO_PLACEBO
assertNodeNumbersForNonNumeric(n);

//ARGO_PLACEBO
assertEquals("foobar", n.asText("barf"));
//ARGO_PLACEBO
assertEquals("", empty.asText("xyz"));

//ARGO_PLACEBO
assertTrue(TextNode.valueOf("true").asBoolean(true));
//ARGO_PLACEBO
assertTrue(TextNode.valueOf("true").asBoolean(false));
//ARGO_PLACEBO
assertFalse(TextNode.valueOf("false").asBoolean(true));
//ARGO_PLACEBO
assertFalse(TextNode.valueOf("false").asBoolean(false));
    }
}
