package com.fasterxml.jackson.databind.deser.jdk;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class MapDeserializationTest
    extends BaseMapTest
{
    static enum Key {
        KEY1, KEY2, WHATEVER;
    }

    static class BrokenMap
        extends HashMap<Object,Object>
    {
        // No default ctor, nor @JsonCreators
        public BrokenMap(boolean dummy) { super(); }
    }

    @JsonDeserialize(using=CustomMapDeserializer.class)
    static class CustomMap extends LinkedHashMap<String,String> { }

    static class CustomMapDeserializer extends StdDeserializer<CustomMap>
    {
        public CustomMapDeserializer() { super(CustomMap.class); }
        @Override
        public CustomMap deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException
        {
            CustomMap result = new CustomMap();
            result.put("x", p.getText());
            return result;
        }
    }

    static class KeyType {
        protected String value;
        
        private KeyType(String v, boolean bogus) {
            value = v;
        }

        @JsonCreator
        public static KeyType create(String v) {
            return new KeyType(v, true);
        }
    }

    public static class EnumMapContainer {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
        public EnumMap<KeyEnum,ITestType> testTypes;
    }

    public static class ListContainer {
        public List<ITestType> testTypes;
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public static interface ITestType { }

    public static enum KeyEnum {
        A, B
    }
    public static enum ConcreteType implements ITestType {
        ONE, TWO;
    }

    static class ClassStringMap extends HashMap<Class<?>,String> { }
    
    static class AbstractMapWrapper {
        public AbstractMap<String, Integer> values;
    }

    /*
    /**********************************************************
    /* Test methods, untyped (Object valued) maps
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testBigUntypedMap() throws Exception
    {
        Map<String,Object> map = new LinkedHashMap<String,Object>();
        for (int i = 0; i < 1100; ++i) {
            if ((i & 1) == 0) {
                map.put(String.valueOf(i), Integer.valueOf(i));
            } else {
                Map<String,Object> map2 = new LinkedHashMap<String,Object>();
                map2.put("x", Integer.valueOf(i));
                map.put(String.valueOf(i), map2);
            }
        }
        String json = MAPPER.writeValueAsString(map);
        Object bound = MAPPER.readValue(json, Object.class);
//ARGO_PLACEBO
assertEquals(map, bound);
    }
    
    /**
     * Let's also try another way to express "gimme a Map" deserialization;
     * this time by specifying a Map class, to reduce need to cast
     */
    public void testUntypedMap2() throws Exception
    {
        // to get "untyped" default map-to-map, pass Object.class
        String JSON = "{ \"a\" : \"x\" }";

        @SuppressWarnings("unchecked")
        HashMap<String,Object> result = /*(HashMap<String,Object>)*/ MAPPER.readValue(JSON, HashMap.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertTrue(result instanceof Map<?,?>);

//ARGO_PLACEBO
assertEquals(1, result.size());

//ARGO_PLACEBO
assertEquals("x", result.get("a"));
    }

    /**
     * Unit test for [JACKSON-185]
     */
    public void testUntypedMap3() throws Exception
    {
        String JSON = "{\"a\":[{\"a\":\"b\"},\"value\"]}";
        Map<?,?> result = MAPPER.readValue(JSON, Map.class);
//ARGO_PLACEBO
assertTrue(result instanceof Map<?,?>);
//ARGO_PLACEBO
assertEquals(1, result.size());
        Object ob = result.get("a");
//ARGO_PLACEBO
assertNotNull(ob);
        Collection<?> list = (Collection<?>)ob;
//ARGO_PLACEBO
assertEquals(2, list.size());

        JSON = "{ \"var1\":\"val1\", \"var2\":\"val2\", "
            +"\"subvars\": ["
            +" {  \"subvar1\" : \"subvar2\", \"x\" : \"y\" }, "
            +" { \"a\":1 } ]"
            +" }"
            ;
        result = MAPPER.readValue(JSON, Map.class);
//ARGO_PLACEBO
assertTrue(result instanceof Map<?,?>);
//ARGO_PLACEBO
assertEquals(3, result.size());
    }

    private static final String UNTYPED_MAP_JSON =
            "{ \"double\":42.0, \"string\":\"string\","
            +"\"boolean\":true, \"list\":[\"list0\"],"
            +"\"null\":null }";
    
    static class ObjectWrapperMap extends HashMap<String, ObjectWrapper> { }
    
    public void testSpecialMap() throws IOException
    {
       final ObjectWrapperMap map = MAPPER.readValue(UNTYPED_MAP_JSON, ObjectWrapperMap.class);
//ARGO_PLACEBO
assertNotNull(map);
       _doTestUntyped(map);
    }

    public void testGenericMap() throws IOException
    {
        final Map<String, ObjectWrapper> map = MAPPER.readValue
            (UNTYPED_MAP_JSON,
             new TypeReference<Map<String, ObjectWrapper>>() { });
       _doTestUntyped(map);
    }
    
    private void _doTestUntyped(final Map<String, ObjectWrapper> map)
    {
        ObjectWrapper w = map.get("double");
//ARGO_PLACEBO
assertNotNull(w);
//ARGO_PLACEBO
assertEquals(Double.valueOf(42), w.getObject());
//ARGO_PLACEBO
assertEquals("string", map.get("string").getObject());
//ARGO_PLACEBO
assertEquals(Boolean.TRUE, map.get("boolean").getObject());
//ARGO_PLACEBO
assertEquals(Collections.singletonList("list0"), map.get("list").getObject());
//ARGO_PLACEBO
assertTrue(map.containsKey("null"));
//ARGO_PLACEBO
assertNull(map.get("null"));
//ARGO_PLACEBO
assertEquals(5, map.size());
    }
    
    // [JACKSON-620]: allow "" to mean 'null' for Maps
    public void testFromEmptyString() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        m.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        Map<?,?> result = m.readValue(quote(""), Map.class);
//ARGO_PLACEBO
assertNull(result);
    }

    /*
    /**********************************************************
    /* Test methods, typed maps
    /**********************************************************
     */

    public void testExactStringIntMap() throws Exception
    {
        // to get typing, must use type reference
        String JSON = "{ \"foo\" : 13, \"bar\" : -39, \n \"\" : 0 }";
        Map<String,Integer> result = MAPPER.readValue
            (JSON, new TypeReference<HashMap<String,Integer>>() { });

//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(HashMap.class, result.getClass());
//ARGO_PLACEBO
assertEquals(3, result.size());

//ARGO_PLACEBO
assertEquals(Integer.valueOf(13), result.get("foo"));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(-39), result.get("bar"));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(0), result.get(""));
//ARGO_PLACEBO
assertNull(result.get("foobar"));
//ARGO_PLACEBO
assertNull(result.get(" "));
    }

    /**
     * Let's also check that it is possible to do type conversions
     * to allow use of non-String Map keys.
     */
    public void testIntBooleanMap() throws Exception
    {
        // to get typing, must use type reference
        String JSON = "{ \"1\" : true, \"-1\" : false }";
        Map<?,Object> result = MAPPER.readValue
            (JSON, new TypeReference<HashMap<Integer,Object>>() { });

//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(HashMap.class, result.getClass());
//ARGO_PLACEBO
assertEquals(2, result.size());

//ARGO_PLACEBO
assertEquals(Boolean.TRUE, result.get(Integer.valueOf(1)));
//ARGO_PLACEBO
assertEquals(Boolean.FALSE, result.get(Integer.valueOf(-1)));
//ARGO_PLACEBO
assertNull(result.get("foobar"));
//ARGO_PLACEBO
assertNull(result.get(0));
    }

    public void testExactStringStringMap() throws Exception
    {
        // to get typing, must use type reference
        String JSON = "{ \"a\" : \"b\" }";
        TreeMap<String,String> result = MAPPER.readValue
            (JSON, new TypeReference<TreeMap<String,String>>() { });

//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(TreeMap.class, result.getClass());
//ARGO_PLACEBO
assertEquals(1, result.size());

//ARGO_PLACEBO
assertEquals("b", result.get("a"));
//ARGO_PLACEBO
assertNull(result.get("b"));
    }

    /**
     * Unit test that verifies that it's ok to have incomplete
     * information about Map class itself, as long as it's something
     * we good guess about: for example, <code>Map.Class</code> will
     * be replaced by something like <code>HashMap.class</code>,
     * if given.
     */
    public void testGenericStringIntMap() throws Exception
    {
        // to get typing, must use type reference; but with abstract type
        String JSON = "{ \"a\" : 1, \"b\" : 2, \"c\" : -99 }";
        Map<String,Integer> result = MAPPER.readValue
            (JSON, new TypeReference<Map<String,Integer>>() { });
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertTrue(result instanceof Map<?,?>);
//ARGO_PLACEBO
assertEquals(3, result.size());

//ARGO_PLACEBO
assertEquals(Integer.valueOf(-99), result.get("c"));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(2), result.get("b"));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(1), result.get("a"));

//ARGO_PLACEBO
assertNull(result.get(""));
    }

    public void testAbstractMapDefault() throws Exception
    {
        final AbstractMapWrapper result = MAPPER.readValue("{\"values\":{\"foo\":42}}",
                AbstractMapWrapper.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(LinkedHashMap.class, result.values.getClass());
    }

    /*
    /**********************************************************
    /* Test methods, maps with enums
    /**********************************************************
     */

    public void testEnumMap() throws Exception
    {
        String JSON = "{ \"KEY1\" : \"\", \"WHATEVER\" : null }";

        // to get typing, must use type reference
        EnumMap<Key,String> result = MAPPER.readValue
            (JSON, new TypeReference<EnumMap<Key,String>>() { });

//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(EnumMap.class, result.getClass());
//ARGO_PLACEBO
assertEquals(2, result.size());

//ARGO_PLACEBO
assertEquals("", result.get(Key.KEY1));
        // null should be ok too...
//ARGO_PLACEBO
assertTrue(result.containsKey(Key.WHATEVER));
//ARGO_PLACEBO
assertNull(result.get(Key.WHATEVER));

        // plus we have nothing for this key
//ARGO_PLACEBO
assertFalse(result.containsKey(Key.KEY2));
//ARGO_PLACEBO
assertNull(result.get(Key.KEY2));
    }

    public void testMapWithEnums() throws Exception
    {
        String JSON = "{ \"KEY2\" : \"WHATEVER\" }";

        // to get typing, must use type reference
        Map<Key,Key> result = MAPPER.readValue
            (JSON, new TypeReference<Map<Key,Key>>() { });

//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertTrue(result instanceof Map<?,?>);
//ARGO_PLACEBO
assertEquals(1, result.size());

//ARGO_PLACEBO
assertEquals(Key.WHATEVER, result.get(Key.KEY2));
//ARGO_PLACEBO
assertNull(result.get(Key.WHATEVER));
//ARGO_PLACEBO
assertNull(result.get(Key.KEY1));
    }

    public void testEnumPolymorphicSerializationTest() throws Exception 
    {
        ObjectMapper mapper = new ObjectMapper();
        List<ITestType> testTypesList = new ArrayList<ITestType>();
        testTypesList.add(ConcreteType.ONE);
        testTypesList.add(ConcreteType.TWO);
        ListContainer listContainer = new ListContainer();
        listContainer.testTypes = testTypesList;
        String json = mapper.writeValueAsString(listContainer);
        listContainer = mapper.readValue(json, ListContainer.class);
        EnumMapContainer enumMapContainer = new EnumMapContainer();
        EnumMap<KeyEnum,ITestType> testTypesMap = new EnumMap<KeyEnum,ITestType>(KeyEnum.class);
        testTypesMap.put(KeyEnum.A, ConcreteType.ONE);
        testTypesMap.put(KeyEnum.B, ConcreteType.TWO);
        enumMapContainer.testTypes = testTypesMap;
        
        json = mapper.writeValueAsString(enumMapContainer);
        enumMapContainer = mapper.readValue(json, EnumMapContainer.class);
    }

    /*
    /**********************************************************
    /* Test methods, maps with Date
    /**********************************************************
     */
    public void testDateMap() throws Exception
    {
    	 Date date1=new Date(123456000L);
    	 DateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
         
    	 String JSON = "{ \""+  fmt.format(date1)+"\" : \"\", \""+new Date(0).getTime()+"\" : null }";
    	 HashMap<Date,String> result=  MAPPER.readValue
    	            (JSON, new TypeReference<HashMap<Date,String>>() { });
    	 
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(HashMap.class, result.getClass());
//ARGO_PLACEBO
assertEquals(2, result.size());
    	 
//ARGO_PLACEBO
assertTrue(result.containsKey(date1));
//ARGO_PLACEBO
assertEquals("", result.get(new Date(123456000L)));

//ARGO_PLACEBO
assertTrue(result.containsKey(new Date(0)));
//ARGO_PLACEBO
assertNull(result.get(new Date(0)));
    }

    /*
    /**********************************************************
    /* Test methods, maps with various alternative key types
    /**********************************************************
     */

    public void testCalendarMap() throws Exception
    {
        // 18-Jun-2015, tatu: Should be safest to use default timezone that mapper would use
        TimeZone tz = MAPPER.getSerializationConfig().getTimeZone();        
        Calendar c = Calendar.getInstance(tz);

        c.setTimeInMillis(123456000L);
        DateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        String JSON = "{ \""+fmt.format(c.getTime())+"\" : \"\", \""+new Date(0).getTime()+"\" : null }";
        HashMap<Calendar,String> result = MAPPER.readValue
                (JSON, new TypeReference<HashMap<Calendar,String>>() { });

//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(HashMap.class, result.getClass());
//ARGO_PLACEBO
assertEquals(2, result.size());

//ARGO_PLACEBO
assertTrue(result.containsKey(c));
//ARGO_PLACEBO
assertEquals("", result.get(c));
        c.setTimeInMillis(0);
//ARGO_PLACEBO
assertTrue(result.containsKey(c));
//ARGO_PLACEBO
assertNull(result.get(c));
    }

    public void testUUIDKeyMap() throws Exception
    {
         UUID key = UUID.nameUUIDFromBytes("foobar".getBytes("UTF-8"));
         String JSON = "{ \""+key+"\":4}";
         Map<UUID,Object> result = MAPPER.readValue(JSON, new TypeReference<Map<UUID,Object>>() { });
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result.size());
         Object ob = result.keySet().iterator().next();
//ARGO_PLACEBO
assertNotNull(ob);
//ARGO_PLACEBO
assertEquals(UUID.class, ob.getClass());
//ARGO_PLACEBO
assertEquals(key, ob);
    }

    public void testLocaleKeyMap() throws Exception {
        Locale key = Locale.CHINA;
        String JSON = "{ \"" + key + "\":4}";
        Map<Locale, Object> result = MAPPER.readValue(JSON, new TypeReference<Map<Locale, Object>>() {
        });
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result.size());
        Object ob = result.keySet().iterator().next();
//ARGO_PLACEBO
assertNotNull(ob);
//ARGO_PLACEBO
assertEquals(Locale.class, ob.getClass());
//ARGO_PLACEBO
assertEquals(key, ob);
    }

    public void testCurrencyKeyMap() throws Exception {
        Currency key = Currency.getInstance("USD");
        String JSON = "{ \"" + key + "\":4}";
        Map<Currency, Object> result = MAPPER.readValue(JSON, new TypeReference<Map<Currency, Object>>() {
        });
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result.size());
        Object ob = result.keySet().iterator().next();
//ARGO_PLACEBO
assertNotNull(ob);
//ARGO_PLACEBO
assertEquals(Currency.class, ob.getClass());
//ARGO_PLACEBO
assertEquals(key, ob);
    }

    // Test confirming that @JsonCreator may be used with Map Key types
    public void testKeyWithCreator() throws Exception
    {
        // first, key should deserialize normally:
        KeyType key = MAPPER.readValue(quote("abc"), KeyType.class);
//ARGO_PLACEBO
assertEquals("abc", key.value);

        Map<KeyType,Integer> map = MAPPER.readValue("{\"foo\":3}", new TypeReference<Map<KeyType,Integer>>() {} );
//ARGO_PLACEBO
assertEquals(1, map.size());
        key = map.keySet().iterator().next();
//ARGO_PLACEBO
assertEquals("foo", key.value);
    }

    public void testClassKeyMap() throws Exception {
        ClassStringMap map = MAPPER.readValue(aposToQuotes("{'java.lang.String':'foo'}"),
                ClassStringMap.class);
//ARGO_PLACEBO
assertNotNull(map);
//ARGO_PLACEBO
assertEquals(1, map.size());
//ARGO_PLACEBO
assertEquals("foo", map.get(String.class));
    }

    public void testcharSequenceKeyMap() throws Exception {
        String JSON = aposToQuotes("{'a':'b'}");
        Map<CharSequence,String> result = MAPPER.readValue(JSON, new TypeReference<Map<CharSequence,String>>() { });
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result.size());
//ARGO_PLACEBO
assertEquals("b", result.get("a"));
    }

    /*
    /**********************************************************
    /* Test methods, annotated Maps
    /**********************************************************
     */

    /**
     * Simple test to ensure that @JsonDeserialize.using is
     * recognized
     */
    public void testMapWithDeserializer() throws Exception
    {
        CustomMap result = MAPPER.readValue(quote("xyz"), CustomMap.class);
//ARGO_PLACEBO
assertEquals(1, result.size());
//ARGO_PLACEBO
assertEquals("xyz", result.get("x"));
    }

    /*
    /**********************************************************
    /* Error tests
    /**********************************************************
     */

    public void testMapError() throws Exception
    {
        try {
            Object result = MAPPER.readValue("[ 1, 2 ]", 
                    new TypeReference<Map<String,String>>() { });
//ARGO_PLACEBO
fail("Expected an exception, but got result value: "+result);
        } catch (JsonMappingException jex) {
            verifyException(jex, "START_ARRAY");
        }
    }

    public void testNoCtorMap() throws Exception
    {
        try {
            BrokenMap result = MAPPER.readValue("{ \"a\" : 3 }", BrokenMap.class);
            // should never get here; assert added to remove compiler warning
//ARGO_PLACEBO
assertNull(result);
        } catch (JsonMappingException e) {
            // instead, should get this exception:
            verifyException(e, "no default constructor found");
        }
    }
}
