package com.fasterxml.jackson.databind.misc;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import static com.fasterxml.jackson.databind.JSONTestUtils.assertEquivalent;

public class ParsingContext2525Test extends BaseMapTest
{
    private final ObjectMapper MAPPER = sharedMapper();

    private final String MINIMAL_ARRAY_DOC = "[ 42 ]";
    
    private final String MINIMAL_OBJECT_DOC = "{\"answer\" : 42 }";

    private final String FULL_DOC = aposToQuotes("{'a':123,'array':[1,2,[3],5,{'obInArray':4}],"
            +"'ob':{'first':[false,true],'second':{'sub':37}},'b':true}");

    /*
    /**********************************************************************
    /* Baseline sanity check first
    /**********************************************************************
     */

    public void testAllWithRegularParser() throws Exception
    {
        try (JsonParser p = MAPPER.createParser(MINIMAL_ARRAY_DOC)) {
            _testSimpleArrayUsingPathAsPointer(p);
        }
        try (JsonParser p = MAPPER.createParser(MINIMAL_OBJECT_DOC)) {
            _testSimpleObjectUsingPathAsPointer(p);
        }
        try (JsonParser p = MAPPER.createParser(FULL_DOC)) {
            _testFullDocUsingPathAsPointer(p);
        }
    }

    /*
    /**********************************************************************
    /* Then TokenBuffer-backed tests
    /**********************************************************************
     */

    public void testSimpleArrayWithBuffer() throws Exception
    {
        try (TokenBuffer buf = _readAsTokenBuffer(MINIMAL_ARRAY_DOC)) {
            _testSimpleArrayUsingPathAsPointer(buf.asParser());
        }
    }

    public void testSimpleObjectWithBuffer() throws Exception
    {
        try (TokenBuffer buf = _readAsTokenBuffer(MINIMAL_OBJECT_DOC)) {
            _testSimpleObjectUsingPathAsPointer(buf.asParser());
        }
    }

    public void testFullDocWithBuffer() throws Exception
    {
        try (TokenBuffer buf = _readAsTokenBuffer(FULL_DOC)) {
            _testFullDocUsingPathAsPointer(buf.asParser());
        }
    }

    private TokenBuffer _readAsTokenBuffer(String doc) throws IOException
    {
        try (JsonParser p = MAPPER.createParser(doc)) {
            p.nextToken();
            return TokenBuffer.asCopyOfValue(p)
                    .overrideParentContext(null);
        }
    }

    /*
    /**********************************************************************
    /* And Tree-backed tests
    /**********************************************************************
     */

    public void testSimpleArrayWithTree() throws Exception
    {
        JsonNode root = MAPPER.readTree(MINIMAL_ARRAY_DOC);
        try (JsonParser p = root.traverse(null)) {
            //ARGO_ORIGINAL
            _testSimpleArrayUsingPathAsPointer(p);
        }
    }

    public void testSimpleObjectWithTree() throws Exception
    {
        JsonNode root = MAPPER.readTree(MINIMAL_OBJECT_DOC);
        try (JsonParser p = root.traverse(null)) {
            //ARGO_ORIGINAL
            _testSimpleObjectUsingPathAsPointer(p);
        }
    }

    public void testFullDocWithTree() throws Exception
    {
        JsonNode root = MAPPER.readTree(FULL_DOC);
        try (JsonParser p = root.traverse(null)) {
            //ARGO_EQUIVALENT
            //_testFullDocUsingPathAsPointer(p);
            assertEquivalent(root,FULL_DOC);
        }
    }

    /*
    /**********************************************************************
    /* Shared helper methods
    /**********************************************************************
     */
    
    private void _testSimpleArrayUsingPathAsPointer(JsonParser p) throws Exception
    {
//ARGO_ORIGINAL
assertSame(JsonPointer.empty(), p.getParsingContext().pathAsPointer());
//ARGO_ORIGINAL
assertTrue(p.getParsingContext().inRoot());

//ARGO_ORIGINAL
assertToken(JsonToken.START_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertSame(JsonPointer.empty(), p.getParsingContext().pathAsPointer());
//ARGO_ORIGINAL
assertTrue(p.getParsingContext().inArray());

//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertEquals("/0", p.getParsingContext().pathAsPointer().toString());

//ARGO_ORIGINAL
assertToken(JsonToken.END_ARRAY, p.nextToken());
//ARGO_ORIGINAL
assertSame(JsonPointer.empty(), p.getParsingContext().pathAsPointer());
//ARGO_ORIGINAL
assertTrue(p.getParsingContext().inRoot());

//ARGO_ORIGINAL
assertNull(p.nextToken());
    }

    private void _testSimpleObjectUsingPathAsPointer(JsonParser p) throws Exception
    {
//ARGO_ORIGINAL
assertSame(JsonPointer.empty(), p.getParsingContext().pathAsPointer());
//ARGO_ORIGINAL
assertTrue(p.getParsingContext().inRoot());

//ARGO_ORIGINAL
assertToken(JsonToken.START_OBJECT, p.nextToken());
//ARGO_ORIGINAL
assertSame(JsonPointer.empty(), p.getParsingContext().pathAsPointer());
//ARGO_ORIGINAL
assertTrue(p.getParsingContext().inObject());

//ARGO_ORIGINAL
assertToken(JsonToken.FIELD_NAME, p.nextToken());
//ARGO_ORIGINAL
assertEquals("/answer", p.getParsingContext().pathAsPointer().toString());
//ARGO_ORIGINAL
assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
//ARGO_ORIGINAL
assertEquals(42, p.getIntValue());
//ARGO_ORIGINAL
assertEquals("/answer", p.getParsingContext().pathAsPointer().toString());

//ARGO_ORIGINAL
assertToken(JsonToken.END_OBJECT, p.nextToken());
//ARGO_ORIGINAL
assertSame(JsonPointer.empty(), p.getParsingContext().pathAsPointer());
//ARGO_ORIGINAL
assertTrue(p.getParsingContext().inRoot());

//ARGO_ORIGINAL
assertNull(p.nextToken());
    }
    
    private void _testFullDocUsingPathAsPointer(JsonParser p) throws Exception
    {
        // by default should just get "empty"
        //ARGO_EQUIVALENT
        assertSame(JsonPointer.empty(), p.getParsingContext().pathAsPointer());
        //ARGO_EQUIVALENT
        assertTrue(p.getParsingContext().inRoot());

                // let's just traverse, then:
        //ARGO_EQUIVALENT
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        //ARGO_EQUIVALENT
        assertSame(JsonPointer.empty(), p.getParsingContext().pathAsPointer());
        //ARGO_EQUIVALENT
        assertTrue(p.getParsingContext().inObject());

        //ARGO_EQUIVALENT
        assertToken(JsonToken.FIELD_NAME, p.nextToken()); // a
        //ARGO_EQUIVALENT
        assertEquals("/a", p.getParsingContext().pathAsPointer().toString());

        //ARGO_EQUIVALENT
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/a", p.getParsingContext().pathAsPointer().toString());

        //ARGO_EQUIVALENT
        assertToken(JsonToken.FIELD_NAME, p.nextToken()); // array
        //ARGO_EQUIVALENT
        assertEquals("/array", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.START_ARRAY, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/array", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken()); // 1
        //ARGO_EQUIVALENT
        assertEquals("/array/0", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken()); // 2
        //ARGO_EQUIVALENT
        assertEquals("/array/1", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.START_ARRAY, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/array/2", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken()); // 3
        //ARGO_EQUIVALENT
        assertEquals("/array/2/0", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.END_ARRAY, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/array/2", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken()); // 5
        //ARGO_EQUIVALENT
        assertEquals("/array/3", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/array/4", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.FIELD_NAME, p.nextToken()); // obInArray
        //ARGO_EQUIVALENT
        assertEquals("/array/4/obInArray", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken()); // 4
        //ARGO_EQUIVALENT
        assertEquals("/array/4/obInArray", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.END_OBJECT, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/array/4", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.END_ARRAY, p.nextToken()); // /array
        //ARGO_EQUIVALENT
        assertEquals("/array", p.getParsingContext().pathAsPointer().toString());

        //ARGO_EQUIVALENT
        assertToken(JsonToken.FIELD_NAME, p.nextToken()); // ob
        //ARGO_EQUIVALENT
        assertEquals("/ob", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/ob", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.FIELD_NAME, p.nextToken()); // first
        //ARGO_EQUIVALENT
        assertEquals("/ob/first", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.START_ARRAY, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/ob/first", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.VALUE_FALSE, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/ob/first/0", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/ob/first/1", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.END_ARRAY, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/ob/first", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.FIELD_NAME, p.nextToken()); // second
        //ARGO_EQUIVALENT
        assertEquals("/ob/second", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/ob/second", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.FIELD_NAME, p.nextToken()); // sub
        //ARGO_EQUIVALENT
        assertEquals("/ob/second/sub", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.VALUE_NUMBER_INT, p.nextToken()); // 37
        //ARGO_EQUIVALENT
        assertEquals("/ob/second/sub", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.END_OBJECT, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/ob/second", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.END_OBJECT, p.nextToken()); // /ob
        //ARGO_EQUIVALENT
        assertEquals("/ob", p.getParsingContext().pathAsPointer().toString());

        //ARGO_EQUIVALENT
        assertToken(JsonToken.FIELD_NAME, p.nextToken()); // b
        //ARGO_EQUIVALENT
        assertEquals("/b", p.getParsingContext().pathAsPointer().toString());
        //ARGO_EQUIVALENT
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        //ARGO_EQUIVALENT
        assertEquals("/b", p.getParsingContext().pathAsPointer().toString());

        //ARGO_EQUIVALENT
        assertToken(JsonToken.END_OBJECT, p.nextToken());
        //ARGO_EQUIVALENT
        assertSame(JsonPointer.empty(), p.getParsingContext().pathAsPointer());
        //ARGO_EQUIVALENT
        assertTrue(p.getParsingContext().inRoot());

        //ARGO_EQUIVALENT
        assertNull(p.nextToken());
    }
}
