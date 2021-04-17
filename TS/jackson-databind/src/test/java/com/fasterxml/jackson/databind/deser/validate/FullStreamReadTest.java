package com.fasterxml.jackson.databind.deser.validate;

import java.util.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

/**
 * Test for validating {@link com.fasterxml.jackson.databind.DeserializationFeature#FAIL_ON_TRAILING_TOKENS}.
 */
public class FullStreamReadTest extends BaseMapTest
{
    private final static String JSON_OK_ARRAY = " [ 1, 2, 3]    ";
    private final static String JSON_OK_ARRAY_WITH_COMMENT = JSON_OK_ARRAY + " // stuff ";

    private final static String JSON_FAIL_ARRAY = JSON_OK_ARRAY + " [ ]";

    private final static String JSON_OK_NULL = " null  ";
    private final static String JSON_OK_NULL_WITH_COMMENT = " null /* stuff */ ";
    private final static String JSON_FAIL_NULL = JSON_OK_NULL + " false";
    
    /*
    /**********************************************************
    /* Test methods, config
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    public void testMapperAcceptTrailing() throws Exception
    {
//ARGO_ORIGINAL
assertFalse(MAPPER.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));

        // by default, should be ok to read, all
        //ARGO_ORIGINAL
        _verifyArray(MAPPER.readTree(JSON_OK_ARRAY));
        //ARGO_ORIGINAL
        _verifyArray(MAPPER.readTree(JSON_OK_ARRAY_WITH_COMMENT));
        //ARGO_ORIGINAL
        _verifyArray(MAPPER.readTree(JSON_FAIL_ARRAY));

        // and also via "untyped"
        //ARGO_ORIGINAL
        _verifyCollection(MAPPER.readValue(JSON_OK_ARRAY, List.class));
        //ARGO_ORIGINAL
        _verifyCollection(MAPPER.readValue(JSON_OK_ARRAY_WITH_COMMENT, List.class));
        //ARGO_ORIGINAL
        _verifyCollection(MAPPER.readValue(JSON_FAIL_ARRAY, List.class));

        // ditto for getting `null` and some other token

//ARGO_ORIGINAL
assertTrue(MAPPER.readTree(JSON_OK_NULL).isNull());
//ARGO_ORIGINAL
assertTrue(MAPPER.readTree(JSON_OK_NULL_WITH_COMMENT).isNull());
//ARGO_ORIGINAL
assertTrue(MAPPER.readTree(JSON_FAIL_NULL).isNull());

//ARGO_ORIGINAL
assertNull(MAPPER.readValue(JSON_OK_NULL, Object.class));
//ARGO_ORIGINAL
assertNull(MAPPER.readValue(JSON_OK_NULL_WITH_COMMENT, Object.class));
//ARGO_ORIGINAL
assertNull(MAPPER.readValue(JSON_FAIL_NULL, Object.class));
    }

    public void testMapperFailOnTrailing() throws Exception
    {
        // but things change if we enforce checks
        ObjectMapper strict = newJsonMapper()
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
//ARGO_ORIGINAL
assertTrue(strict.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));

        // some still ok
        //ARGO_ORIGINAL
        _verifyArray(strict.readTree(JSON_OK_ARRAY));
        //ARGO_ORIGINAL
        _verifyCollection(strict.readValue(JSON_OK_ARRAY, List.class));

        // but if real content exists, will fail
        try {
            strict.readTree(JSON_FAIL_ARRAY);
//ARGO_ORIGINAL
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            //ARGO_ORIGINAL
            verifyException(e, "Trailing token (of type START_ARRAY)");
            //ARGO_ORIGINAL
            verifyException(e, "value (bound as `com.fasterxml.jackson.databind.JsonNode`)");
        }

        try {
            strict.readValue(JSON_FAIL_ARRAY, List.class);
//ARGO_ORIGINAL
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            //ARGO_ORIGINAL
            verifyException(e, "Trailing token (of type START_ARRAY)");
            //ARGO_ORIGINAL
            verifyException(e, "value (bound as `java.util.List`)");
        }

        // others fail conditionally: will fail on comments unless enabled

        try {
            strict.readValue(JSON_OK_ARRAY_WITH_COMMENT, List.class);
//ARGO_ORIGINAL
fail("Should not have passed");
        } catch (JsonParseException e) {
            //ARGO_ORIGINAL
            verifyException(e, "Unexpected character");
            //ARGO_ORIGINAL
            verifyException(e, "maybe a (non-standard) comment");
        }
        try {
            strict.readTree(JSON_OK_ARRAY_WITH_COMMENT);
//ARGO_ORIGINAL
fail("Should not have passed");
        } catch (JsonParseException e) {
            //ARGO_ORIGINAL
            verifyException(e, "Unexpected character");
            //ARGO_ORIGINAL
            verifyException(e, "maybe a (non-standard) comment");
        }

        ObjectReader strictWithComments = strict.reader()
                .with(JsonReadFeature.ALLOW_JAVA_COMMENTS);
        //ARGO_ORIGINAL
        _verifyArray(strictWithComments.readTree(JSON_OK_ARRAY_WITH_COMMENT));
        //ARGO_ORIGINAL
        _verifyCollection((List<?>) strictWithComments.forType(List.class)
                .readValue(JSON_OK_ARRAY_WITH_COMMENT));
    }

    public void testMapperFailOnTrailingWithNull() throws Exception
    {
        final ObjectMapper strict = newJsonMapper()
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

        // some still ok
        JsonNode n = strict.readTree(JSON_OK_NULL);
//ARGO_PLACEBO
assertNotNull(n);
//ARGO_PLACEBO
assertTrue(n.isNull());

        // but if real content exists, will fail
        try {
            strict.readTree(JSON_FAIL_NULL);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (of type VALUE_FALSE)");
            verifyException(e, "value (bound as `com.fasterxml.jackson.databind.JsonNode`)");
        }

        try {
            strict.readValue(JSON_FAIL_NULL, List.class);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (of type VALUE_FALSE)");
            verifyException(e, "value (bound as `java.util.List`)");
        }

        // others fail conditionally: will fail on comments unless enabled

        try {
            strict.readValue(JSON_OK_NULL_WITH_COMMENT, Object.class);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (JsonParseException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }
        try {
            strict.readTree(JSON_OK_NULL_WITH_COMMENT);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (JsonParseException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }

        ObjectReader strictWithComments = strict.reader()
                .with(JsonReadFeature.ALLOW_JAVA_COMMENTS);
        n = strictWithComments.readTree(JSON_OK_NULL);
//ARGO_PLACEBO
assertNotNull(n);
//ARGO_PLACEBO
assertTrue(n.isNull());

        Object ob = strictWithComments.forType(List.class)
                .readValue(JSON_OK_NULL_WITH_COMMENT);
//ARGO_PLACEBO
assertNull(ob);
    }
    
    public void testReaderAcceptTrailing() throws Exception
    {
        ObjectReader R = MAPPER.reader();
//ARGO_ORIGINAL
assertFalse(R.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));
//ARGO_ORIGINAL
        _verifyArray(R.readTree(JSON_OK_ARRAY));
        //ARGO_ORIGINAL
        _verifyArray(R.readTree(JSON_OK_ARRAY_WITH_COMMENT));
        //ARGO_ORIGINAL
        _verifyArray(R.readTree(JSON_FAIL_ARRAY));
        ObjectReader rColl = R.forType(List.class);
        //ARGO_ORIGINAL
        _verifyCollection((List<?>)rColl.readValue(JSON_OK_ARRAY));
        //ARGO_ORIGINAL
        _verifyCollection((List<?>)rColl.readValue(JSON_OK_ARRAY_WITH_COMMENT));
        //ARGO_ORIGINAL
        _verifyCollection((List<?>)rColl.readValue(JSON_FAIL_ARRAY));
    }

    public void testReaderFailOnTrailing() throws Exception
    {
        ObjectReader strictR = MAPPER.reader().with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        ObjectReader strictRForList = strictR.forType(List.class);
        _verifyArray(strictR.readTree(JSON_OK_ARRAY));
        _verifyCollection((List<?>)strictRForList.readValue(JSON_OK_ARRAY));

        // Will fail hard if there is a trailing token
        try {
            strictRForList.readValue(JSON_FAIL_ARRAY);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (of type START_ARRAY)");
            verifyException(e, "value (bound as `java.util.List`)");
        }
        try {
            strictR.readTree(JSON_FAIL_ARRAY);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (of type START_ARRAY)");
            verifyException(e, "value (bound as `com.fasterxml.jackson.databind.JsonNode`)");
        }

        // ... also verify that same happens with "value to update"
        try {
            strictR.withValueToUpdate(new ArrayList<Object>())
                .readValue(JSON_FAIL_ARRAY);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (of type START_ARRAY)");
            verifyException(e, "value (bound as `java.util.ArrayList`)");
        }

        // others conditionally: will fail on comments unless enabled

        try {
            strictRForList.readValue(JSON_OK_ARRAY_WITH_COMMENT);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (JsonParseException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }
        try {
            strictR.readTree(JSON_OK_ARRAY_WITH_COMMENT);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (JsonParseException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }

        // but works if comments enabled etc

        ObjectReader strictRWithComments = strictR.with(JsonReadFeature.ALLOW_JAVA_COMMENTS);
        
        _verifyCollection((List<?>)strictRWithComments.forType(List.class).readValue(JSON_OK_ARRAY_WITH_COMMENT));
        _verifyArray(strictRWithComments.readTree(JSON_OK_ARRAY_WITH_COMMENT));
    }

    public void testReaderFailOnTrailingWithNull() throws Exception
    {
        ObjectReader strictR = MAPPER.reader().with(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
        ObjectReader strictRForList = strictR.forType(List.class);
        JsonNode n = strictR.readTree(JSON_OK_NULL);
//ARGO_PLACEBO
assertTrue(n.isNull());

        // Will fail hard if there is a trailing token
        try {
            strictRForList.readValue(JSON_FAIL_NULL);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (of type VALUE_FALSE)");
            verifyException(e, "value (bound as `java.util.List`)");
        }

        try {
            strictR.readTree(JSON_FAIL_NULL);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (of type VALUE_FALSE)");
            verifyException(e, "value (bound as `com.fasterxml.jackson.databind.JsonNode`)");
        }

        // others conditionally: will fail on comments unless enabled

        try {
            strictRForList.readValue(JSON_OK_NULL_WITH_COMMENT);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (JsonParseException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }
        try {
            strictR.readTree(JSON_OK_NULL_WITH_COMMENT);
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (JsonParseException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }

        // but works if comments enabled etc

        ObjectReader strictRWithComments = strictR.with(JsonReadFeature.ALLOW_JAVA_COMMENTS);
        Object ob = strictRWithComments.forType(List.class).readValue(JSON_OK_NULL_WITH_COMMENT);
//ARGO_PLACEBO
assertNull(ob);
    }
    
    private void _verifyArray(JsonNode n) throws Exception
    {
//ARGO_PLACEBO
assertTrue(n.isArray());
//ARGO_PLACEBO
assertEquals(3, n.size());
    }

    private void _verifyCollection(List<?> coll) throws Exception
    {
//ARGO_PLACEBO
assertEquals(3, coll.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(1), coll.get(0));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(2), coll.get(1));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(3), coll.get(2));
    }
}
