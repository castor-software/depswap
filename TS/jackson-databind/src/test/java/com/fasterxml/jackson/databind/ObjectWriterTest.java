package com.fasterxml.jackson.databind;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Unit tests for checking features added to {@link ObjectWriter}, such
 * as adding of explicit pretty printer.
 */
public class ObjectWriterTest
    extends BaseMapTest
{
    static class CloseableValue implements Closeable
    {
        public int x;

        public boolean closed;
        
        @Override
        public void close() throws IOException {
            closed = true;
        }
    }

    final ObjectMapper MAPPER = new ObjectMapper();

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    static class PolyBase {
    }

    @JsonTypeName("A")
    static class ImplA extends PolyBase {
        public int value;
        
        public ImplA(int v) { value = v; }
    }

    @JsonTypeName("B")
    static class ImplB extends PolyBase {
        public int b;
        
        public ImplB(int v) { b = v; }
    }

    /*
    /**********************************************************
    /* Test methods, normal operation
    /**********************************************************
     */

    public void testPrettyPrinter() throws Exception
    {
        ObjectWriter writer = MAPPER.writer();
        HashMap<String, Integer> data = new HashMap<String,Integer>();
        data.put("a", 1);
        
        // default: no indentation
//ARGO_PLACEBO
assertEquals("{\"a\":1}", writer.writeValueAsString(data));

        // and then with standard
        writer = writer.withDefaultPrettyPrinter();

        // pretty printer uses system-specific line feeds, so we do that as well.
        String lf = System.getProperty("line.separator");
//ARGO_PLACEBO
assertEquals("{" + lf + "  \"a\" : 1" + lf + "}", writer.writeValueAsString(data));

        // and finally, again without indentation
        writer = writer.with((PrettyPrinter) null);
//ARGO_PLACEBO
assertEquals("{\"a\":1}", writer.writeValueAsString(data));
    }

    public void testPrefetch() throws Exception
    {
        ObjectWriter writer = MAPPER.writer();
//ARGO_PLACEBO
assertFalse(writer.hasPrefetchedSerializer());
        writer = writer.forType(String.class);
//ARGO_PLACEBO
assertTrue(writer.hasPrefetchedSerializer());
    }

    public void testObjectWriterFeatures() throws Exception
    {
        ObjectWriter writer = MAPPER.writer()
                .without(JsonWriteFeature.QUOTE_FIELD_NAMES);                
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("a", 1);
//ARGO_PLACEBO
assertEquals("{a:1}", writer.writeValueAsString(map));
        // but can also reconfigure
//ARGO_PLACEBO
assertEquals("{\"a\":1}", writer.with(JsonWriteFeature.QUOTE_FIELD_NAMES)
                .writeValueAsString(map));
    }

    public void testObjectWriterWithNode() throws Exception
    {
        ObjectNode stuff = MAPPER.createObjectNode();
        stuff.put("a", 5);
        ObjectWriter writer = MAPPER.writerFor(JsonNode.class);
        String json = writer.writeValueAsString(stuff);
//ARGO_ORIGINAL
assertEquals("{\"a\":5}", json);
    }

    public void testPolymorphicWithTyping() throws Exception
    {
        ObjectWriter writer = MAPPER.writerFor(PolyBase.class);
        String json;

        json = writer.writeValueAsString(new ImplA(3));
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'type':'A','value':3}"), json);
        json = writer.writeValueAsString(new ImplB(-5));
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'type':'B','b':-5}"), json);
    }

    public void testCanSerialize() throws Exception
    {
//ARGO_PLACEBO
assertTrue(MAPPER.writer().canSerialize(String.class));
//ARGO_PLACEBO
assertTrue(MAPPER.writer().canSerialize(String.class, null));
    }

    public void testNoPrefetch() throws Exception
    {
        ObjectWriter w = MAPPER.writer()
                .without(SerializationFeature.EAGER_SERIALIZER_FETCH);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        w.writeValue(out, Integer.valueOf(3));
        out.close();
//ARGO_PLACEBO
assertEquals("3", out.toString("UTF-8"));
    }

    public void testWithCloseCloseable() throws Exception
    {
        ObjectWriter w = MAPPER.writer()
                .with(SerializationFeature.CLOSE_CLOSEABLE);
//ARGO_PLACEBO
assertTrue(w.isEnabled(SerializationFeature.CLOSE_CLOSEABLE));
        CloseableValue input = new CloseableValue();
//ARGO_PLACEBO
assertFalse(input.closed);
        byte[] json = w.writeValueAsBytes(input);
//ARGO_PLACEBO
assertNotNull(json);
//ARGO_PLACEBO
assertTrue(input.closed);
        input.close();

        // and via explicitly passed generator
        JsonGenerator g = MAPPER.createGenerator(new StringWriter());
        input = new CloseableValue();
//ARGO_PLACEBO
assertFalse(input.closed);
        w.writeValue(g, input);
//ARGO_PLACEBO
assertTrue(input.closed);
        g.close();
        input.close();
    }

    public void testViewSettings() throws Exception
    {
        ObjectWriter w = MAPPER.writer();
        ObjectWriter newW = w.withView(String.class);
//ARGO_PLACEBO
assertNotSame(w, newW);
//ARGO_PLACEBO
assertSame(newW, newW.withView(String.class));

        newW = w.with(Locale.CANADA);
//ARGO_PLACEBO
assertNotSame(w, newW);
//ARGO_PLACEBO
assertSame(newW, newW.with(Locale.CANADA));
    }

    public void testMiscSettings() throws Exception
    {
        ObjectWriter w = MAPPER.writer();
//ARGO_PLACEBO
assertSame(MAPPER.getFactory(), w.getFactory());
//ARGO_PLACEBO
assertFalse(w.hasPrefetchedSerializer());
//ARGO_PLACEBO
assertNotNull(w.getTypeFactory());

        JsonFactory f = new JsonFactory();
        w = w.with(f);
//ARGO_PLACEBO
assertSame(f, w.getFactory());
        ObjectWriter newW = w.with(Base64Variants.MODIFIED_FOR_URL);
//ARGO_PLACEBO
assertNotSame(w, newW);
//ARGO_PLACEBO
assertSame(newW, newW.with(Base64Variants.MODIFIED_FOR_URL));
        
        w = w.withAttributes(Collections.emptyMap());
        w = w.withAttribute("a", "b");
//ARGO_PLACEBO
assertEquals("b", w.getAttributes().getAttribute("a"));
        w = w.withoutAttribute("a");
//ARGO_PLACEBO
assertNull(w.getAttributes().getAttribute("a"));

        FormatSchema schema = new BogusSchema();
        try {
            newW = w.with(schema);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (IllegalArgumentException e) {
            verifyException(e, "Cannot use FormatSchema");
        }
    }

    public void testRootValueSettings() throws Exception
    {
        ObjectWriter w = MAPPER.writer();
        
        // First, root name:
        ObjectWriter newW = w.withRootName("foo");
//ARGO_PLACEBO
assertNotSame(w, newW);
//ARGO_PLACEBO
assertSame(newW, newW.withRootName(PropertyName.construct("foo")));
        w = newW;
        newW = w.withRootName((String) null);
//ARGO_PLACEBO
assertNotSame(w, newW);
//ARGO_PLACEBO
assertSame(newW, newW.withRootName((PropertyName) null));

        // Then root value separator

        w = w.withRootValueSeparator(new SerializedString(","));
//ARGO_PLACEBO
assertSame(w, w.withRootValueSeparator(new SerializedString(",")));
//ARGO_PLACEBO
assertSame(w, w.withRootValueSeparator(","));

         newW = w.withRootValueSeparator("/");
//ARGO_PLACEBO
assertNotSame(w, newW);
//ARGO_PLACEBO
assertSame(newW, newW.withRootValueSeparator("/"));

        newW = w.withRootValueSeparator((String) null);
//ARGO_PLACEBO
assertNotSame(w, newW);

        newW = w.withRootValueSeparator((SerializableString) null);
//ARGO_PLACEBO
assertNotSame(w, newW);
    }

    public void testFeatureSettings() throws Exception
    {
        ObjectWriter w = MAPPER.writer();
//ARGO_PLACEBO
assertFalse(w.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
//ARGO_PLACEBO
assertFalse(w.isEnabled(JsonGenerator.Feature.STRICT_DUPLICATE_DETECTION));
//ARGO_PLACEBO
assertFalse(w.isEnabled(StreamWriteFeature.STRICT_DUPLICATE_DETECTION));

        ObjectWriter newW = w.with(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS,
                SerializationFeature.INDENT_OUTPUT);
//ARGO_PLACEBO
assertNotSame(w, newW);
//ARGO_PLACEBO
assertTrue(newW.isEnabled(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS));
//ARGO_PLACEBO
assertTrue(newW.isEnabled(SerializationFeature.INDENT_OUTPUT));
//ARGO_PLACEBO
assertSame(newW, newW.with(SerializationFeature.INDENT_OUTPUT));
//ARGO_PLACEBO
assertSame(newW, newW.withFeatures(SerializationFeature.INDENT_OUTPUT));

        newW = w.withFeatures(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS,
                SerializationFeature.INDENT_OUTPUT);
//ARGO_PLACEBO
assertNotSame(w, newW);

        newW = w.without(SerializationFeature.FAIL_ON_EMPTY_BEANS,
                SerializationFeature.EAGER_SERIALIZER_FETCH);
//ARGO_PLACEBO
assertNotSame(w, newW);
//ARGO_PLACEBO
assertFalse(newW.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
//ARGO_PLACEBO
assertFalse(newW.isEnabled(SerializationFeature.EAGER_SERIALIZER_FETCH));
//ARGO_PLACEBO
assertSame(newW, newW.without(SerializationFeature.FAIL_ON_EMPTY_BEANS));
//ARGO_PLACEBO
assertSame(newW, newW.withoutFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS));

//ARGO_PLACEBO
assertNotSame(w, w.withoutFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS,
                SerializationFeature.EAGER_SERIALIZER_FETCH));
    }

    public void testGeneratorFeatures() throws Exception
    {
        ObjectWriter w = MAPPER.writer();
//ARGO_PLACEBO
assertNotSame(w, w.with(JsonWriteFeature.ESCAPE_NON_ASCII));
//ARGO_PLACEBO
assertNotSame(w, w.withFeatures(JsonWriteFeature.ESCAPE_NON_ASCII));

//ARGO_PLACEBO
assertTrue(w.isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET));
//ARGO_PLACEBO
assertNotSame(w, w.without(JsonGenerator.Feature.AUTO_CLOSE_TARGET));
//ARGO_PLACEBO
assertNotSame(w, w.withoutFeatures(JsonGenerator.Feature.AUTO_CLOSE_TARGET));

//ARGO_PLACEBO
assertFalse(w.isEnabled(StreamWriteFeature.STRICT_DUPLICATE_DETECTION));
//ARGO_PLACEBO
assertNotSame(w, w.with(StreamWriteFeature.STRICT_DUPLICATE_DETECTION));
    }

    /*
    /**********************************************************
    /* Test methods, failures
    /**********************************************************
     */

    public void testArgumentChecking() throws Exception
    {
        final ObjectWriter w = MAPPER.writer();
        try {
            w.acceptJsonFormatVisitor((JavaType) null, null);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (IllegalArgumentException e) {
            verifyException(e, "argument \"type\" is null");
        }
    }

    public void testSchema() throws Exception
    {
        try {
            MAPPER.writerFor(String.class)
                .with(new BogusSchema())
                .writeValueAsBytes("foo");
//ARGO_PLACEBO
fail("Should not pass");
        } catch (IllegalArgumentException e) {
            verifyException(e, "Cannot use FormatSchema");
        }
    }
}
