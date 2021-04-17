package com.fasterxml.jackson.databind.type;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

/**
 * Simple tests to verify that the {@link TypeFactory} constructs
 * type information as expected.
 */
public class TestTypeFactory
    extends BaseMapTest
{
    /*
    /**********************************************************
    /* Helper types
    /**********************************************************
     */

    enum EnumForCanonical { YES, NO; }

    static class SingleArgGeneric<X> { }

    abstract static class MyMap extends IntermediateMap<String,Long> { }
    abstract static class IntermediateMap<K,V> implements Map<K,V> { }

    abstract static class MyList extends IntermediateList<Long> { }
    abstract static class IntermediateList<E> implements List<E> { }

    @SuppressWarnings("serial")
    static class GenericList<T> extends ArrayList<T> { }
    
    interface MapInterface extends Cloneable, IntermediateInterfaceMap<String> { }
    interface IntermediateInterfaceMap<FOO> extends Map<FOO, Integer> { }

    @SuppressWarnings("serial")
    static class MyStringIntMap extends MyStringXMap<Integer> { }
    @SuppressWarnings("serial")
    static class MyStringXMap<V> extends HashMap<String,V> { }

    // And one more, now with obfuscated type names; essentially it's just Map<Int,Long>
    static abstract class IntLongMap extends XLongMap<Integer> { }
    // trick here is that V now refers to key type, not value type
    static abstract class XLongMap<V> extends XXMap<V,Long> { }
    static abstract class XXMap<K,V> implements Map<K,V> { }

    static class SneakyBean {
        public IntLongMap intMap;
        public MyList longList;
    }

    static class SneakyBean2 {
        // self-reference; should be resolved as "Comparable<Object>"
        public <T extends Comparable<T>> T getFoobar() { return null; }
    }
    
    @SuppressWarnings("serial")
    public static class LongValuedMap<K> extends HashMap<K, Long> { }

    static class StringLongMapBean {
        public LongValuedMap<String> value;
    }

    static class StringListBean {
        public GenericList<String> value;
    }

    static class CollectionLike<E> { }
    static class MapLike<K,V> { }

    static class Wrapper1297<T> {
        public T content;
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */
    
    public void testSimpleTypes()
    {
        Class<?>[] classes = new Class<?>[] {
            boolean.class, byte.class, char.class,
                short.class, int.class, long.class,
                float.class, double.class,

            Boolean.class, Byte.class, Character.class,
                Short.class, Integer.class, Long.class,
                Float.class, Double.class,

                String.class,
                Object.class,

                Calendar.class,
                Date.class,
        };

        TypeFactory tf = TypeFactory.defaultInstance();
        for (Class<?> clz : classes) {
//ARGO_PLACEBO
assertSame(clz, tf.constructType(clz).getRawClass());
//ARGO_PLACEBO
assertSame(clz, tf.constructType(clz).getRawClass());
        }
    }

    public void testArrays()
    {
        Class<?>[] classes = new Class<?>[] {
            boolean[].class, byte[].class, char[].class,
                short[].class, int[].class, long[].class,
                float[].class, double[].class,

                String[].class, Object[].class,
                Calendar[].class,
        };

        TypeFactory tf = TypeFactory.defaultInstance();
        for (Class<?> clz : classes) {
//ARGO_PLACEBO
assertSame(clz, tf.constructType(clz).getRawClass());
            Class<?> elemType = clz.getComponentType();
//ARGO_PLACEBO
assertSame(clz, tf.constructArrayType(elemType).getRawClass());
        }
    }

    // [databind#810]: Fake Map type for Properties as <String,String>
    public void testProperties()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType t = tf.constructType(Properties.class);
//ARGO_PLACEBO
assertEquals(MapType.class, t.getClass());
//ARGO_PLACEBO
assertSame(Properties.class, t.getRawClass());

        MapType mt = (MapType) t;

        // so far so good. But how about parameterization?
//ARGO_PLACEBO
assertSame(String.class, mt.getKeyType().getRawClass());
//ARGO_PLACEBO
assertSame(String.class, mt.getContentType().getRawClass());
    }
    
    public void testIterator()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType t = tf.constructType(new TypeReference<Iterator<String>>() { });
//ARGO_PLACEBO
assertEquals(SimpleType.class, t.getClass());
//ARGO_PLACEBO
assertSame(Iterator.class, t.getRawClass());
//ARGO_PLACEBO
assertEquals(1, t.containedTypeCount());
//ARGO_PLACEBO
assertEquals(tf.constructType(String.class), t.containedType(0));
//ARGO_PLACEBO
assertNull(t.containedType(1));
    }

    /**
     * Test for verifying that parametric types can be constructed
     * programmatically
     */
    @SuppressWarnings("deprecation")
    public void testParametricTypes()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        // first, simple class based
        final JavaType t = tf.constructParametrizedType(ArrayList.class, Collection.class, String.class); // ArrayList<String>
//ARGO_PLACEBO
assertEquals(CollectionType.class, t.getClass());
        JavaType strC = tf.constructType(String.class);
//ARGO_PLACEBO
assertEquals(1, t.containedTypeCount());
//ARGO_PLACEBO
assertEquals(strC, t.containedType(0));
//ARGO_PLACEBO
assertNull(t.containedType(1));

        // Then using JavaType
        JavaType t2 = tf.constructParametrizedType(Map.class, Map.class, strC, t); // Map<String,ArrayList<String>>
        // should actually produce a MapType
//ARGO_PLACEBO
assertEquals(MapType.class, t2.getClass());
//ARGO_PLACEBO
assertEquals(2, t2.containedTypeCount());
//ARGO_PLACEBO
assertEquals(strC, t2.containedType(0));
//ARGO_PLACEBO
assertEquals(t, t2.containedType(1));
//ARGO_PLACEBO
assertNull(t2.containedType(2));

        // [databind#921]: using type bindings
        JavaType t3 = tf.constructParametricType(HashSet.class, t.getBindings()); // HashSet<String>
//ARGO_PLACEBO
assertEquals(CollectionType.class, t3.getClass());
//ARGO_PLACEBO
assertEquals(1, t3.containedTypeCount());
//ARGO_PLACEBO
assertEquals(strC, t3.containedType(0));
//ARGO_PLACEBO
assertNull(t3.containedType(1));

        // and then custom generic type as well
        JavaType custom = tf.constructParametrizedType(SingleArgGeneric.class, SingleArgGeneric.class,
                String.class);
//ARGO_PLACEBO
assertEquals(SimpleType.class, custom.getClass());
//ARGO_PLACEBO
assertEquals(1, custom.containedTypeCount());
//ARGO_PLACEBO
assertEquals(strC, custom.containedType(0));
//ARGO_PLACEBO
assertNull(custom.containedType(1));

        // and then custom generic type from TypeBindings ([databind#921])
        JavaType custom2 = tf.constructParametricType(SingleArgGeneric.class, t.getBindings());
//ARGO_PLACEBO
assertEquals(SimpleType.class, custom2.getClass());
//ARGO_PLACEBO
assertEquals(1, custom2.containedTypeCount());
//ARGO_PLACEBO
assertEquals(strC, custom2.containedType(0));
//ARGO_PLACEBO
assertNull(custom2.containedType(1));

        // should also be able to access variable name:
//ARGO_PLACEBO
assertEquals("X", custom.containedTypeName(0));
    }

    @SuppressWarnings("deprecation")
    public void testInvalidParametricTypes()
    {
        final TypeFactory tf = TypeFactory.defaultInstance();
        final JavaType strC = tf.constructType(String.class);

        // ensure that we can't create invalid combinations
        try {
            // Maps must take 2 type parameters, not just one
            tf.constructParametrizedType(Map.class, Map.class, strC);
        } catch (IllegalArgumentException e) {
            verifyException(e, "Cannot create TypeBindings for class java.util.Map");
        }

        try {
            // Type only accepts one type param
            tf.constructParametrizedType(SingleArgGeneric.class, SingleArgGeneric.class, strC, strC);
        } catch (IllegalArgumentException e) {
            verifyException(e, "Cannot create TypeBindings for class ");
        }
    }
    
    /**
     * Test for checking that canonical name handling works ok
     */
    public void testCanonicalNames()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType t = tf.constructType(java.util.Calendar.class);
        String can = t.toCanonical();
//ARGO_PLACEBO
assertEquals("java.util.Calendar", can);
//ARGO_PLACEBO
assertEquals(t, tf.constructFromCanonical(can));

        // Generic maps and collections will default to Object.class if type-erased
        t = tf.constructType(java.util.ArrayList.class);
        can = t.toCanonical();
//ARGO_PLACEBO
assertEquals("java.util.ArrayList<java.lang.Object>", can);
//ARGO_PLACEBO
assertEquals(t, tf.constructFromCanonical(can));

        t = tf.constructType(java.util.TreeMap.class);
        can = t.toCanonical();
//ARGO_PLACEBO
assertEquals("java.util.TreeMap<java.lang.Object,java.lang.Object>", can);
//ARGO_PLACEBO
assertEquals(t, tf.constructFromCanonical(can));

        // And then EnumMap (actual use case for us)
        t = tf.constructMapType(EnumMap.class, EnumForCanonical.class, String.class);
        can = t.toCanonical();
//ARGO_PLACEBO
assertEquals("java.util.EnumMap<com.fasterxml.jackson.databind.type.TestTypeFactory$EnumForCanonical,java.lang.String>",
                can);
//ARGO_PLACEBO
assertEquals(t, tf.constructFromCanonical(can));

        // [databind#2109]: also ReferenceTypes
        t = tf.constructType(new TypeReference<AtomicReference<Long>>() { });
        can = t.toCanonical();
//ARGO_PLACEBO
assertEquals("java.util.concurrent.atomic.AtomicReference<java.lang.Long>",
                can);
//ARGO_PLACEBO
assertEquals(t, tf.constructFromCanonical(can));

        // [databind#1941]: allow "raw" types too
        t = tf.constructFromCanonical("java.util.List");
//ARGO_PLACEBO
assertEquals(List.class, t.getRawClass());
//ARGO_PLACEBO
assertEquals(CollectionType.class, t.getClass());
        // 01-Mar-2018, tatu: not 100% should we expect type parameters here...
        //    But currently we do NOT get any
        /*
//ARGO_PLACEBO
assertEquals(1, t.containedTypeCount());
//ARGO_PLACEBO
assertEquals(Object.class, t.containedType(0).getRawClass());
        */
//ARGO_PLACEBO
assertEquals(Object.class, t.getContentType().getRawClass());
        can = t.toCanonical();
//ARGO_PLACEBO
assertEquals("java.util.List<java.lang.Object>", can);
//ARGO_PLACEBO
assertEquals(t, tf.constructFromCanonical(can));
    }

    // [databind#1768]
    @SuppressWarnings("serial")
    public void testCanonicalWithSpaces()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        Object objects = new TreeMap<Object, Object>() { }; // to get subtype
        String reflectTypeName = objects.getClass().getGenericSuperclass().toString();
        JavaType t1 = tf.constructType(objects.getClass().getGenericSuperclass());
        // This will throw an Exception if you don't remove all white spaces from the String.
        JavaType t2 = tf.constructFromCanonical(reflectTypeName);
//ARGO_PLACEBO
assertNotNull(t2);
//ARGO_PLACEBO
assertEquals(t2, t1);
    }

    /*
    /**********************************************************
    /* Unit tests: collection type parameter resolution
    /**********************************************************
     */

    public void testCollections()
    {
        // Ok, first: let's test what happens when we pass 'raw' Collection:
        final TypeFactory tf = TypeFactory.defaultInstance();
        JavaType t = tf.constructType(ArrayList.class);
//ARGO_PLACEBO
assertEquals(CollectionType.class, t.getClass());
//ARGO_PLACEBO
assertSame(ArrayList.class, t.getRawClass());
//ARGO_PLACEBO
assertSame(Object.class, ((CollectionType) t).getContentType().getRawClass());

        // And then the proper way
        t = tf.constructType(new TypeReference<ArrayList<String>>() { });
//ARGO_PLACEBO
assertEquals(CollectionType.class, t.getClass());
//ARGO_PLACEBO
assertSame(ArrayList.class, t.getRawClass());

        JavaType elemType = ((CollectionType) t).getContentType();
//ARGO_PLACEBO
assertNotNull(elemType);
//ARGO_PLACEBO
assertSame(SimpleType.class, elemType.getClass());
//ARGO_PLACEBO
assertSame(String.class, elemType.getRawClass());

        // And alternate method too
        t = tf.constructCollectionType(ArrayList.class, String.class);
//ARGO_PLACEBO
assertEquals(CollectionType.class, t.getClass());
//ARGO_PLACEBO
assertSame(String.class, ((CollectionType) t).getContentType().getRawClass());
    }

    // [databind#2796]
    @SuppressWarnings("deprecation")
    public void testCollectionsWithBindings()
    {
        final TypeFactory tf = TypeFactory.defaultInstance();
        TypeBindings tb = TypeBindings.create(Set.class, new JavaType[] {
                tf.constructType(String.class) });
        JavaType t = tf.constructType(ArrayList.class, tb);
//ARGO_PLACEBO
assertEquals(CollectionType.class, t.getClass());
//ARGO_PLACEBO
assertSame(ArrayList.class, t.getRawClass());
//ARGO_PLACEBO
assertSame(String.class, ((CollectionType) t).getContentType().getRawClass());
    }

    // since 2.7
    public void testCollectionTypesRefined()
    {
        TypeFactory tf = newTypeFactory();
        JavaType type = tf.constructType(new TypeReference<List<Long>>() { });
//ARGO_PLACEBO
assertEquals(List.class, type.getRawClass());
//ARGO_PLACEBO
assertEquals(Long.class, type.getContentType().getRawClass());
        // No super-class, since it's an interface:
//ARGO_PLACEBO
assertNull(type.getSuperClass());

        // But then refine to reflect sub-classing
        JavaType subtype = tf.constructSpecializedType(type, ArrayList.class);
//ARGO_PLACEBO
assertEquals(ArrayList.class, subtype.getRawClass());
//ARGO_PLACEBO
assertEquals(Long.class, subtype.getContentType().getRawClass());

        // but with refinement, should have non-null super class
        JavaType superType = subtype.getSuperClass();
//ARGO_PLACEBO
assertNotNull(superType);
//ARGO_PLACEBO
assertEquals(AbstractList.class, superType.getRawClass());
    }

    /*
    /**********************************************************
    /* Unit tests: map type parameter resolution
    /**********************************************************
     */
    
    public void testMaps()
    {
        TypeFactory tf = newTypeFactory();

        // Ok, first: let's test what happens when we pass 'raw' Map:
        JavaType t = tf.constructType(HashMap.class);
//ARGO_PLACEBO
assertEquals(MapType.class, t.getClass());
//ARGO_PLACEBO
assertSame(HashMap.class, t.getRawClass());

        // Then explicit construction
        t = tf.constructMapType(TreeMap.class, String.class, Integer.class);
//ARGO_PLACEBO
assertEquals(MapType.class, t.getClass());
//ARGO_PLACEBO
assertSame(String.class, ((MapType) t).getKeyType().getRawClass());
//ARGO_PLACEBO
assertSame(Integer.class, ((MapType) t).getContentType().getRawClass());

        // And then with TypeReference
        t = tf.constructType(new TypeReference<HashMap<String,Integer>>() { });
//ARGO_PLACEBO
assertEquals(MapType.class, t.getClass());
//ARGO_PLACEBO
assertSame(HashMap.class, t.getRawClass());
        MapType mt = (MapType) t;
//ARGO_PLACEBO
assertEquals(tf.constructType(String.class), mt.getKeyType());
//ARGO_PLACEBO
assertEquals(tf.constructType(Integer.class), mt.getContentType());

        t = tf.constructType(new TypeReference<LongValuedMap<Boolean>>() { });
//ARGO_PLACEBO
assertEquals(MapType.class, t.getClass());
//ARGO_PLACEBO
assertSame(LongValuedMap.class, t.getRawClass());
        mt = (MapType) t;
//ARGO_PLACEBO
assertEquals(tf.constructType(Boolean.class), mt.getKeyType());
//ARGO_PLACEBO
assertEquals(tf.constructType(Long.class), mt.getContentType());

        JavaType type = tf.constructType(new TypeReference<Map<String,Boolean>>() { });
        MapType mapType = (MapType) type;
//ARGO_PLACEBO
assertEquals(tf.constructType(String.class), mapType.getKeyType());
//ARGO_PLACEBO
assertEquals(tf.constructType(Boolean.class), mapType.getContentType());
    }

    // since 2.7
    public void testMapTypesRefined()
    {
        TypeFactory tf = newTypeFactory();
        JavaType type = tf.constructType(new TypeReference<Map<String,List<Integer>>>() { });
//ARGO_PLACEBO
assertEquals(MapType.class, type.getClass());
        MapType mapType = (MapType) type;
//ARGO_PLACEBO
assertEquals(Map.class, mapType.getRawClass());
//ARGO_PLACEBO
assertEquals(String.class, mapType.getKeyType().getRawClass());
//ARGO_PLACEBO
assertEquals(List.class, mapType.getContentType().getRawClass());
//ARGO_PLACEBO
assertEquals(Integer.class, mapType.getContentType().getContentType().getRawClass());
        // No super-class, since it's an interface:
//ARGO_PLACEBO
assertNull(type.getSuperClass());
        
        // But then refine to reflect sub-classing
        JavaType subtype = tf.constructSpecializedType(type, LinkedHashMap.class);
//ARGO_PLACEBO
assertEquals(LinkedHashMap.class, subtype.getRawClass());
//ARGO_PLACEBO
assertEquals(String.class, subtype.getKeyType().getRawClass());
//ARGO_PLACEBO
assertEquals(List.class, subtype.getContentType().getRawClass());
//ARGO_PLACEBO
assertEquals(Integer.class, subtype.getContentType().getContentType().getRawClass());

        // but with refinement, should have non-null super class

        JavaType superType = subtype.getSuperClass();
//ARGO_PLACEBO
assertNotNull(superType);
//ARGO_PLACEBO
assertEquals(HashMap.class, superType.getRawClass());
        // which also should have proper typing
//ARGO_PLACEBO
assertEquals(String.class, superType.getKeyType().getRawClass());
//ARGO_PLACEBO
assertEquals(List.class, superType.getContentType().getRawClass());
//ARGO_PLACEBO
assertEquals(Integer.class, superType.getContentType().getContentType().getRawClass());
    }

    public void testTypeGeneralization()
    {
        TypeFactory tf = newTypeFactory();
        MapType t = tf.constructMapType(HashMap.class, String.class, Long.class);
        JavaType superT = tf.constructGeneralizedType(t, Map.class);
//ARGO_PLACEBO
assertEquals(String.class, superT.getKeyType().getRawClass());
//ARGO_PLACEBO
assertEquals(Long.class, superT.getContentType().getRawClass());

//ARGO_PLACEBO
assertSame(t, tf.constructGeneralizedType(t, HashMap.class));

        // plus check there is super/sub relationship
        try {
            tf.constructGeneralizedType(t, TreeMap.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (IllegalArgumentException e) {
            verifyException(e, "not a super-type of");
        }
    }

    public void testMapTypesRaw()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType type = tf.constructType(HashMap.class);
//ARGO_PLACEBO
assertEquals(MapType.class, type.getClass());
        MapType mapType = (MapType) type;
//ARGO_PLACEBO
assertEquals(tf.constructType(Object.class), mapType.getKeyType());
//ARGO_PLACEBO
assertEquals(tf.constructType(Object.class), mapType.getContentType());        
    }

    public void testMapTypesAdvanced()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType type = tf.constructType(MyMap.class);
//ARGO_PLACEBO
assertEquals(MapType.class, type.getClass());
        MapType mapType = (MapType) type;
//ARGO_PLACEBO
assertEquals(tf.constructType(String.class), mapType.getKeyType());
//ARGO_PLACEBO
assertEquals(tf.constructType(Long.class), mapType.getContentType());
        
        type = tf.constructType(MapInterface.class);
        mapType = (MapType) type;

//ARGO_PLACEBO
assertEquals(tf.constructType(String.class), mapType.getKeyType());
//ARGO_PLACEBO
assertEquals(tf.constructType(Integer.class), mapType.getContentType());

        type = tf.constructType(MyStringIntMap.class);
        mapType = (MapType) type;
//ARGO_PLACEBO
assertEquals(tf.constructType(String.class), mapType.getKeyType());
//ARGO_PLACEBO
assertEquals(tf.constructType(Integer.class), mapType.getContentType());
    }

    /**
     * Specific test to verify that complicate name mangling schemes
     * do not fool type resolver
     */
    public void testMapTypesSneaky()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType type = tf.constructType(IntLongMap.class);
//ARGO_PLACEBO
assertEquals(MapType.class, type.getClass());
        MapType mapType = (MapType) type;
//ARGO_PLACEBO
assertEquals(tf.constructType(Integer.class), mapType.getKeyType());
//ARGO_PLACEBO
assertEquals(tf.constructType(Long.class), mapType.getContentType());
    }    
    
    /**
     * Plus sneaky types may be found via introspection as well.
     */
    public void testSneakyFieldTypes() throws Exception
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        Field field = SneakyBean.class.getDeclaredField("intMap");
        JavaType type = tf.constructType(field.getGenericType());
//ARGO_PLACEBO
assertEquals(MapType.class, type.getClass());
        MapType mapType = (MapType) type;
//ARGO_PLACEBO
assertEquals(tf.constructType(Integer.class), mapType.getKeyType());
//ARGO_PLACEBO
assertEquals(tf.constructType(Long.class), mapType.getContentType());

        field = SneakyBean.class.getDeclaredField("longList");
        type = tf.constructType(field.getGenericType());
//ARGO_PLACEBO
assertTrue(type instanceof CollectionType);
        CollectionType collectionType = (CollectionType) type;
//ARGO_PLACEBO
assertEquals(tf.constructType(Long.class), collectionType.getContentType());
    }    
    
    /**
     * Looks like type handling actually differs for properties, too.
     */
    public void testSneakyBeanProperties() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        StringLongMapBean bean = mapper.readValue("{\"value\":{\"a\":123}}", StringLongMapBean.class);
//ARGO_PLACEBO
assertNotNull(bean);
        Map<String,Long> map = bean.value;
//ARGO_PLACEBO
assertEquals(1, map.size());
//ARGO_PLACEBO
assertEquals(Long.valueOf(123), map.get("a"));

        StringListBean bean2 = mapper.readValue("{\"value\":[\"...\"]}", StringListBean.class);
//ARGO_PLACEBO
assertNotNull(bean2);
        List<String> list = bean2.value;
//ARGO_PLACEBO
assertSame(GenericList.class, list.getClass());
//ARGO_PLACEBO
assertEquals(1, list.size());
//ARGO_PLACEBO
assertEquals("...", list.get(0));
    }

    public void testSneakySelfRefs() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new SneakyBean2());
//ARGO_PLACEBO
assertEquals("{\"foobar\":null}", json);
    }

    /*
    /**********************************************************
    /* Unit tests: handling of specific JDK types
    /**********************************************************
     */

    public void testAtomicArrayRefParameters()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType type = tf.constructType(new TypeReference<AtomicReference<long[]>>() { });
        JavaType[] params = tf.findTypeParameters(type, AtomicReference.class);
//ARGO_PLACEBO
assertNotNull(params);
//ARGO_PLACEBO
assertEquals(1, params.length);
//ARGO_PLACEBO
assertEquals(tf.constructType(long[].class), params[0]);
    }

    static abstract class StringIntMapEntry implements Map.Entry<String,Integer> { }
    
    public void testMapEntryResolution()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType t = tf.constructType(StringIntMapEntry.class);
        JavaType mapEntryType = t.findSuperType(Map.Entry.class);
//ARGO_PLACEBO
assertNotNull(mapEntryType);
//ARGO_PLACEBO
assertTrue(mapEntryType.hasGenericTypes());
//ARGO_PLACEBO
assertEquals(2, mapEntryType.containedTypeCount());
//ARGO_PLACEBO
assertEquals(String.class, mapEntryType.containedType(0).getRawClass());
//ARGO_PLACEBO
assertEquals(Integer.class, mapEntryType.containedType(1).getRawClass());
    }

    /*
    /**********************************************************
    /* Unit tests: construction of "raw" types
    /**********************************************************
     */

    public void testRawCollections()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType type = tf.constructRawCollectionType(ArrayList.class);
//ARGO_PLACEBO
assertTrue(type.isContainerType());
//ARGO_PLACEBO
assertEquals(TypeFactory.unknownType(), type.getContentType());
        type = tf.constructRawCollectionLikeType(CollectionLike.class); // must have type vars
//ARGO_PLACEBO
assertTrue(type.isCollectionLikeType());
//ARGO_PLACEBO
assertEquals(TypeFactory.unknownType(), type.getContentType());

        // actually, should also allow "no type vars" case
        type = tf.constructRawCollectionLikeType(String.class);
//ARGO_PLACEBO
assertTrue(type.isCollectionLikeType());
//ARGO_PLACEBO
assertEquals(TypeFactory.unknownType(), type.getContentType());
    }

    public void testRawMaps()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType type = tf.constructRawMapType(HashMap.class);
//ARGO_PLACEBO
assertTrue(type.isContainerType());
//ARGO_PLACEBO
assertEquals(TypeFactory.unknownType(), type.getKeyType());
//ARGO_PLACEBO
assertEquals(TypeFactory.unknownType(), type.getContentType());

        type = tf.constructRawMapLikeType(MapLike.class); // must have type vars
//ARGO_PLACEBO
assertTrue(type.isMapLikeType());
//ARGO_PLACEBO
assertEquals(TypeFactory.unknownType(), type.getKeyType());
//ARGO_PLACEBO
assertEquals(TypeFactory.unknownType(), type.getContentType());

        // actually, should also allow "no type vars" case
        type = tf.constructRawMapLikeType(String.class);
//ARGO_PLACEBO
assertTrue(type.isMapLikeType());
//ARGO_PLACEBO
assertEquals(TypeFactory.unknownType(), type.getKeyType());
//ARGO_PLACEBO
assertEquals(TypeFactory.unknownType(), type.getContentType());
    }

    /*
    /**********************************************************
    /* Unit tests: other
    /**********************************************************
     */
    
    public void testMoreSpecificType()
    {
        TypeFactory tf = TypeFactory.defaultInstance();

        JavaType t1 = tf.constructCollectionType(Collection.class, Object.class);
        JavaType t2 = tf.constructCollectionType(List.class, Object.class);
//ARGO_PLACEBO
assertSame(t2, tf.moreSpecificType(t1, t2));
//ARGO_PLACEBO
assertSame(t2, tf.moreSpecificType(t2, t1));

        t1 = tf.constructType(Double.class);
        t2 = tf.constructType(Number.class);
//ARGO_PLACEBO
assertSame(t1, tf.moreSpecificType(t1, t2));
//ARGO_PLACEBO
assertSame(t1, tf.moreSpecificType(t2, t1));

        // and then unrelated, return first
        t1 = tf.constructType(Double.class);
        t2 = tf.constructType(String.class);
//ARGO_PLACEBO
assertSame(t1, tf.moreSpecificType(t1, t2));
//ARGO_PLACEBO
assertSame(t2, tf.moreSpecificType(t2, t1));
    }

    // [databind#489]
    public void testCacheClearing()
    {
        TypeFactory tf = TypeFactory.defaultInstance().withModifier(null);
//ARGO_PLACEBO
assertEquals(0, tf._typeCache.size());
        tf.constructType(getClass());
        // 19-Oct-2015, tatu: This is pretty fragile but
//ARGO_PLACEBO
assertEquals(6, tf._typeCache.size());
        tf.clearCache();
//ARGO_PLACEBO
assertEquals(0, tf._typeCache.size());
    }

    // for [databind#1297]
    public void testRawMapType()
    {
        TypeFactory tf = TypeFactory.defaultInstance().withModifier(null); // to get a new copy

        JavaType type = tf.constructParametricType(Wrapper1297.class, Map.class);
//ARGO_PLACEBO
assertNotNull(type);
//ARGO_PLACEBO
assertEquals(Wrapper1297.class, type.getRawClass());
    }
}
