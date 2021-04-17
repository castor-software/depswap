package com.fasterxml.jackson.databind.deser.merge;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonMerge;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CollectionMergeTest extends BaseMapTest
{
    static class CollectionWrapper {
        @JsonMerge
        public Collection<String> bag = new TreeSet<String>();
        {
            bag.add("a");
        }
    }

    static class MergedList
    {
        @JsonMerge
        public List<String> values = new ArrayList<>();
        {
            values.add("a");
        }
    }

    static class MergedEnumSet
    {
        @JsonMerge
        public EnumSet<ABC> abc = EnumSet.of(ABC.B);
    }

    static class MergedX<T>
    {
        @JsonMerge
        T value;

        public MergedX(T v) { value = v; }
        protected MergedX() { }

        public void setValue(T v) { value = v; }
    }

    /*
    /********************************************************
    /* Test methods
    /********************************************************
     */

    private final ObjectMapper MAPPER = jsonMapperBuilder()
            // 26-Oct-2016, tatu: Make sure we'll report merge problems by default
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .build();

    public void testCollectionMerging() throws Exception
    {
        CollectionWrapper w = MAPPER.readValue(aposToQuotes("{'bag':['b']}"), CollectionWrapper.class);
//ARGO_PLACEBO
assertEquals(2, w.bag.size());
//ARGO_PLACEBO
assertTrue(w.bag.contains("a"));
//ARGO_PLACEBO
assertTrue(w.bag.contains("b"));
    }

    public void testListMerging() throws Exception
    {
        MergedList w = MAPPER.readValue(aposToQuotes("{'values':['x']}"), MergedList.class);
//ARGO_PLACEBO
assertEquals(2, w.values.size());
//ARGO_PLACEBO
assertTrue(w.values.contains("a"));
//ARGO_PLACEBO
assertTrue(w.values.contains("x"));
    }

    // Test that uses generic type
    public void testGenericListMerging() throws Exception
    {
        Collection<String> l = new ArrayList<>();
        l.add("foo");
        MergedX<Collection<String>> input = new MergedX<Collection<String>>(l);

        MergedX<Collection<String>> result = MAPPER
                .readerFor(new TypeReference<MergedX<Collection<String>>>() {})
                .withValueToUpdate(input)
                .readValue(aposToQuotes("{'value':['bar']}"));
//ARGO_PLACEBO
assertSame(input, result);
//ARGO_PLACEBO
assertEquals(2, result.value.size());
        Iterator<String> it = result.value.iterator();
//ARGO_PLACEBO
assertEquals("foo", it.next());
//ARGO_PLACEBO
assertEquals("bar", it.next());
    }

    public void testEnumSetMerging() throws Exception
    {
        MergedEnumSet result = MAPPER.readValue(aposToQuotes("{'abc':['A']}"), MergedEnumSet.class);
//ARGO_PLACEBO
assertEquals(2, result.abc.size());
//ARGO_PLACEBO
assertTrue(result.abc.contains(ABC.B)); // original
//ARGO_PLACEBO
assertTrue(result.abc.contains(ABC.A)); // added
    }

}
