package com.fasterxml.jackson.databind.deser.filter;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidNullException;

// For [databind#1402]; configurable null handling, for contents of
// Collections, Maps, arrays
public class NullConversionsForContentTest extends BaseMapTest
{
    static class NullContentFail<T> {
        public T nullsOk;

        @JsonSetter(contentNulls=Nulls.FAIL)
        public T noNulls;
    }

    static class NullContentAsEmpty<T> {
        @JsonSetter(contentNulls=Nulls.AS_EMPTY)
        public T values;
    }

    static class NullContentSkip<T> {
        @JsonSetter(contentNulls=Nulls.SKIP)
        public T values;
    }

    static class NullContentUndefined<T> {
        @JsonSetter // leave with defaults
        public T values;
    }

    /*
    /**********************************************************
    /* Test methods, fail-on-null
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    // Tests to verify that we can set default settings for failure
    public void testFailOnNullFromDefaults() throws Exception
    {
        final String JSON = aposToQuotes("{'values':[null]}");
        TypeReference<NullContentUndefined<List<String>>> listType = new TypeReference<NullContentUndefined<List<String>>>() { };

        // by default fine to get nulls
        NullContentUndefined<List<String>> result = MAPPER.readValue(JSON, listType);
//ARGO_PLACEBO
assertNotNull(result.values);
//ARGO_PLACEBO
assertEquals(1, result.values.size());
//ARGO_PLACEBO
assertNull(result.values.get(0));

        // but not when overridden globally:
        ObjectMapper mapper = newJsonMapper();
        mapper.setDefaultSetterInfo(JsonSetter.Value.forContentNulls(Nulls.FAIL));
        try {
            mapper.readValue(JSON, listType);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"values\"");
//ARGO_PLACEBO
assertEquals(String.class, e.getTargetType());
        }

        // or configured for type:
        mapper = newJsonMapper();
        mapper.configOverride(List.class)
                .setSetterInfo(JsonSetter.Value.forContentNulls(Nulls.FAIL));
        try {
            mapper.readValue(JSON, listType);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"values\"");
//ARGO_PLACEBO
assertEquals(String.class, e.getTargetType());
        }
    }
    
    public void testFailOnNullWithCollections() throws Exception
    {
        TypeReference<NullContentFail<List<Integer>>> typeRef = new TypeReference<NullContentFail<List<Integer>>>() { };

        // first, ok if assigning non-null to not-nullable, null for nullable
        NullContentFail<List<Integer>> result = MAPPER.readValue(aposToQuotes("{'nullsOk':[null]}"),
                typeRef);
//ARGO_PLACEBO
assertNotNull(result.nullsOk);
//ARGO_PLACEBO
assertEquals(1, result.nullsOk.size());
//ARGO_PLACEBO
assertNull(result.nullsOk.get(0));

        // and then see that nulls are not ok for non-nullable.
        
        // List<Integer>
        final String JSON = aposToQuotes("{'noNulls':[null]}");
        try {
            MAPPER.readValue(JSON, typeRef);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"noNulls\"");
//ARGO_PLACEBO
assertEquals(Integer.class, e.getTargetType());
        }

        // List<String>
        try {
            MAPPER.readValue(JSON, new TypeReference<NullContentFail<List<String>>>() { });
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"noNulls\"");
//ARGO_PLACEBO
assertEquals(String.class, e.getTargetType());
        }
    }

    public void testFailOnNullWithArrays() throws Exception
    {
        final String JSON = aposToQuotes("{'noNulls':[null]}");
        // Object[]
        try {
            MAPPER.readValue(JSON, new TypeReference<NullContentFail<Object[]>>() { });
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"noNulls\"");
//ARGO_PLACEBO
assertEquals(Object.class, e.getTargetType());
        }

        // String[]
        try {
            MAPPER.readValue(JSON, new TypeReference<NullContentFail<String[]>>() { });
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"noNulls\"");
//ARGO_PLACEBO
assertEquals(String.class, e.getTargetType());
        }
    }

    public void testFailOnNullWithPrimitiveArrays() throws Exception
    {
        final String JSON = aposToQuotes("{'noNulls':[null]}");

        // boolean[]
        try {
            MAPPER.readValue(JSON, new TypeReference<NullContentFail<boolean[]>>() { });
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"noNulls\"");
//ARGO_PLACEBO
assertEquals(Boolean.TYPE, e.getTargetType());
        }
        // int[]
        try {
            MAPPER.readValue(JSON, new TypeReference<NullContentFail<int[]>>() { });
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"noNulls\"");
//ARGO_PLACEBO
assertEquals(Integer.TYPE, e.getTargetType());
        }
        // double[]
        try {
            MAPPER.readValue(JSON, new TypeReference<NullContentFail<double[]>>() { });
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"noNulls\"");
//ARGO_PLACEBO
assertEquals(Double.TYPE, e.getTargetType());
        }
    }

    public void testFailOnNullWithMaps() throws Exception
    {
        // Then: Map<String,String>
        try {
            final String MAP_JSON = aposToQuotes("{'noNulls':{'a':null}}");
            MAPPER.readValue(MAP_JSON, new TypeReference<NullContentFail<Map<String,String>>>() { });
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"noNulls\"");
//ARGO_PLACEBO
assertEquals(String.class, e.getTargetType());
        }

        // Then: EnumMap<Enum,String>
        try {
            final String MAP_JSON = aposToQuotes("{'noNulls':{'A':null}}");
            MAPPER.readValue(MAP_JSON, new TypeReference<NullContentFail<EnumMap<ABC,String>>>() { });
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidNullException e) {
            verifyException(e, "property \"noNulls\"");
//ARGO_PLACEBO
assertEquals(String.class, e.getTargetType());
        }
    }

    /*
    /**********************************************************
    /* Test methods, null-as-empty
    /**********************************************************
     */

    public void testNullsAsEmptyWithCollections() throws Exception
    {
        final String JSON = aposToQuotes("{'values':[null]}");

        // List<Integer>
        {
            NullContentAsEmpty<List<Integer>> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentAsEmpty<List<Integer>>>() { });
//ARGO_PLACEBO
assertEquals(1, result.values.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(0), result.values.get(0));
        }

        // List<String>
        {
            NullContentAsEmpty<List<String>> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentAsEmpty<List<String>>>() { });
//ARGO_PLACEBO
assertEquals(1, result.values.size());
//ARGO_PLACEBO
assertEquals("", result.values.get(0));
        }
    }

    public void testNullsAsEmptyUsingDefaults() throws Exception
    {
        final String JSON = aposToQuotes("{'values':[null]}");
        TypeReference<NullContentUndefined<List<Integer>>> listType = new TypeReference<NullContentUndefined<List<Integer>>>() { };

        // Let's see defaulting in action
        ObjectMapper mapper = newJsonMapper();
        mapper.setDefaultSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY));
        NullContentUndefined<List<Integer>> result = mapper.readValue(JSON, listType);
//ARGO_PLACEBO
assertEquals(1, result.values.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(0), result.values.get(0));

        // or configured for type:
        mapper = newJsonMapper();
        mapper.configOverride(List.class)
                .setSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY));
        result = mapper.readValue(JSON, listType);
//ARGO_PLACEBO
assertEquals(1, result.values.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(0), result.values.get(0));
    }        
    
    public void testNullsAsEmptyWithArrays() throws Exception
    {
        // Note: skip `Object[]`, no default empty value at this point
        final String JSON = aposToQuotes("{'values':[null]}");

        // Then: String[]
        {
            NullContentAsEmpty<String[]> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentAsEmpty<String[]>>() { });
//ARGO_PLACEBO
assertEquals(1, result.values.length);
//ARGO_PLACEBO
assertEquals("", result.values[0]);
        }
    }

    public void testNullsAsEmptyWithPrimitiveArrays() throws Exception
    {
        final String JSON = aposToQuotes("{'values':[null]}");

        // int[]
        {
            NullContentAsEmpty<int[]> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentAsEmpty<int[]>>() { });
//ARGO_PLACEBO
assertEquals(1, result.values.length);
//ARGO_PLACEBO
assertEquals(0, result.values[0]);
        }

        // long[]
        {
            NullContentAsEmpty<long[]> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentAsEmpty<long[]>>() { });
//ARGO_PLACEBO
assertEquals(1, result.values.length);
//ARGO_PLACEBO
assertEquals(0L, result.values[0]);
        }

        // boolean[]
        {
            NullContentAsEmpty<boolean[]> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentAsEmpty<boolean[]>>() { });
//ARGO_PLACEBO
assertEquals(1, result.values.length);
//ARGO_PLACEBO
assertEquals(false, result.values[0]);
        }
}
    
    public void testNullsAsEmptyWithMaps() throws Exception
    {
        // Then: Map<String,String>
        final String MAP_JSON = aposToQuotes("{'values':{'A':null}}");
        {
            NullContentAsEmpty<Map<String,String>> result 
                = MAPPER.readValue(MAP_JSON, new TypeReference<NullContentAsEmpty<Map<String,String>>>() { });
//ARGO_PLACEBO
assertEquals(1, result.values.size());
//ARGO_PLACEBO
assertEquals("A", result.values.entrySet().iterator().next().getKey());
//ARGO_PLACEBO
assertEquals("", result.values.entrySet().iterator().next().getValue());
        }

        // Then: EnumMap<Enum,String>
        {
            NullContentAsEmpty<EnumMap<ABC,String>> result 
                = MAPPER.readValue(MAP_JSON, new TypeReference<NullContentAsEmpty<EnumMap<ABC,String>>>() { });
//ARGO_PLACEBO
assertEquals(1, result.values.size());
//ARGO_PLACEBO
assertEquals(ABC.A, result.values.entrySet().iterator().next().getKey());
//ARGO_PLACEBO
assertEquals("", result.values.entrySet().iterator().next().getValue());
        }
    }

    /*
    /**********************************************************
    /* Test methods, skip-nulls
    /**********************************************************
     */

    public void testNullsSkipUsingDefaults() throws Exception
    {
        final String JSON = aposToQuotes("{'values':[null]}");
        TypeReference<NullContentUndefined<List<Long>>> listType = new TypeReference<NullContentUndefined<List<Long>>>() { };

        // Let's see defaulting in action
        ObjectMapper mapper = newJsonMapper();
        mapper.setDefaultSetterInfo(JsonSetter.Value.forContentNulls(Nulls.SKIP));
        NullContentUndefined<List<Long>> result = mapper.readValue(JSON, listType);
//ARGO_PLACEBO
assertEquals(0, result.values.size());

        // or configured for type:
        mapper = newJsonMapper();
        mapper.configOverride(List.class)
                .setSetterInfo(JsonSetter.Value.forContentNulls(Nulls.SKIP));
        result = mapper.readValue(JSON, listType);
//ARGO_PLACEBO
assertEquals(0, result.values.size());
    }        

    // Test to verify that per-property setting overrides defaults:
    public void testNullsSkipWithOverrides() throws Exception
    {
        final String JSON = aposToQuotes("{'values':[null]}");
        TypeReference<NullContentSkip<List<Long>>> listType = new TypeReference<NullContentSkip<List<Long>>>() { };

        ObjectMapper mapper = newJsonMapper();
        // defaults call for fail; but POJO specifies "skip"; latter should win
        mapper.setDefaultSetterInfo(JsonSetter.Value.forContentNulls(Nulls.FAIL));
        NullContentSkip<List<Long>> result = mapper.readValue(JSON, listType);
//ARGO_PLACEBO
assertEquals(0, result.values.size());

        // ditto for per-type defaults
        mapper = newJsonMapper();
        mapper.configOverride(List.class)
                .setSetterInfo(JsonSetter.Value.forContentNulls(Nulls.FAIL));
        result = mapper.readValue(JSON, listType);
//ARGO_PLACEBO
assertEquals(0, result.values.size());
    }        

    public void testNullsSkipWithCollections() throws Exception
    {
        // List<Integer>
        {
            final String JSON = aposToQuotes("{'values':[1,null,2]}");
            NullContentSkip<List<Integer>> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentSkip<List<Integer>>>() { });
//ARGO_PLACEBO
assertEquals(2, result.values.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(1), result.values.get(0));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(2), result.values.get(1));
        }

        // List<String>
        {
            final String JSON = aposToQuotes("{'values':['ab',null,'xy']}");
            NullContentSkip<List<String>> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentSkip<List<String>>>() { });
//ARGO_PLACEBO
assertEquals(2, result.values.size());
//ARGO_PLACEBO
assertEquals("ab", result.values.get(0));
//ARGO_PLACEBO
assertEquals("xy", result.values.get(1));
        }
    }

    public void testNullsSkipWithArrays() throws Exception
    {
        final String JSON = aposToQuotes("{'values':['a',null,'xy']}");
        // Object[]
        {
            NullContentSkip<Object[]> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentSkip<Object[]>>() { });
//ARGO_PLACEBO
assertEquals(2, result.values.length);
//ARGO_PLACEBO
assertEquals("a", result.values[0]);
//ARGO_PLACEBO
assertEquals("xy", result.values[1]);
        }
        // String[]
        {
            NullContentSkip<String[]> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentSkip<String[]>>() { });
//ARGO_PLACEBO
assertEquals(2, result.values.length);
//ARGO_PLACEBO
assertEquals("a", result.values[0]);
//ARGO_PLACEBO
assertEquals("xy", result.values[1]);
        }
    }

    public void testNullsSkipWithPrimitiveArrays() throws Exception
    {
        // int[]
        {
            final String JSON = aposToQuotes("{'values':[3,null,7]}");
            NullContentSkip<int[]> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentSkip<int[]>>() { });
//ARGO_PLACEBO
assertEquals(2, result.values.length);
//ARGO_PLACEBO
assertEquals(3, result.values[0]);
//ARGO_PLACEBO
assertEquals(7, result.values[1]);
        }

        // long[]
        {
            final String JSON = aposToQuotes("{'values':[-13,null,999]}");
            NullContentSkip<long[]> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentSkip<long[]>>() { });
//ARGO_PLACEBO
assertEquals(2, result.values.length);
//ARGO_PLACEBO
assertEquals(-13L, result.values[0]);
//ARGO_PLACEBO
assertEquals(999L, result.values[1]);
        }

        // boolean[]
        {
            final String JSON = aposToQuotes("{'values':[true,null,true]}");
            NullContentSkip<boolean[]> result = MAPPER.readValue(JSON,
                    new TypeReference<NullContentSkip<boolean[]>>() { });
//ARGO_PLACEBO
assertEquals(2, result.values.length);
//ARGO_PLACEBO
assertEquals(true, result.values[0]);
//ARGO_PLACEBO
assertEquals(true, result.values[1]);
        }
    }
    
    public void testNullsSkipWithMaps() throws Exception
    {
        // Then: Map<String,String>
        final String MAP_JSON = aposToQuotes("{'values':{'A':'foo','B':null,'C':'bar'}}");
        {
            NullContentSkip<Map<String,String>> result 
                = MAPPER.readValue(MAP_JSON, new TypeReference<NullContentSkip<Map<String,String>>>() { });
//ARGO_PLACEBO
assertEquals(2, result.values.size());
//ARGO_PLACEBO
assertEquals("foo", result.values.get("A"));
//ARGO_PLACEBO
assertEquals("bar", result.values.get("C"));
        }

        // Then: EnumMap<Enum,String>
        {
            NullContentSkip<EnumMap<ABC,String>> result 
                = MAPPER.readValue(MAP_JSON, new TypeReference<NullContentSkip<EnumMap<ABC,String>>>() { });
//ARGO_PLACEBO
assertEquals(2, result.values.size());
//ARGO_PLACEBO
assertEquals("foo", result.values.get(ABC.A));
//ARGO_PLACEBO
assertEquals("bar", result.values.get(ABC.C));
        }
    }
}
