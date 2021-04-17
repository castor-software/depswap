package com.fasterxml.jackson.databind.util;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.util.JsonParserSequence;

import com.fasterxml.jackson.databind.*;

public class TestTokenBuffer extends BaseMapTest
{
    private final ObjectMapper MAPPER = objectMapper();

    static class Base1730 { }

    static class Sub1730 extends Base1730 { }

    /*
    /**********************************************************
    /* Basic TokenBuffer tests
    /**********************************************************
     */

    public void testBasicConfig() throws IOException
    {
        TokenBuffer buf;

        buf = new TokenBuffer(MAPPER, false);
//ARGO_PLACEBO
assertEquals(MAPPER.version(), buf.version());
//ARGO_PLACEBO
assertSame(MAPPER, buf.getCodec());
//ARGO_PLACEBO
assertNotNull(buf.getOutputContext());
//ARGO_PLACEBO
assertFalse(buf.isClosed());

        buf.setCodec(null);
//ARGO_PLACEBO
assertNull(buf.getCodec());

//ARGO_PLACEBO
assertFalse(buf.isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN));
        buf.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
//ARGO_PLACEBO
assertTrue(buf.isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN));
        buf.disable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
//ARGO_PLACEBO
assertFalse(buf.isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN));

        buf.close();
//ARGO_PLACEBO
assertTrue(buf.isClosed());
    }

    /**
     * Test writing of individual simple values
     */
    public void testSimpleWrites() throws IOException
    {
        TokenBuffer buf = new TokenBuffer(null, false); // no ObjectCodec
        
        // First, with empty buffer
        JsonParser p = buf.asParser();
//ARGO_PLACEBO
assertNull(p.currentToken());
//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();

        // Then with simple text
        buf.writeString("abc");

        p = buf.asParser();
//ARGO_PLACEBO
assertNull(p.currentToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_STRING, p.nextToken());
//ARGO_PLACEBO
assertEquals("abc", p.getText());
//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();

        // Then, let's append at root level
        buf.writeNumber(13);
        p = buf.asParser();
//ARGO_PLACEBO
assertNull(p.currentToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_STRING, p.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_PLACEBO
assertEquals(13, p.getIntValue());
//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();
        buf.close();
    }

    // For 2.9, explicit "isNaN" check
    public void testSimpleNumberWrites() throws IOException
    {
        TokenBuffer buf = new TokenBuffer(null, false);

        double[] values1 = new double[] {
                0.25, Double.NaN, -2.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY
        };
        float[] values2 = new float[] {
                Float.NEGATIVE_INFINITY,
                0.25f,
                Float.POSITIVE_INFINITY
        };

        for (double v : values1) {
            buf.writeNumber(v);
        }
        for (float v : values2) {
            buf.writeNumber(v);
        }

        JsonParser p = buf.asParser();
//ARGO_PLACEBO
assertNull(p.currentToken());

        for (double v : values1) {
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            double actual = p.getDoubleValue();
            boolean expNan = Double.isNaN(v) || Double.isInfinite(v);
//ARGO_PLACEBO
assertEquals(expNan, p.isNaN());
//ARGO_PLACEBO
assertEquals(0, Double.compare(v, actual));
        }
        for (float v : values2) {
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            float actual = p.getFloatValue();
            boolean expNan = Float.isNaN(v) || Float.isInfinite(v);
//ARGO_PLACEBO
assertEquals(expNan, p.isNaN());
//ARGO_PLACEBO
assertEquals(0, Float.compare(v, actual));
        }
        p.close();
        buf.close();
    }

    // [databind#1729]
    public void testNumberOverflowInt() throws IOException
    {
        try (TokenBuffer buf = new TokenBuffer(null, false)) {
            long big = 1L + Integer.MAX_VALUE;
            buf.writeNumber(big);
            try (JsonParser p = buf.asParser()) {
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_PLACEBO
assertEquals(NumberType.LONG, p.getNumberType());
                try {
                    p.getIntValue();
//ARGO_PLACEBO
fail("Expected failure for `int` overflow");
                } catch (InputCoercionException e) {
                    verifyException(e, "Numeric value ("+big+") out of range of int");
                }
            }
        }
        // and ditto for coercion.
        try (TokenBuffer buf = new TokenBuffer(null, false)) {
            long big = 1L + Integer.MAX_VALUE;
            buf.writeNumber(String.valueOf(big));
            try (JsonParser p = buf.asParser()) {
                // NOTE: oddity of buffering, no inspection of "real" type if given String...
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
                try {
                    p.getIntValue();
//ARGO_PLACEBO
fail("Expected failure for `int` overflow");
                } catch (InputCoercionException e) {
                    verifyException(e, "Numeric value ("+big+") out of range of int");
                }
            }
        }
    }

    public void testNumberOverflowLong() throws IOException
    {
        try (TokenBuffer buf = new TokenBuffer(null, false)) {
            BigInteger big = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
            buf.writeNumber(big);
            try (JsonParser p = buf.asParser()) {
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_PLACEBO
assertEquals(NumberType.BIG_INTEGER, p.getNumberType());
                try {
                    p.getLongValue();
//ARGO_PLACEBO
fail("Expected failure for `long` overflow");
                } catch (InputCoercionException e) {
                    verifyException(e, "Numeric value ("+big+") out of range of long");
                }
            }
        }
    }

    public void testParentContext() throws IOException
    {
        TokenBuffer buf = new TokenBuffer(null, false); // no ObjectCodec
        buf.writeStartObject();
        buf.writeFieldName("b");
        buf.writeStartObject();
        buf.writeFieldName("c");
        //This assertion succeeds as expected
//ARGO_PLACEBO
assertEquals("b", buf.getOutputContext().getParent().getCurrentName());
        buf.writeString("cval");
        buf.writeEndObject();
        buf.writeEndObject();
        buf.close();
    }

    public void testSimpleArray() throws IOException
    {
        TokenBuffer buf = new TokenBuffer(null, false); // no ObjectCodec

        // First, empty array
//ARGO_PLACEBO
assertTrue(buf.getOutputContext().inRoot());
        buf.writeStartArray();
//ARGO_PLACEBO
assertTrue(buf.getOutputContext().inArray());
        buf.writeEndArray();
//ARGO_PLACEBO
assertTrue(buf.getOutputContext().inRoot());

        JsonParser p = buf.asParser();
//ARGO_PLACEBO
assertNull(p.currentToken());
//ARGO_PLACEBO
assertTrue(p.getParsingContext().inRoot());
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_PLACEBO
assertTrue(p.getParsingContext().inArray());
//ARGO_PLACEBO
assertToken(JsonToken.END_ARRAY, p.nextToken());
//ARGO_PLACEBO
assertTrue(p.getParsingContext().inRoot());
//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();
        buf.close();

        // Then one with simple contents
        buf = new TokenBuffer(null, false);
        buf.writeStartArray();
        buf.writeBoolean(true);
        buf.writeNull();
        buf.writeEndArray();
        p = buf.asParser();
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_TRUE, p.nextToken());
//ARGO_PLACEBO
assertTrue(p.getBooleanValue());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NULL, p.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.END_ARRAY, p.nextToken());
//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();
        buf.close();

        // And finally, with array-in-array
        buf = new TokenBuffer(null, false);
        buf.writeStartArray();
        buf.writeStartArray();
        buf.writeBinary(new byte[3]);
        buf.writeEndArray();
        buf.writeEndArray();
        p = buf.asParser();
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, p.nextToken());
        // TokenBuffer exposes it as embedded object...
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
        Object ob = p.getEmbeddedObject();
//ARGO_PLACEBO
assertNotNull(ob);
//ARGO_PLACEBO
assertTrue(ob instanceof byte[]);
//ARGO_PLACEBO
assertEquals(3, ((byte[]) ob).length);
//ARGO_PLACEBO
assertToken(JsonToken.END_ARRAY, p.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.END_ARRAY, p.nextToken());
//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();
        buf.close();
    }
    
    public void testSimpleObject() throws IOException
    {
        TokenBuffer buf = new TokenBuffer(null, false);

        // First, empty JSON Object
//ARGO_PLACEBO
assertTrue(buf.getOutputContext().inRoot());
        buf.writeStartObject();
//ARGO_PLACEBO
assertTrue(buf.getOutputContext().inObject());
        buf.writeEndObject();
//ARGO_PLACEBO
assertTrue(buf.getOutputContext().inRoot());

        JsonParser p = buf.asParser();
//ARGO_PLACEBO
assertNull(p.currentToken());
//ARGO_PLACEBO
assertTrue(p.getParsingContext().inRoot());
//ARGO_PLACEBO
assertToken(JsonToken.START_OBJECT, p.nextToken());
//ARGO_PLACEBO
assertTrue(p.getParsingContext().inObject());
//ARGO_PLACEBO
assertToken(JsonToken.END_OBJECT, p.nextToken());
//ARGO_PLACEBO
assertTrue(p.getParsingContext().inRoot());
//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();
        buf.close();

        // Then one with simple contents
        buf = new TokenBuffer(null, false);
        buf.writeStartObject();
        buf.writeNumberField("num", 1.25);
        buf.writeEndObject();

        p = buf.asParser();
//ARGO_PLACEBO
assertNull(p.currentToken());
//ARGO_PLACEBO
assertToken(JsonToken.START_OBJECT, p.nextToken());
//ARGO_PLACEBO
assertNull(p.currentName());
//ARGO_PLACEBO
assertToken(JsonToken.FIELD_NAME, p.nextToken());
//ARGO_PLACEBO
assertEquals("num", p.currentName());
        // and override should also work:
        p.overrideCurrentName("bah");
//ARGO_PLACEBO
assertEquals("bah", p.currentName());
        
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
//ARGO_PLACEBO
assertEquals(1.25, p.getDoubleValue());
        // should still have access to (overridden) name
//ARGO_PLACEBO
assertEquals("bah", p.currentName());
//ARGO_PLACEBO
assertToken(JsonToken.END_OBJECT, p.nextToken());
        // but not any more
//ARGO_PLACEBO
assertNull(p.currentName());
//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();
        buf.close();
    }

    /**
     * Verify handling of that "standard" test document (from JSON
     * specification)
     */
    public void testWithJSONSampleDoc() throws Exception
    {
        // First, copy events from known good source (StringReader)
        JsonParser p = createParserUsingReader(SAMPLE_DOC_JSON_SPEC);
        TokenBuffer tb = new TokenBuffer(null, false);
        while (p.nextToken() != null) {
            tb.copyCurrentEvent(p);
        }

        // And then request verification; first structure only:
        verifyJsonSpecSampleDoc(tb.asParser(), false);

        // then content check too:
        verifyJsonSpecSampleDoc(tb.asParser(), true);
        tb.close();
        p.close();

    
        // 19-Oct-2016, tatu: Just for fun, trigger `toString()` for code coverage
        String desc = tb.toString();
//ARGO_PLACEBO
assertNotNull(desc);
    }

    public void testAppend() throws IOException
    {
        TokenBuffer buf1 = new TokenBuffer(null, false);
        buf1.writeStartObject();
        buf1.writeFieldName("a");
        buf1.writeBoolean(true);
        
        TokenBuffer buf2 = new TokenBuffer(null, false);
        buf2.writeFieldName("b");
        buf2.writeNumber(13);
        buf2.writeEndObject();
        
        buf1.append(buf2);
        
        // and verify that we got it all...
        JsonParser p = buf1.asParser();
//ARGO_PLACEBO
assertToken(JsonToken.START_OBJECT, p.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.FIELD_NAME, p.nextToken());
//ARGO_PLACEBO
assertEquals("a", p.currentName());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_TRUE, p.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.FIELD_NAME, p.nextToken());
//ARGO_PLACEBO
assertEquals("b", p.currentName());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_PLACEBO
assertEquals(13, p.getIntValue());
//ARGO_PLACEBO
assertToken(JsonToken.END_OBJECT, p.nextToken());
        p.close();
        buf1.close();
        buf2.close();
    }

    // Since 2.3 had big changes to UUID handling, let's verify we can
    // deal with
    public void testWithUUID() throws IOException
    {
        for (String value : new String[] {
                "00000007-0000-0000-0000-000000000000",
                "76e6d183-5f68-4afa-b94a-922c1fdb83f8",
                "540a88d1-e2d8-4fb1-9396-9212280d0a7f",
                "2c9e441d-1cd0-472d-9bab-69838f877574",
                "591b2869-146e-41d7-8048-e8131f1fdec5",
                "82994ac2-7b23-49f2-8cc5-e24cf6ed77be",
        }) {
            TokenBuffer buf = new TokenBuffer(MAPPER, false); // no ObjectCodec
            UUID uuid = UUID.fromString(value);
            MAPPER.writeValue(buf, uuid);
            buf.close();
    
            // and bring it back
            UUID out = MAPPER.readValue(buf.asParser(), UUID.class);
//ARGO_PLACEBO
assertEquals(uuid.toString(), out.toString());

            // second part: As per [databind#362], should NOT use binary with TokenBuffer
            JsonParser p = buf.asParser();
//ARGO_PLACEBO
assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            String str = p.getText();
//ARGO_PLACEBO
assertEquals(value, str);
            p.close();
        }
    }

    /*
    /**********************************************************
    /* Tests for read/output contexts
    /**********************************************************
     */

    // for [databind#984]: ensure output context handling identical
    public void testOutputContext() throws IOException
    {
        TokenBuffer buf = new TokenBuffer(null, false); // no ObjectCodec
        StringWriter w = new StringWriter();
        JsonGenerator gen = MAPPER.createGenerator(w);
 
        // test content: [{"a":1,"b":{"c":2}},{"a":2,"b":{"c":3}}]

        buf.writeStartArray();
        gen.writeStartArray();
        _verifyOutputContext(buf, gen);
        
        buf.writeStartObject();
        gen.writeStartObject();
        _verifyOutputContext(buf, gen);
        
        buf.writeFieldName("a");
        gen.writeFieldName("a");
        _verifyOutputContext(buf, gen);

        buf.writeNumber(1);
        gen.writeNumber(1);
        _verifyOutputContext(buf, gen);

        buf.writeFieldName("b");
        gen.writeFieldName("b");
        _verifyOutputContext(buf, gen);

        buf.writeStartObject();
        gen.writeStartObject();
        _verifyOutputContext(buf, gen);
        
        buf.writeFieldName("c");
        gen.writeFieldName("c");
        _verifyOutputContext(buf, gen);

        buf.writeNumber(2);
        gen.writeNumber(2);
        _verifyOutputContext(buf, gen);

        buf.writeEndObject();
        gen.writeEndObject();
        _verifyOutputContext(buf, gen);

        buf.writeEndObject();
        gen.writeEndObject();
        _verifyOutputContext(buf, gen);

        buf.writeEndArray();
        gen.writeEndArray();
        _verifyOutputContext(buf, gen);
        
        buf.close();
        gen.close();
    }

    private void _verifyOutputContext(JsonGenerator gen1, JsonGenerator gen2)
    {
        _verifyOutputContext(gen1.getOutputContext(), gen2.getOutputContext());
    }

    private void _verifyOutputContext(JsonStreamContext ctxt1, JsonStreamContext ctxt2)
    {
        if (ctxt1 == null) {
            if (ctxt2 == null) {
                return;
            }
//ARGO_PLACEBO
fail("Context 1 null, context 2 not null: "+ctxt2);
        } else if (ctxt2 == null) {
//ARGO_PLACEBO
fail("Context 2 null, context 1 not null: "+ctxt1);
        }
        if (!ctxt1.toString().equals(ctxt2.toString())) {
//ARGO_PLACEBO
fail("Different output context: token-buffer's = "+ctxt1+", json-generator's: "+ctxt2);
        }

        if (ctxt1.inObject()) {
//ARGO_PLACEBO
assertTrue(ctxt2.inObject());
            String str1 = ctxt1.getCurrentName();
            String str2 = ctxt2.getCurrentName();

            if ((str1 != str2) && !str1.equals(str2)) {
//ARGO_PLACEBO
fail("Expected name '"+str2+"' (JsonParser), TokenBuffer had '"+str1+"'");
            }
        } else if (ctxt1.inArray()) {
//ARGO_PLACEBO
assertTrue(ctxt2.inArray());
//ARGO_PLACEBO
assertEquals(ctxt1.getCurrentIndex(), ctxt2.getCurrentIndex());
        }
        _verifyOutputContext(ctxt1.getParent(), ctxt2.getParent());
    }

    // [databind#1253]
    public void testParentSiblingContext() throws IOException
    {
        TokenBuffer buf = new TokenBuffer(null, false); // no ObjectCodec

        // {"a":{},"b":{"c":"cval"}}
        
        buf.writeStartObject();
        buf.writeFieldName("a");
        buf.writeStartObject();
        buf.writeEndObject();

        buf.writeFieldName("b");
        buf.writeStartObject();
        buf.writeFieldName("c");
        //This assertion fails (because of 'a')
//ARGO_PLACEBO
assertEquals("b", buf.getOutputContext().getParent().getCurrentName());
        buf.writeString("cval");
        buf.writeEndObject();
        buf.writeEndObject();
        buf.close();
    }

    public void testBasicSerialize() throws IOException
    {
        TokenBuffer buf;

        // let's see how empty works...
        buf = new TokenBuffer(MAPPER, false);
//ARGO_PLACEBO
assertEquals("", MAPPER.writeValueAsString(buf));
        buf.close();
        
        buf = new TokenBuffer(MAPPER, false);
        buf.writeStartArray();
        buf.writeBoolean(true);
        buf.writeBoolean(false);
        long l = 1L + Integer.MAX_VALUE;
        buf.writeNumber(l);
        buf.writeNumber((short) 4);
        buf.writeNumber(0.5);
        buf.writeEndArray();
//ARGO_PLACEBO
assertEquals(aposToQuotes("[true,false,"+l+",4,0.5]"), MAPPER.writeValueAsString(buf));
        buf.close();

        buf = new TokenBuffer(MAPPER, false);
        buf.writeStartObject();
        buf.writeFieldName(new SerializedString("foo"));
        buf.writeNull();
        buf.writeFieldName("bar");
        buf.writeNumber(BigInteger.valueOf(123));
        buf.writeFieldName("dec");
        buf.writeNumber(BigDecimal.valueOf(5).movePointLeft(2));
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'foo':null,'bar':123,'dec':0.05}"), MAPPER.writeValueAsString(buf));
        buf.close();
    }

    /*
    /**********************************************************
    /* Tests to verify interaction of TokenBuffer and JsonParserSequence
    /**********************************************************
     */
    
    public void testWithJsonParserSequenceSimple() throws IOException
    {
        // Let's join a TokenBuffer with JsonParser first
        TokenBuffer buf = new TokenBuffer(null, false);
        buf.writeStartArray();
        buf.writeString("test");
        JsonParser p = createParserUsingReader("[ true, null ]");
        
        JsonParserSequence seq = JsonParserSequence.createFlattened(false, buf.asParser(), p);
//ARGO_PLACEBO
assertEquals(2, seq.containedParsersCount());

//ARGO_PLACEBO
assertFalse(p.isClosed());
        
//ARGO_PLACEBO
assertFalse(seq.hasCurrentToken());
//ARGO_PLACEBO
assertNull(seq.currentToken());
//ARGO_PLACEBO
assertNull(seq.currentName());

//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, seq.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_STRING, seq.nextToken());
//ARGO_PLACEBO
assertEquals("test", seq.getText());
        // end of first parser input, should switch over:
        
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, seq.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_TRUE, seq.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NULL, seq.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.END_ARRAY, seq.nextToken());

        /* 17-Jan-2009, tatus: At this point, we may or may not get an
         *   exception, depending on how underlying parsers work.
         *   Ideally this should be fixed, probably by asking underlying
         *   parsers to disable checking for balanced start/end markers.
         */

        // for this particular case, we won't get an exception tho...
//ARGO_PLACEBO
assertNull(seq.nextToken());
        // not an error to call again...
//ARGO_PLACEBO
assertNull(seq.nextToken());

        // also: original parsers should be closed
//ARGO_PLACEBO
assertTrue(p.isClosed());
        p.close();
        buf.close();
        seq.close();
    }
    
    /**
     * Test to verify that TokenBuffer and JsonParserSequence work together
     * as expected.
     */
    public void testWithMultipleJsonParserSequences() throws IOException
    {
        TokenBuffer buf1 = new TokenBuffer(null, false);
        buf1.writeStartArray();
        TokenBuffer buf2 = new TokenBuffer(null, false);
        buf2.writeString("a");
        TokenBuffer buf3 = new TokenBuffer(null, false);
        buf3.writeNumber(13);
        TokenBuffer buf4 = new TokenBuffer(null, false);
        buf4.writeEndArray();

        JsonParserSequence seq1 = JsonParserSequence.createFlattened(false, buf1.asParser(), buf2.asParser());
//ARGO_PLACEBO
assertEquals(2, seq1.containedParsersCount());
        JsonParserSequence seq2 = JsonParserSequence.createFlattened(false, buf3.asParser(), buf4.asParser());
//ARGO_PLACEBO
assertEquals(2, seq2.containedParsersCount());
        JsonParserSequence combo = JsonParserSequence.createFlattened(false, seq1, seq2);
        // should flatten it to have 4 underlying parsers
//ARGO_PLACEBO
assertEquals(4, combo.containedParsersCount());

//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, combo.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_STRING, combo.nextToken());
//ARGO_PLACEBO
assertEquals("a", combo.getText());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_INT, combo.nextToken());
//ARGO_PLACEBO
assertEquals(13, combo.getIntValue());
//ARGO_PLACEBO
assertToken(JsonToken.END_ARRAY, combo.nextToken());
//ARGO_PLACEBO
assertNull(combo.nextToken());        
        buf1.close();
        buf2.close();
        buf3.close();
        buf4.close();
    }

    // [databind#743]
    public void testRawValues() throws Exception
    {
        final String RAW = "{\"a\":1}";
        TokenBuffer buf = new TokenBuffer(null, false);
        buf.writeRawValue(RAW);
        // first: raw value won't be transformed in any way:
        JsonParser p = buf.asParser();
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
//ARGO_PLACEBO
assertEquals(RawValue.class, p.getEmbeddedObject().getClass());
//ARGO_PLACEBO
assertNull(p.nextToken());
        p.close();
        buf.close();

        // then verify it would be serialized just fine
//ARGO_PLACEBO
assertEquals(RAW, MAPPER.writeValueAsString(buf));
    }

    // [databind#1730]
    public void testEmbeddedObjectCoerceCheck() throws Exception
    {
        TokenBuffer buf = new TokenBuffer(null, false);
        Object inputPojo = new Sub1730();
        buf.writeEmbeddedObject(inputPojo);

        // first: raw value won't be transformed in any way:
        JsonParser p = buf.asParser();
        Base1730 out = MAPPER.readValue(p, Base1730.class);

//ARGO_PLACEBO
assertSame(inputPojo, out);
        p.close();
        buf.close();
    }
}
