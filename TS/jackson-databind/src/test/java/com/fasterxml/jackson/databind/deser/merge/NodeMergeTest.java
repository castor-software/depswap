package com.fasterxml.jackson.databind.deser.merge;

import com.fasterxml.jackson.annotation.JsonMerge;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class NodeMergeTest extends BaseMapTest
{
    final static ObjectMapper MAPPER = jsonMapperBuilder()
            // 26-Oct-2016, tatu: Make sure we'll report merge problems by default
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .build();

    static class ObjectNodeWrapper {
        @JsonMerge
        public ObjectNode props = MAPPER.createObjectNode();
        {
            props.put("default", "enabled");
        }
    }

    static class ArrayNodeWrapper {
        @JsonMerge
        public ArrayNode list = MAPPER.createArrayNode();
        {
            list.add(123);
        }
    }

    /*
    /********************************************************
    /* Test methods
    /********************************************************
     */

    public void testObjectNodeUpdateValue() throws Exception
    {
        ObjectNode base = MAPPER.createObjectNode();
        base.put("first", "foo");
//ARGO_ORIGINAL
assertSame(base,
                MAPPER.readerForUpdating(base)
                .readValue(aposToQuotes("{'second':'bar', 'third':5, 'fourth':true}")));
//ARGO_ORIGINAL
assertEquals(4, base.size());
//ARGO_ORIGINAL
assertEquals("bar", base.path("second").asText());
//ARGO_ORIGINAL
assertEquals("foo", base.path("first").asText());
//ARGO_ORIGINAL
assertEquals(5, base.path("third").asInt());
//ARGO_ORIGINAL
assertTrue(base.path("fourth").asBoolean());
    }

    public void testObjectNodeMerge() throws Exception
    {
        ObjectNodeWrapper w = MAPPER.readValue(aposToQuotes("{'props':{'stuff':'xyz'}}"),
                ObjectNodeWrapper.class);
//ARGO_ORIGINAL
assertEquals(2, w.props.size());
//ARGO_ORIGINAL
assertEquals("enabled", w.props.path("default").asText());
//ARGO_ORIGINAL
assertEquals("xyz", w.props.path("stuff").asText());
    }

    public void testObjectDeepUpdate() throws Exception
    {
        ObjectNode base = MAPPER.createObjectNode();
        ObjectNode props = base.putObject("props");
        props.put("base", 123);
        props.put("value", 456);
        ArrayNode a = props.putArray("array");
        a.add(true);
        base.putNull("misc");
//ARGO_ORIGINAL
assertSame(base,
                MAPPER.readerForUpdating(base)
                .readValue(aposToQuotes(
                        "{'props':{'value':true, 'extra':25.5, 'array' : [ 3 ]}}")));
        System.out.println("base: " + base.toPrettyString());
//ARGO_ORIGINAL
assertEquals(2, base.size());
        ObjectNode resultProps = (ObjectNode) base.get("props");
//ARGO_ORIGINAL
assertEquals(4, resultProps.size());

//ARGO_ORIGINAL
assertEquals(123, resultProps.path("base").asInt());
//ARGO_ORIGINAL
assertTrue(resultProps.path("value").asBoolean());
//ARGO_ORIGINAL
assertEquals(25.5, resultProps.path("extra").asDouble());
        JsonNode n = resultProps.get("array");
//ARGO_ORIGINAL
assertEquals(ArrayNode.class, n.getClass());
//ARGO_ORIGINAL
assertEquals(2, n.size());
//ARGO_ORIGINAL
assertEquals(3, n.get(1).asInt());
    }

    public void testArrayNodeUpdateValue() throws Exception
    {
        ArrayNode base = MAPPER.createArrayNode();
        base.add("first");
//ARGO_ORIGINAL
assertSame(base,
                MAPPER.readerForUpdating(base)
                .readValue(aposToQuotes("['second',false,null]")));
//ARGO_ORIGINAL
assertEquals(4, base.size());
//ARGO_ORIGINAL
assertEquals("first", base.path(0).asText());
//ARGO_ORIGINAL
assertEquals("second", base.path(1).asText());
//ARGO_ORIGINAL
assertFalse(base.path(2).asBoolean());
//ARGO_ORIGINAL
assertTrue(base.path(3).isNull());
    }

    public void testArrayNodeMerge() throws Exception
    {
        ArrayNodeWrapper w = MAPPER.readValue(aposToQuotes("{'list':[456,true,{},  [], 'foo']}"),
                ArrayNodeWrapper.class);
//ARGO_ORIGINAL
assertEquals(6, w.list.size());
//ARGO_ORIGINAL
assertEquals(123, w.list.get(0).asInt());
//ARGO_ORIGINAL
assertEquals(456, w.list.get(1).asInt());
//ARGO_ORIGINAL
assertTrue(w.list.get(2).asBoolean());
        JsonNode n = w.list.get(3);
//ARGO_ORIGINAL
assertTrue(n.isObject());
//ARGO_ORIGINAL
assertEquals(0, n.size());
        n = w.list.get(4);
//ARGO_ORIGINAL
assertTrue(n.isArray());
//ARGO_ORIGINAL
assertEquals(0, n.size());
//ARGO_ORIGINAL
assertEquals("foo", w.list.get(5).asText());
    }
}
