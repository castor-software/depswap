package com.fasterxml.jackson.databind.deser.jdk;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class CollectionDeserTest
    extends BaseMapTest
{
    enum Key {
        KEY1, KEY2, WHATEVER;
    }

    @JsonDeserialize(using=ListDeserializer.class)
    static class CustomList extends LinkedList<String> { }

    static class ListDeserializer extends StdDeserializer<CustomList>
    {
        public ListDeserializer() { super(CustomList.class); }

        @Override
        public CustomList deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException
        {
            CustomList result = new CustomList();
            result.add(jp.getText());
            return result;
        }
    }

    static class XBean {
        public int x;

        public XBean() { }
        public XBean(int x) { this.x = x; }
    }

    // [databind#199]
    static class ListAsIterable {
        public Iterable<String> values;
    }

    // [databind#2251]
    static class ListAsAbstract {
        public AbstractList<String> values;
    }

    static class SetAsAbstract {
        public AbstractSet<String> values;
    }

    static class ListAsIterableX {
        public Iterable<XBean> nums;
    }

    static class KeyListBean {
        public List<Key> keys;
    }

    // [Issue#828]
    @JsonDeserialize(using=SomeObjectDeserializer.class)
    static class SomeObject {}

    static class SomeObjectDeserializer extends StdDeserializer<SomeObject> {
        public SomeObjectDeserializer() { super(SomeObject.class); }

        @Override
        public SomeObject deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            throw new RuntimeException("I want to catch this exception");
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final static ObjectMapper MAPPER = new ObjectMapper();
    
    public void testUntypedList() throws Exception
    {
        // to get "untyped" default List, pass Object.class
        String JSON = "[ \"text!\", true, null, 23 ]";

        // Not a guaranteed cast theoretically, but will work:
        // (since we know that Jackson will construct an ArrayList here...)
        Object value = MAPPER.readValue(JSON, Object.class);
//ARGO_PLACEBO
assertNotNull(value);
//ARGO_PLACEBO
assertTrue(value instanceof ArrayList<?>);
        List<?> result = (List<?>) value;

//ARGO_PLACEBO
assertEquals(4, result.size());

//ARGO_PLACEBO
assertEquals("text!", result.get(0));
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, result.get(1));
//ARGO_PLACEBO
assertNull(result.get(2));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(23), result.get(3));
    }

    public void testExactStringCollection() throws Exception
    {
        // to get typing, must use type reference
        String JSON = "[ \"a\", \"b\" ]";
        List<String> result = MAPPER.readValue(JSON, new TypeReference<ArrayList<String>>() { });

//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(ArrayList.class, result.getClass());
//ARGO_PLACEBO
assertEquals(2, result.size());

//ARGO_PLACEBO
assertEquals("a", result.get(0));
//ARGO_PLACEBO
assertEquals("b", result.get(1));
    }

    public void testHashSet() throws Exception
    {
        String JSON = "[ \"KEY1\", \"KEY2\" ]";

        EnumSet<Key> result = MAPPER.readValue(JSON, new TypeReference<EnumSet<Key>>() { });
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertTrue(EnumSet.class.isAssignableFrom(result.getClass()));
//ARGO_PLACEBO
assertEquals(2, result.size());

//ARGO_PLACEBO
assertTrue(result.contains(Key.KEY1));
//ARGO_PLACEBO
assertTrue(result.contains(Key.KEY2));
//ARGO_PLACEBO
assertFalse(result.contains(Key.WHATEVER));
    }

    /// Test to verify that @JsonDeserialize.using works as expected
    public void testCustomDeserializer() throws IOException
    {
        CustomList result = MAPPER.readValue(quote("abc"), CustomList.class);
//ARGO_PLACEBO
assertEquals(1, result.size());
//ARGO_PLACEBO
assertEquals("abc", result.get(0));
    }

    // Testing "implicit JSON array" for single-element arrays,
    // mostly produced by Jettison, Badgerfish conversions (from XML)
    @SuppressWarnings("unchecked")
    public void testImplicitArrays() throws Exception
    {
        // can't share mapper, custom configs (could create ObjectWriter tho)
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        // first with simple scalar types (numbers), with collections
        List<Integer> ints = mapper.readValue("4", List.class);
//ARGO_PLACEBO
assertEquals(1, ints.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(4), ints.get(0));
        List<String> strings = mapper.readValue(quote("abc"), new TypeReference<ArrayList<String>>() { });
//ARGO_PLACEBO
assertEquals(1, strings.size());
//ARGO_PLACEBO
assertEquals("abc", strings.get(0));
        // and arrays:
        int[] intArray = mapper.readValue("-7", int[].class);
//ARGO_PLACEBO
assertEquals(1, intArray.length);
//ARGO_PLACEBO
assertEquals(-7, intArray[0]);
        String[] stringArray = mapper.readValue(quote("xyz"), String[].class);
//ARGO_PLACEBO
assertEquals(1, stringArray.length);
//ARGO_PLACEBO
assertEquals("xyz", stringArray[0]);

        // and then with Beans:
        List<XBean> xbeanList = mapper.readValue("{\"x\":4}", new TypeReference<List<XBean>>() { });
//ARGO_PLACEBO
assertEquals(1, xbeanList.size());
//ARGO_PLACEBO
assertEquals(XBean.class, xbeanList.get(0).getClass());

        Object ob = mapper.readValue("{\"x\":29}", XBean[].class);
        XBean[] xbeanArray = (XBean[]) ob;
//ARGO_PLACEBO
assertEquals(1, xbeanArray.length);
//ARGO_PLACEBO
assertEquals(XBean.class, xbeanArray[0].getClass());
    }

    // [JACKSON-620]: allow "" to mean 'null' for Maps
    public void testFromEmptyString() throws Exception
    {
        ObjectReader r = MAPPER.reader(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        List<?> result = r.forType(List.class).readValue(quote(""));
//ARGO_PLACEBO
assertNull(result);
    }

    // [databind#161]
    public void testArrayBlockingQueue() throws Exception
    {
        // ok to skip polymorphic type to get Object
        ArrayBlockingQueue<?> q = MAPPER.readValue("[1, 2, 3]", ArrayBlockingQueue.class);
//ARGO_PLACEBO
assertNotNull(q);
//ARGO_PLACEBO
assertEquals(3, q.size());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(1), q.take());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(2), q.take());
//ARGO_PLACEBO
assertEquals(Integer.valueOf(3), q.take());
    }

    // [databind#199]
    public void testIterableWithStrings() throws Exception
    {
        String JSON = "{ \"values\":[\"a\",\"b\"]}";
        ListAsIterable w = MAPPER.readValue(JSON, ListAsIterable.class);
//ARGO_PLACEBO
assertNotNull(w);
//ARGO_PLACEBO
assertNotNull(w.values);
        Iterator<String> it = w.values.iterator();
//ARGO_PLACEBO
assertTrue(it.hasNext());
//ARGO_PLACEBO
assertEquals("a", it.next());
//ARGO_PLACEBO
assertEquals("b", it.next());
//ARGO_PLACEBO
assertFalse(it.hasNext());
    }

    public void testIterableWithBeans() throws Exception
    {
        String JSON = "{ \"nums\":[{\"x\":1},{\"x\":2}]}";
        ListAsIterableX w = MAPPER.readValue(JSON, ListAsIterableX.class);
//ARGO_PLACEBO
assertNotNull(w);
//ARGO_PLACEBO
assertNotNull(w.nums);
        Iterator<XBean> it = w.nums.iterator();
//ARGO_PLACEBO
assertTrue(it.hasNext());
        XBean xb = it.next();
//ARGO_PLACEBO
assertNotNull(xb);
//ARGO_PLACEBO
assertEquals(1, xb.x);
        xb = it.next();
//ARGO_PLACEBO
assertEquals(2, xb.x);
//ARGO_PLACEBO
assertFalse(it.hasNext());
    }

    // for [databind#506]
    public void testArrayIndexForExceptions() throws Exception
    {
        final String OBJECTS_JSON = "[ \"KEY2\", false ]";
        try {
            MAPPER.readValue(OBJECTS_JSON, Key[].class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize");
            List<JsonMappingException.Reference> refs = e.getPath();
//ARGO_PLACEBO
assertEquals(1, refs.size());
//ARGO_PLACEBO
assertEquals(1, refs.get(0).getIndex());
        }

        try {
            MAPPER.readValue("[ \"xyz\", { } ]", String[].class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize");
            List<JsonMappingException.Reference> refs = e.getPath();
//ARGO_PLACEBO
assertEquals(1, refs.size());
//ARGO_PLACEBO
assertEquals(1, refs.get(0).getIndex());
        }

        try {
            MAPPER.readValue("{\"keys\":"+OBJECTS_JSON+"}", KeyListBean.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize");
            List<JsonMappingException.Reference> refs = e.getPath();
//ARGO_PLACEBO
assertEquals(2, refs.size());
            // Bean has no index, but has name:
//ARGO_PLACEBO
assertEquals(-1, refs.get(0).getIndex());
//ARGO_PLACEBO
assertEquals("keys", refs.get(0).getFieldName());

            // and for List, reverse:
//ARGO_PLACEBO
assertEquals(1, refs.get(1).getIndex());
//ARGO_PLACEBO
assertNull(refs.get(1).getFieldName());
        }
    }

    // for [databind#828]
    public void testWrapExceptions() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.WRAP_EXCEPTIONS);

        try {
            mapper.readValue("[{}]", new TypeReference<List<SomeObject>>() {});
        } catch (JsonMappingException exc) {
//ARGO_PLACEBO
assertEquals("I want to catch this exception", exc.getOriginalMessage());
        } catch (RuntimeException exc) {
//ARGO_PLACEBO
fail("The RuntimeException should have been wrapped with a JsonMappingException.");
        }

        ObjectMapper mapperNoWrap = new ObjectMapper();
        mapperNoWrap.disable(DeserializationFeature.WRAP_EXCEPTIONS);

        try {
            mapperNoWrap.readValue("[{}]", new TypeReference<List<SomeObject>>() {});
        } catch (JsonMappingException exc) {
//ARGO_PLACEBO
fail("It should not have wrapped the RuntimeException.");
        } catch (RuntimeException exc) {
//ARGO_PLACEBO
assertEquals("I want to catch this exception", exc.getMessage());
        }
    }

    // [databind#2251]
    public void testAbstractListAndSet() throws Exception
    {
        final String JSON = "{\"values\":[\"foo\", \"bar\"]}";

        ListAsAbstract list = MAPPER.readValue(JSON, ListAsAbstract.class);
//ARGO_PLACEBO
assertEquals(2, list.values.size());
//ARGO_PLACEBO
assertEquals(ArrayList.class, list.values.getClass());

        SetAsAbstract set = MAPPER.readValue(JSON, SetAsAbstract.class);
//ARGO_PLACEBO
assertEquals(2, set.values.size());
//ARGO_PLACEBO
assertEquals(HashSet.class, set.values.getClass());
    }
}
