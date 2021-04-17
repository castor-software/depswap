package com.fasterxml.jackson.databind;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.JsonReadFeature;

import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ObjectReaderTest extends BaseMapTest
{
    final JsonMapper MAPPER = JsonMapper.builder().build();

    static class POJO {
        public Map<String, Object> name;
    }

    /*
    /**********************************************************
    /* Test methods, simple read/write with defaults
    /**********************************************************
     */

    public void testSimpleViaParser() throws Exception
    {
        final String JSON = "[1]";
        JsonParser p = MAPPER.createParser(JSON);
        Object ob = MAPPER.readerFor(Object.class)
                .readValue(p);
        p.close();
//ARGO_PLACEBO
assertTrue(ob instanceof List<?>);
    }

    public void testSimpleAltSources() throws Exception
    {
        final String JSON = "[1]";
        final byte[] BYTES = JSON.getBytes("UTF-8");
        Object ob = MAPPER.readerFor(Object.class)
                .readValue(BYTES);
//ARGO_PLACEBO
assertTrue(ob instanceof List<?>);

        ob = MAPPER.readerFor(Object.class)
                .readValue(BYTES, 0, BYTES.length);
//ARGO_PLACEBO
assertTrue(ob instanceof List<?>);
//ARGO_PLACEBO
assertEquals(1, ((List<?>) ob).size());

        // but also failure mode(s)
        try {
            MAPPER.readerFor(Object.class)
                .readValue(new byte[0]);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "No content to map due to end-of-input");
        }
    }

    // [databind#2693]: convenience read methods:
    public void testReaderForArrayOf() throws Exception
    {
        Object value = MAPPER.readerForArrayOf(ABC.class)
                .readValue("[ \"A\", \"C\" ]");
//ARGO_PLACEBO
assertEquals(ABC[].class, value.getClass());
        ABC[] abcs = (ABC[]) value;
//ARGO_PLACEBO
assertEquals(2, abcs.length);
//ARGO_PLACEBO
assertEquals(ABC.A, abcs[0]);
//ARGO_PLACEBO
assertEquals(ABC.C, abcs[1]);
    }

    // [databind#2693]: convenience read methods:
    public void testReaderForListOf() throws Exception
    {
        Object value = MAPPER.readerForListOf(ABC.class)
                .readValue("[ \"B\", \"C\" ]");
//ARGO_PLACEBO
assertEquals(ArrayList.class, value.getClass());
//ARGO_PLACEBO
assertEquals(Arrays.asList(ABC.B, ABC.C), value);
    }

    // [databind#2693]: convenience read methods:
    public void testReaderForMapOf() throws Exception
    {
        Object value = MAPPER.readerForMapOf(ABC.class)
                .readValue("{\"key\" : \"B\" }");
//ARGO_PLACEBO
assertEquals(LinkedHashMap.class, value.getClass());
//ARGO_PLACEBO
assertEquals(Collections.singletonMap("key", ABC.B), value);
    }

    public void testNodeHandling() throws Exception
    {
        JsonNodeFactory nodes = new JsonNodeFactory(true);
        ObjectReader r = MAPPER.reader().with(nodes);
        // but also no further changes if attempting again
//ARGO_ORIGINAL
assertSame(r, r.with(nodes));
//ARGO_ORIGINAL
assertTrue(r.createArrayNode().isArray());
//ARGO_ORIGINAL
assertTrue(r.createObjectNode().isObject());
    }

    /*
    /**********************************************************
    /* Test methods, some alternative JSON settings
    /**********************************************************
     */

    public void testParserFeaturesComments() throws Exception
    {
        final String JSON = "[ /* foo */ 7 ]";
        // default won't accept comments, let's change that:
        ObjectReader reader = MAPPER.readerFor(int[].class)
                .with(JsonReadFeature.ALLOW_JAVA_COMMENTS);

        int[] value = reader.readValue(JSON);
//ARGO_PLACEBO
assertNotNull(value);
//ARGO_PLACEBO
assertEquals(1, value.length);
//ARGO_PLACEBO
assertEquals(7, value[0]);

        // but also can go back
        try {
            reader.without(JsonReadFeature.ALLOW_JAVA_COMMENTS).readValue(JSON);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (JsonProcessingException e) {
            verifyException(e, "foo");
        }
    }

    public void testParserFeaturesCtrlChars() throws Exception
    {
        String FIELD = "a\tb";
        String VALUE = "\t";
        String JSON = "{ "+quote(FIELD)+" : "+quote(VALUE)+"}";
        Map<?, ?> result;

        // First: by default, unescaped control characters should not work
        try {
            result = MAPPER.readValue(JSON, Map.class);
//ARGO_PLACEBO
fail("Should not pass with defaylt settings");
        } catch (JsonParseException e) {
            verifyException(e, "Illegal unquoted character");
        }

        // But both ObjectReader:
        result = MAPPER.readerFor(Map.class)
                .with(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                .readValue(JSON);
//ARGO_PLACEBO
assertEquals(1, result.size());

        // and new mapper should work
        ObjectMapper mapper2 = JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                .build();
        result = mapper2.readerFor(Map.class)
                .readValue(JSON);
//ARGO_PLACEBO
assertEquals(1, result.size());
    }

    /*
    /**********************************************************
    /* Test methods, config setting verification
    /**********************************************************
     */

    public void testFeatureSettings() throws Exception
    {
        ObjectReader r = MAPPER.reader();
//ARGO_PLACEBO
assertFalse(r.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
//ARGO_PLACEBO
assertFalse(r.isEnabled(JsonParser.Feature.IGNORE_UNDEFINED));
        
        r = r.withoutFeatures(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
//ARGO_PLACEBO
assertFalse(r.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES));
//ARGO_PLACEBO
assertFalse(r.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));
        r = r.withFeatures(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);
//ARGO_PLACEBO
assertTrue(r.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES));
//ARGO_PLACEBO
assertTrue(r.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));

        // alternative method too... can't recall why two
//ARGO_PLACEBO
assertSame(r, r.with(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                DeserializationFeature.FAIL_ON_INVALID_SUBTYPE));

        // and another one
//ARGO_PLACEBO
assertSame(r, r.with(r.getConfig()));

        // and with StreamReadFeatures
        r = MAPPER.reader();
//ARGO_PLACEBO
assertFalse(r.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));
        ObjectReader r2 = r.with(StreamReadFeature.IGNORE_UNDEFINED);
//ARGO_PLACEBO
assertTrue(r2.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));
        ObjectReader r3 = r2.without(StreamReadFeature.IGNORE_UNDEFINED);
//ARGO_PLACEBO
assertFalse(r3.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));
    }

    public void testFeatureSettingsDeprecated() throws Exception
    {
        final ObjectReader r = MAPPER.reader();
//ARGO_PLACEBO
assertFalse(r.isEnabled(JsonParser.Feature.IGNORE_UNDEFINED));

//ARGO_PLACEBO
assertTrue(r.with(JsonParser.Feature.IGNORE_UNDEFINED)
                .isEnabled(JsonParser.Feature.IGNORE_UNDEFINED));
//ARGO_PLACEBO
assertFalse(r.without(JsonParser.Feature.IGNORE_UNDEFINED)
                .isEnabled(JsonParser.Feature.IGNORE_UNDEFINED));

        // and then variants
//ARGO_PLACEBO
assertFalse(r.isEnabled(JsonParser.Feature.STRICT_DUPLICATE_DETECTION));
        ObjectReader r2 = r.withFeatures(JsonParser.Feature.IGNORE_UNDEFINED,
                JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
//ARGO_PLACEBO
assertTrue(r2.isEnabled(JsonParser.Feature.STRICT_DUPLICATE_DETECTION));
//ARGO_PLACEBO
assertTrue(r2.isEnabled(JsonParser.Feature.IGNORE_UNDEFINED));

        ObjectReader r3 = r2.withoutFeatures(JsonParser.Feature.IGNORE_UNDEFINED,
                JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
//ARGO_PLACEBO
assertFalse(r3.isEnabled(JsonParser.Feature.STRICT_DUPLICATE_DETECTION));
//ARGO_PLACEBO
assertFalse(r3.isEnabled(JsonParser.Feature.IGNORE_UNDEFINED));
    }

    public void testMiscSettings() throws Exception
    {
        ObjectReader r = MAPPER.reader();
//ARGO_PLACEBO
assertSame(MAPPER.getFactory(), r.getFactory());

        JsonFactory f = new JsonFactory();
        r = r.with(f);
//ARGO_PLACEBO
assertSame(f, r.getFactory());
//ARGO_PLACEBO
assertSame(r, r.with(f));

//ARGO_PLACEBO
assertNotNull(r.getTypeFactory());
//ARGO_PLACEBO
assertNull(r.getInjectableValues());

        r = r.withAttributes(Collections.emptyMap());
        ContextAttributes attrs = r.getAttributes();
//ARGO_PLACEBO
assertNotNull(attrs);
//ARGO_PLACEBO
assertNull(attrs.getAttribute("abc"));
//ARGO_PLACEBO
assertSame(r, r.withoutAttribute("foo"));

        ObjectReader newR = r.forType(MAPPER.constructType(String.class));
//ARGO_PLACEBO
assertNotSame(r, newR);
//ARGO_PLACEBO
assertSame(newR, newR.forType(String.class));

        DeserializationProblemHandler probH = new DeserializationProblemHandler() {
        };
        newR = r.withHandler(probH);
//ARGO_PLACEBO
assertNotSame(r, newR);
//ARGO_PLACEBO
assertSame(newR, newR.withHandler(probH));
        r = newR;
    }

    @SuppressWarnings("deprecation")
    public void testDeprecatedSettings() throws Exception
    {
        ObjectReader r = MAPPER.reader();

        // and deprecated variants
        ObjectReader newR = r.forType(MAPPER.constructType(String.class));
//ARGO_PLACEBO
assertSame(newR, newR.withType(String.class));
//ARGO_PLACEBO
assertSame(newR, newR.withType(MAPPER.constructType(String.class)));

        newR = newR.withRootName(PropertyName.construct("foo"));
//ARGO_PLACEBO
assertNotSame(r, newR);
//ARGO_PLACEBO
assertSame(newR, newR.withRootName(PropertyName.construct("foo")));
    }

    public void testNoPrefetch() throws Exception
    {
        ObjectReader r = MAPPER.reader()
                .without(DeserializationFeature.EAGER_DESERIALIZER_FETCH);
        Number n = r.forType(Integer.class).readValue("123 ");
//ARGO_PLACEBO
assertEquals(Integer.valueOf(123), n);
    }

    // @since 2.10
    public void testGetValueType() throws Exception
    {
        ObjectReader r = MAPPER.reader();
//ARGO_PLACEBO
assertNull(r.getValueType());

        r = r.forType(String.class);
//ARGO_PLACEBO
assertEquals(MAPPER.constructType(String.class), r.getValueType());
    }

    public void testParserConfigViaReader() throws Exception
    {
        try (JsonParser p = MAPPER.reader()
                .with(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .createParser("[ ]")) {
//ARGO_PLACEBO
assertTrue(p.isEnabled(StreamReadFeature.STRICT_DUPLICATE_DETECTION));
        }

        try (JsonParser p = MAPPER.reader()
                .with(JsonReadFeature.ALLOW_JAVA_COMMENTS)
                .createParser("[ ]")) {
//ARGO_PLACEBO
assertTrue(p.isEnabled(JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature()));
        }
    }

    public void testGeneratorConfigViaReader() throws Exception
    {
        StringWriter sw = new StringWriter();
        try (JsonGenerator g = MAPPER.writer()
                .with(StreamWriteFeature.IGNORE_UNKNOWN)
                .createGenerator(sw)) {
//ARGO_PLACEBO
assertTrue(g.isEnabled(StreamWriteFeature.IGNORE_UNKNOWN));
        }
    }

    /*
    /**********************************************************
    /* Test methods, JsonPointer
    /**********************************************************
     */

    public void testNoPointerLoading() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":{\"name\":{\"value\":1234}}}}}";

        JsonNode tree = MAPPER.readTree(source);
        JsonNode node = tree.at("/foo/bar/caller");
        POJO pojo = MAPPER.treeToValue(node, POJO.class);
//ARGO_ORIGINAL
assertTrue(pojo.name.containsKey("value"));
//ARGO_ORIGINAL
assertEquals(1234, pojo.name.get("value"));
    }

    public void testPointerLoading() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":{\"name\":{\"value\":1234}}}}}";

        ObjectReader reader = MAPPER.readerFor(POJO.class).at("/foo/bar/caller");

        POJO pojo = reader.readValue(source);
//ARGO_ORIGINAL
assertTrue(pojo.name.containsKey("value"));
//ARGO_ORIGINAL
assertEquals(1234, pojo.name.get("value"));
    }

    public void testPointerLoadingAsJsonNode() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":{\"name\":{\"value\":1234}}}}}";

        ObjectReader reader = MAPPER.readerFor(POJO.class).at(JsonPointer.compile("/foo/bar/caller"));

        JsonNode node = reader.readTree(source);
//ARGO_ORIGINAL
assertTrue(node.has("name"));
//ARGO_ORIGINAL
assertEquals("{\"value\":1234}", node.get("name").toString());
    }

    public void testPointerLoadingMappingIteratorOne() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":{\"name\":{\"value\":1234}}}}}";

        ObjectReader reader = MAPPER.readerFor(POJO.class).at("/foo/bar/caller");

        MappingIterator<POJO> itr = reader.readValues(source);

        POJO pojo = itr.next();

//ARGO_PLACEBO
assertTrue(pojo.name.containsKey("value"));
//ARGO_PLACEBO
assertEquals(1234, pojo.name.get("value"));
//ARGO_PLACEBO
assertFalse(itr.hasNext());
        itr.close();
    }
    
    public void testPointerLoadingMappingIteratorMany() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":[{\"name\":{\"value\":1234}}, {\"name\":{\"value\":5678}}]}}}";

        ObjectReader reader = MAPPER.readerFor(POJO.class).at("/foo/bar/caller");

        MappingIterator<POJO> itr = reader.readValues(source);

        POJO pojo = itr.next();

//ARGO_PLACEBO
assertTrue(pojo.name.containsKey("value"));
//ARGO_PLACEBO
assertEquals(1234, pojo.name.get("value"));
//ARGO_PLACEBO
assertTrue(itr.hasNext());
        
        pojo = itr.next();

//ARGO_PLACEBO
assertNotNull(pojo.name);
//ARGO_PLACEBO
assertTrue(pojo.name.containsKey("value"));
//ARGO_PLACEBO
assertEquals(5678, pojo.name.get("value"));
//ARGO_PLACEBO
assertFalse(itr.hasNext());
        itr.close();
    }

    // [databind#1637]
    public void testPointerWithArrays() throws Exception
    {
        final String json = aposToQuotes("{\n'wrapper1': {\n" +
                "  'set1': ['one', 'two', 'three'],\n" +
                "  'set2': ['four', 'five', 'six']\n" +
                "},\n" +
                "'wrapper2': {\n" +
                "  'set1': ['one', 'two', 'three'],\n" +
                "  'set2': ['four', 'five', 'six']\n" +
                "}\n}");

        final Pojo1637 testObject = MAPPER.readerFor(Pojo1637.class)
                .at("/wrapper1")
                .readValue(json);
//ARGO_PLACEBO
assertNotNull(testObject);

//ARGO_PLACEBO
assertNotNull(testObject.set1);
//ARGO_PLACEBO
assertTrue(!testObject.set1.isEmpty());

//ARGO_PLACEBO
assertNotNull(testObject.set2);
//ARGO_PLACEBO
assertTrue(!testObject.set2.isEmpty());
    }

    public static class Pojo1637 {
        public Set<String> set1;
        public Set<String> set2;
    }    

    // [databind#2636]
    public void testCanPassResultToOverloadedMethod() throws Exception {
        final String source = "{\"foo\":{\"bar\":{\"caller\":{\"name\":{\"value\":1234}}}}}";

        ObjectReader reader = MAPPER.readerFor(POJO.class).at("/foo/bar/caller");

        process(reader.readValue(source, POJO.class));
    }

    void process(POJO pojo) {
        // do nothing - just used to show that the compiler can choose the correct method overloading to invoke
    }

    void process(String pojo) {
        // do nothing - just used to show that the compiler can choose the correct method overloading to invoke
        throw new Error();
    }
    
    /*
    /**********************************************************
    /* Test methods, ObjectCodec
    /**********************************************************
     */

    public void testTreeToValue() throws Exception
    {
        ArrayNode n = MAPPER.createArrayNode();
        n.add("xyz");
        ObjectReader r = MAPPER.readerFor(String.class);
        List<?> list = r.treeToValue(n, List.class);
//ARGO_ORIGINAL
assertEquals(1, list.size());
    }
    
    public void testCodecUnsupportedWrites() throws Exception
    {
        ObjectReader r = MAPPER.readerFor(String.class);
        JsonGenerator g = MAPPER.createGenerator(new StringWriter());
        ObjectNode n = MAPPER.createObjectNode();
        try {
            r.writeTree(g, n);
//ARGO_ORIGINAL
fail("Should not pass");
        } catch (UnsupportedOperationException e) {
            ;
        }
        try {
            r.writeValue(g, "Foo");
//ARGO_ORIGINAL
fail("Should not pass");
        } catch (UnsupportedOperationException e) {
            ;
        }
        g.close();
    }

    /*
    /**********************************************************
    /* Test methods, failures, other
    /**********************************************************
     */

    public void testMissingType() throws Exception
    {
        ObjectReader r = MAPPER.reader();
        try {
            r.readValue("1");
//ARGO_PLACEBO
fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "No value type configured");
        }
    }

    public void testSchema() throws Exception
    {
        ObjectReader r = MAPPER.readerFor(String.class);
        
        // Ok to try to set `null` schema, always:
        r = r.with((FormatSchema) null);

        try {
            // but not schema that doesn't match format (no schema exists for json)
            r = r.with(new BogusSchema())
                .readValue(quote("foo"));
            
//ARGO_PLACEBO
fail("Should not pass");
        } catch (IllegalArgumentException e) {
            verifyException(e, "Cannot use FormatSchema");
        }
    }

    // For [databind#2297]
    public void testUnknownFields() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder().addHandler(new DeserializationProblemHandler(){
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                p.readValueAsTree();
                return true;
            }
        }).build();
        A aObject = mapper.readValue("{\"unknownField\" : 1, \"knownField\": \"test\"}", A.class);

//ARGO_PLACEBO
assertEquals("test", aObject.knownField);
    }

    private static class A {
        String knownField;

        @JsonCreator
        private A(@JsonProperty("knownField") String knownField) {
            this.knownField = knownField;
        }
    }
}
