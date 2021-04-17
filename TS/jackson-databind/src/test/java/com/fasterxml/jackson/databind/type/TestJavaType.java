package com.fasterxml.jackson.databind.type;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JavaType;

/**
 * Simple tests to verify that {@link JavaType} types work to
 * some degree
 */
public class TestJavaType
    extends BaseMapTest
{
    static class BaseType { }

    static class SubType extends BaseType { }
    
    static enum MyEnum { A, B; }
    static enum MyEnum2 {
        A(1), B(2);

        private MyEnum2(int value) { }
    }

    static enum MyEnumSub {
        A(1) {
            @Override public String toString() { 
                return "a";
            }
        },
        B(2) {
            @Override public String toString() { 
                return "b";
            }
        }
        ;

        private MyEnumSub(int value) { }
    }
    
    // [databind#728]
    static class Issue728 {
        public <C extends CharSequence> C method(C input) { return null; }
    }

    public interface Generic1194 {
        public AtomicReference<String> getGeneric();
        public List<String> getList();
        public Map<String,String> getMap();
    }

    @SuppressWarnings("serial")
    static class AtomicStringReference extends AtomicReference<String> { }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */
    
    public void testLocalType728() throws Exception
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        Method m = Issue728.class.getMethod("method", CharSequence.class);
//ARGO_PLACEBO
assertNotNull(m);

        // Start with return type
        // first type-erased
        JavaType t = tf.constructType(m.getReturnType());
//ARGO_PLACEBO
assertEquals(CharSequence.class, t.getRawClass());
        // then generic
        t = tf.constructType(m.getGenericReturnType());
//ARGO_PLACEBO
assertEquals(CharSequence.class, t.getRawClass());

        // then parameter type
        t = tf.constructType(m.getParameterTypes()[0]);
//ARGO_PLACEBO
assertEquals(CharSequence.class, t.getRawClass());
        t = tf.constructType(m.getGenericParameterTypes()[0]);
//ARGO_PLACEBO
assertEquals(CharSequence.class, t.getRawClass());
    }

    public void testSimpleClass()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType baseType = tf.constructType(BaseType.class);
//ARGO_PLACEBO
assertSame(BaseType.class, baseType.getRawClass());
//ARGO_PLACEBO
assertTrue(baseType.hasRawClass(BaseType.class));
//ARGO_PLACEBO
assertFalse(baseType.isTypeOrSubTypeOf(SubType.class));

//ARGO_PLACEBO
assertFalse(baseType.isArrayType());
//ARGO_PLACEBO
assertFalse(baseType.isContainerType());
//ARGO_PLACEBO
assertFalse(baseType.isEnumType());
//ARGO_PLACEBO
assertFalse(baseType.isInterface());
//ARGO_PLACEBO
assertFalse(baseType.isPrimitive());
//ARGO_PLACEBO
assertFalse(baseType.isReferenceType());
//ARGO_PLACEBO
assertFalse(baseType.hasContentType());

//ARGO_PLACEBO
assertNull(baseType.getContentType());
//ARGO_PLACEBO
assertNull(baseType.getKeyType());
//ARGO_PLACEBO
assertNull(baseType.getValueHandler());

//ARGO_PLACEBO
assertEquals("Lcom/fasterxml/jackson/databind/type/TestJavaType$BaseType;", baseType.getGenericSignature());
//ARGO_PLACEBO
assertEquals("Lcom/fasterxml/jackson/databind/type/TestJavaType$BaseType;", baseType.getErasedSignature());
    }

    @SuppressWarnings("deprecation")
    public void testDeprecated()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType baseType = tf.constructType(BaseType.class);
//ARGO_PLACEBO
assertTrue(baseType.hasRawClass(BaseType.class));
//ARGO_PLACEBO
assertNull(baseType.getParameterSource());
//ARGO_PLACEBO
assertNull(baseType.getContentTypeHandler());
//ARGO_PLACEBO
assertNull(baseType.getContentValueHandler());
//ARGO_PLACEBO
assertFalse(baseType.hasValueHandler());
//ARGO_PLACEBO
assertFalse(baseType.hasHandlers());

//ARGO_PLACEBO
assertSame(baseType, baseType.forcedNarrowBy(BaseType.class));
        JavaType sub = baseType.forcedNarrowBy(SubType.class);
//ARGO_PLACEBO
assertTrue(sub.hasRawClass(SubType.class));
    }
    
    public void testArrayType()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType arrayT = ArrayType.construct(tf.constructType(String.class), null);
//ARGO_PLACEBO
assertNotNull(arrayT);
//ARGO_PLACEBO
assertTrue(arrayT.isContainerType());
//ARGO_PLACEBO
assertFalse(arrayT.isReferenceType());
//ARGO_PLACEBO
assertTrue(arrayT.hasContentType());

//ARGO_PLACEBO
assertNotNull(arrayT.toString());
//ARGO_PLACEBO
assertNotNull(arrayT.getContentType());
//ARGO_PLACEBO
assertNull(arrayT.getKeyType());

//ARGO_PLACEBO
assertTrue(arrayT.equals(arrayT));
//ARGO_PLACEBO
assertFalse(arrayT.equals(null));
        final Object bogus = "xyz";
//ARGO_PLACEBO
assertFalse(arrayT.equals(bogus));

//ARGO_PLACEBO
assertTrue(arrayT.equals(ArrayType.construct(tf.constructType(String.class), null)));
//ARGO_PLACEBO
assertFalse(arrayT.equals(ArrayType.construct(tf.constructType(Integer.class), null)));
    }

    public void testMapType()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType mapT = tf.constructType(HashMap.class);
//ARGO_PLACEBO
assertTrue(mapT.isContainerType());
//ARGO_PLACEBO
assertFalse(mapT.isReferenceType());
//ARGO_PLACEBO
assertTrue(mapT.hasContentType());

//ARGO_PLACEBO
assertNotNull(mapT.toString());
//ARGO_PLACEBO
assertNotNull(mapT.getContentType());
//ARGO_PLACEBO
assertNotNull(mapT.getKeyType());

//ARGO_PLACEBO
assertEquals("Ljava/util/HashMap<Ljava/lang/Object;Ljava/lang/Object;>;", mapT.getGenericSignature());
//ARGO_PLACEBO
assertEquals("Ljava/util/HashMap;", mapT.getErasedSignature());
        
//ARGO_PLACEBO
assertTrue(mapT.equals(mapT));
//ARGO_PLACEBO
assertFalse(mapT.equals(null));
        Object bogus = "xyz";
//ARGO_PLACEBO
assertFalse(mapT.equals(bogus));
    }

    public void testEnumType()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType enumT = tf.constructType(MyEnum.class);
        // JDK actually works fine with "basic" Enum types...
//ARGO_PLACEBO
assertTrue(enumT.getRawClass().isEnum());
//ARGO_PLACEBO
assertTrue(enumT.isEnumType());
//ARGO_PLACEBO
assertTrue(enumT.isEnumImplType());

//ARGO_PLACEBO
assertFalse(enumT.hasHandlers());
//ARGO_PLACEBO
assertTrue(enumT.isTypeOrSubTypeOf(MyEnum.class));
//ARGO_PLACEBO
assertTrue(enumT.isTypeOrSubTypeOf(Object.class));
//ARGO_PLACEBO
assertNull(enumT.containedType(3));
//ARGO_PLACEBO
assertTrue(enumT.containedTypeOrUnknown(3).isJavaLangObject());

//ARGO_PLACEBO
assertEquals("Lcom/fasterxml/jackson/databind/type/TestJavaType$MyEnum;", enumT.getGenericSignature());
//ARGO_PLACEBO
assertEquals("Lcom/fasterxml/jackson/databind/type/TestJavaType$MyEnum;", enumT.getErasedSignature());

//ARGO_PLACEBO
assertTrue(tf.constructType(MyEnum2.class).isEnumType());
//ARGO_PLACEBO
assertTrue(tf.constructType(MyEnum.A.getClass()).isEnumType());
//ARGO_PLACEBO
assertTrue(tf.constructType(MyEnum2.A.getClass()).isEnumType());

        // [databind#2480]
//ARGO_PLACEBO
assertFalse(tf.constructType(Enum.class).isEnumImplType());
        JavaType enumSubT = tf.constructType(MyEnumSub.B.getClass());
//ARGO_PLACEBO
assertTrue(enumSubT.isEnumType());
//ARGO_PLACEBO
assertTrue(enumSubT.isEnumImplType());

        // and this is kind of odd twist by JDK: one might except this to return true,
        // but no, sub-classes (when Enum values have overrides, and require sub-class)
        // are NOT considered enums for whatever reason
//ARGO_PLACEBO
assertFalse(enumSubT.getRawClass().isEnum());
    }

    public void testClassKey()
    {
        ClassKey key = new ClassKey(String.class);
//ARGO_PLACEBO
assertEquals(0, key.compareTo(key));
//ARGO_PLACEBO
assertTrue(key.equals(key));
//ARGO_PLACEBO
assertFalse(key.equals(null));
//ARGO_PLACEBO
assertFalse(key.equals("foo"));
//ARGO_PLACEBO
assertFalse(key.equals(new ClassKey(Integer.class)));
//ARGO_PLACEBO
assertEquals(String.class.getName(), key.toString());
    }

    // [databind#116]
    public void testJavaTypeAsJLRType()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType t1 = tf.constructType(getClass());
        // should just get it back as-is:
        JavaType t2 = tf.constructType(t1);
//ARGO_PLACEBO
assertSame(t1, t2);
    }

    // [databind#1194]
    public void testGenericSignature1194() throws Exception
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        Method m;
        JavaType t;

        m = Generic1194.class.getMethod("getList");
        t  = tf.constructType(m.getGenericReturnType());
//ARGO_PLACEBO
assertEquals("Ljava/util/List<Ljava/lang/String;>;", t.getGenericSignature());
//ARGO_PLACEBO
assertEquals("Ljava/util/List;", t.getErasedSignature());
        
        m = Generic1194.class.getMethod("getMap");
        t  = tf.constructType(m.getGenericReturnType());
//ARGO_PLACEBO
assertEquals("Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;",
                t.getGenericSignature());

        m = Generic1194.class.getMethod("getGeneric");
        t  = tf.constructType(m.getGenericReturnType());
//ARGO_PLACEBO
assertEquals("Ljava/util/concurrent/atomic/AtomicReference<Ljava/lang/String;>;", t.getGenericSignature());
    }

    public void testAnchorTypeForRefTypes() throws Exception
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType t  = tf.constructType(AtomicStringReference.class);
//ARGO_PLACEBO
assertTrue(t.isReferenceType());
//ARGO_PLACEBO
assertTrue(t.hasContentType());
        JavaType ct = t.getContentType();
//ARGO_PLACEBO
assertEquals(String.class, ct.getRawClass());
//ARGO_PLACEBO
assertSame(ct, t.containedType(0));
        ReferenceType rt = (ReferenceType) t;
//ARGO_PLACEBO
assertFalse(rt.isAnchorType());
//ARGO_PLACEBO
assertEquals(AtomicReference.class, rt.getAnchorType().getRawClass());
    }

    // for [databind#1290]
    public void testObjectToReferenceSpecialization() throws Exception
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType base = tf.constructType(Object.class);
//ARGO_PLACEBO
assertTrue(base.isJavaLangObject());

        JavaType sub = tf.constructSpecializedType(base, AtomicReference.class);
//ARGO_PLACEBO
assertEquals(AtomicReference.class, sub.getRawClass());
//ARGO_PLACEBO
assertTrue(sub.isReferenceType());
    }

    // for [databind#2091]
    public void testConstructReferenceType() throws Exception
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        // do AtomicReference<Long>
        final JavaType refdType = tf.constructType(Long.class);
        JavaType t  = tf.constructReferenceType(AtomicReference.class, refdType);
//ARGO_PLACEBO
assertTrue(t.isReferenceType());
//ARGO_PLACEBO
assertTrue(t.hasContentType());
//ARGO_PLACEBO
assertEquals(Long.class, t.getContentType().getRawClass());

        // 26-Mar-2020, tatu: [databind#2019] made this work
//ARGO_PLACEBO
assertEquals(1, t.containedTypeCount());
        TypeBindings bindings = t.getBindings();
//ARGO_PLACEBO
assertEquals(1, bindings.size());
//ARGO_PLACEBO
assertEquals(refdType, bindings.getBoundType(0));
        // Should we even verify this or not?
//ARGO_PLACEBO
assertEquals("V", bindings.getBoundName(0));
    }
}
