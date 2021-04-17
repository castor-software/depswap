package com.fasterxml.jackson.databind.util;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class ClassUtilTest extends BaseMapTest
{
    /*
    /**********************************************************
    /* Test classes, enums
    /**********************************************************
     */

    /* Test classes and interfaces needed for testing class util
     * methods
     */
    static abstract class BaseClass implements Comparable<BaseClass>,
        BaseInt
    {
        BaseClass(String str) { }
    }

    interface BaseInt { }

    interface SubInt extends BaseInt { }

    enum TestEnum { A; }

    abstract class InnerNonStatic { }

    static class Inner {
        protected Inner() {
            throw new IllegalStateException("test");
        }
    }

    static abstract class SubClass
        extends BaseClass
        implements SubInt {
        SubClass() { super("x"); }
    }

    static abstract class ConcreteAndAbstract {
        public abstract void a();
        
        public void c() { }
    }

    static class MaybeGetters {
        public static void staticMethod() { }

        public void voidMethod() { }

        public int getMethod() { return 1; }
        public void setMethod(int x) { }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testIsConcrete() throws Exception
    {
//ARGO_PLACEBO
assertTrue(ClassUtil.isConcrete(getClass()));
//ARGO_PLACEBO
assertFalse(ClassUtil.isConcrete(BaseClass.class));
//ARGO_PLACEBO
assertFalse(ClassUtil.isConcrete(BaseInt.class));

//ARGO_PLACEBO
assertFalse(ClassUtil.isConcrete(ConcreteAndAbstract.class.getDeclaredMethod("a")));
//ARGO_PLACEBO
assertTrue(ClassUtil.isConcrete(ConcreteAndAbstract.class.getDeclaredMethod("c")));
    }

    public void testCanBeABeanType()
    {
//ARGO_PLACEBO
assertEquals("annotation", ClassUtil.canBeABeanType(java.lang.annotation.Retention.class));
//ARGO_PLACEBO
assertEquals("array", ClassUtil.canBeABeanType(String[].class));
//ARGO_PLACEBO
assertEquals("enum", ClassUtil.canBeABeanType(TestEnum.class));
//ARGO_PLACEBO
assertEquals("primitive", ClassUtil.canBeABeanType(Integer.TYPE));
//ARGO_PLACEBO
assertNull(ClassUtil.canBeABeanType(Integer.class));

//ARGO_PLACEBO
assertEquals("non-static member class", ClassUtil.isLocalType(InnerNonStatic.class, false));
//ARGO_PLACEBO
assertNull(ClassUtil.isLocalType(Integer.class, false));
    }

    public void testExceptionHelpers()
    {
        RuntimeException e = new RuntimeException("test");
        RuntimeException wrapper = new RuntimeException(e);

//ARGO_PLACEBO
assertSame(e, ClassUtil.getRootCause(wrapper));

        try {
            ClassUtil.throwAsIAE(e);
//ARGO_PLACEBO
fail("Shouldn't get this far");
        } catch (RuntimeException e2) {
//ARGO_PLACEBO
assertSame(e, e2);
        }

        Error err = new Error();
        try {
            ClassUtil.throwAsIAE(err);
//ARGO_PLACEBO
fail("Shouldn't get this far");
        } catch (Error errAct) {
//ARGO_PLACEBO
assertSame(err, errAct);
        }
        
        try {
            ClassUtil.unwrapAndThrowAsIAE(wrapper);
//ARGO_PLACEBO
fail("Shouldn't get this far");
        } catch (RuntimeException e2) {
//ARGO_PLACEBO
assertSame(e, e2);
        }
    }

    public void testFailedCreateInstance()
    {
        try {
            ClassUtil.createInstance(BaseClass.class, true);
        } catch (IllegalArgumentException e) {
            verifyException(e, "has no default");
        }

        try {
            // false means ctor would need to be public
            ClassUtil.createInstance(Inner.class, false);
        } catch (IllegalArgumentException e) {
            verifyException(e, "is not accessible");
        }

        // and finally, check that we'll get expected exception...
        try {
            ClassUtil.createInstance(Inner.class, true);
        } catch (IllegalStateException e) {
            verifyException(e, "test");
        }
    }

    public void testPrimitiveDefaultValue()
    {
//ARGO_PLACEBO
assertEquals(Integer.valueOf(0), ClassUtil.defaultValue(Integer.TYPE));
//ARGO_PLACEBO
assertEquals(Long.valueOf(0L), ClassUtil.defaultValue(Long.TYPE));
//ARGO_PLACEBO
assertEquals(Character.valueOf('\0'), ClassUtil.defaultValue(Character.TYPE));
//ARGO_PLACEBO
assertEquals(Short.valueOf((short) 0), ClassUtil.defaultValue(Short.TYPE));
//ARGO_PLACEBO
assertEquals(Byte.valueOf((byte) 0), ClassUtil.defaultValue(Byte.TYPE));

//ARGO_PLACEBO
assertEquals(Double.valueOf(0.0), ClassUtil.defaultValue(Double.TYPE));
//ARGO_PLACEBO
assertEquals(Float.valueOf(0.0f), ClassUtil.defaultValue(Float.TYPE));

//ARGO_PLACEBO
assertEquals(Boolean.FALSE, ClassUtil.defaultValue(Boolean.TYPE));
        
        try {
            ClassUtil.defaultValue(String.class);
        } catch (IllegalArgumentException e) {
            verifyException(e, "String is not a primitive type");
        }
    }

    public void testPrimitiveWrapperType()
    {
//ARGO_PLACEBO
assertEquals(Byte.class, ClassUtil.wrapperType(Byte.TYPE));
//ARGO_PLACEBO
assertEquals(Short.class, ClassUtil.wrapperType(Short.TYPE));
//ARGO_PLACEBO
assertEquals(Character.class, ClassUtil.wrapperType(Character.TYPE));
//ARGO_PLACEBO
assertEquals(Integer.class, ClassUtil.wrapperType(Integer.TYPE));
//ARGO_PLACEBO
assertEquals(Long.class, ClassUtil.wrapperType(Long.TYPE));

//ARGO_PLACEBO
assertEquals(Double.class, ClassUtil.wrapperType(Double.TYPE));
//ARGO_PLACEBO
assertEquals(Float.class, ClassUtil.wrapperType(Float.TYPE));

//ARGO_PLACEBO
assertEquals(Boolean.class, ClassUtil.wrapperType(Boolean.TYPE));
        
        try {
            ClassUtil.wrapperType(String.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (IllegalArgumentException e) {
            verifyException(e, "String is not a primitive type");
        }
    }

    public void testWrapperToPrimitiveType()
    {
//ARGO_PLACEBO
assertEquals(Integer.TYPE, ClassUtil.primitiveType(Integer.class));
//ARGO_PLACEBO
assertEquals(Long.TYPE, ClassUtil.primitiveType(Long.class));
//ARGO_PLACEBO
assertEquals(Character.TYPE, ClassUtil.primitiveType(Character.class));
//ARGO_PLACEBO
assertEquals(Short.TYPE, ClassUtil.primitiveType(Short.class));
//ARGO_PLACEBO
assertEquals(Byte.TYPE, ClassUtil.primitiveType(Byte.class));
//ARGO_PLACEBO
assertEquals(Float.TYPE, ClassUtil.primitiveType(Float.class));
//ARGO_PLACEBO
assertEquals(Double.TYPE, ClassUtil.primitiveType(Double.class));
//ARGO_PLACEBO
assertEquals(Boolean.TYPE, ClassUtil.primitiveType(Boolean.class));
        
//ARGO_PLACEBO
assertNull(ClassUtil.primitiveType(String.class));
    }

    public void testFindEnumType()
    {
//ARGO_PLACEBO
assertEquals(TestEnum.class, ClassUtil.findEnumType(TestEnum.A));
        // different codepaths for empty and non-empty EnumSets...
//ARGO_PLACEBO
assertEquals(TestEnum.class, ClassUtil.findEnumType(EnumSet.allOf(TestEnum.class)));
//ARGO_PLACEBO
assertEquals(TestEnum.class, ClassUtil.findEnumType(EnumSet.noneOf(TestEnum.class)));

//ARGO_PLACEBO
assertEquals(TestEnum.class, ClassUtil.findEnumType(new EnumMap<TestEnum,Integer>(TestEnum.class)));
    }

    public void testDescs()
    {
        final String stringExp = "`java.lang.String`";
//ARGO_PLACEBO
assertEquals(stringExp, ClassUtil.getClassDescription("foo"));
//ARGO_PLACEBO
assertEquals(stringExp, ClassUtil.getClassDescription(String.class));
        final JavaType stringType = TypeFactory.defaultInstance().constructType(String.class);
//ARGO_PLACEBO
assertEquals(stringExp, ClassUtil.getTypeDescription(stringType));
        final JavaType mapType = TypeFactory.defaultInstance().constructType(
                new TypeReference<Map<String, Integer>>() { });
//ARGO_PLACEBO
assertEquals("`java.util.Map<java.lang.String,java.lang.Integer>`",
                ClassUtil.getTypeDescription(mapType));
    }

    public void testSubtypes()
    {
        final JavaType stringType = TypeFactory.defaultInstance().constructType(String.class);
        List<JavaType> supers = ClassUtil.findSuperTypes(stringType, Object.class, false);
//ARGO_PLACEBO
assertEquals(Collections.emptyList(), supers);

        supers = ClassUtil.findSuperTypes(stringType, Object.class, true);
//ARGO_PLACEBO
assertEquals(Collections.singletonList(stringType), supers);
    }

    public void testGetDeclaringClass()
    {
//ARGO_PLACEBO
assertEquals(null, ClassUtil.getDeclaringClass(String.class));
//ARGO_PLACEBO
assertEquals(getClass(), ClassUtil.getDeclaringClass(BaseClass.class));
    }

    public void testIsXxxType()
    {
//ARGO_PLACEBO
assertTrue(ClassUtil.isCollectionMapOrArray(String[].class));
//ARGO_PLACEBO
assertTrue(ClassUtil.isCollectionMapOrArray(ArrayList.class));
//ARGO_PLACEBO
assertTrue(ClassUtil.isCollectionMapOrArray(LinkedHashMap.class));
//ARGO_PLACEBO
assertFalse(ClassUtil.isCollectionMapOrArray(java.net.URL.class));

//ARGO_PLACEBO
assertTrue(ClassUtil.isBogusClass(Void.class));
//ARGO_PLACEBO
assertTrue(ClassUtil.isBogusClass(Void.TYPE));
//ARGO_PLACEBO
assertFalse(ClassUtil.isBogusClass(String.class));
    }

    public void testEnforceSubtype()
    {
        try {
            ClassUtil.verifyMustOverride(Number.class, Boolean.TRUE, "Test");
        } catch (IllegalStateException e) {
            verifyException(e, "must override method 'Test'");
        }
    }

    public void testCloseEtc() throws Exception
    {
        final Exception testExc1 = new IllegalArgumentException("test");
        // First: without any actual stuff, with an RTE
        try {
            ClassUtil.closeOnFailAndThrowAsIOE(null, null, testExc1);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (Exception e) {
//ARGO_PLACEBO
assertSame(testExc1, e);
        }

        // then with bogus Closeable and with non-RTE:
        JsonFactory f = new JsonFactory();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        JsonGenerator gen = f.createGenerator(bytes);
        final Exception testExc2 = new Exception("test");
        try {
            ClassUtil.closeOnFailAndThrowAsIOE(gen, bytes, testExc2);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (Exception e) {
//ARGO_PLACEBO
assertEquals(RuntimeException.class, e.getClass());
//ARGO_PLACEBO
assertSame(testExc2, e.getCause());
//ARGO_PLACEBO
assertEquals("test", e.getCause().getMessage());
//ARGO_PLACEBO
assertTrue(gen.isClosed());
        }
        gen.close();
    }

    /*
    /**********************************************************
    /* Test methods, deprecated
    /**********************************************************
     */

    @SuppressWarnings("deprecation")
    public void testSubtypesDeprecated()
    {
        // just for code coverage
        List<Class<?>> supers = ClassUtil.findSuperTypes(String.class, Object.class);
//ARGO_PLACEBO
assertFalse(supers.isEmpty()); // serializable/comparable/char-seq
    }

    @SuppressWarnings("deprecation")
    public void testHasGetterSignature() throws Exception
    {
//ARGO_PLACEBO
assertFalse(ClassUtil.hasGetterSignature(MaybeGetters.class.getDeclaredMethod("staticMethod")));
//ARGO_PLACEBO
assertFalse(ClassUtil.hasGetterSignature(MaybeGetters.class.getDeclaredMethod("voidMethod")));
//ARGO_PLACEBO
assertFalse(ClassUtil.hasGetterSignature(MaybeGetters.class.getDeclaredMethod("setMethod", Integer.TYPE)));
//ARGO_PLACEBO
assertTrue(ClassUtil.hasGetterSignature(MaybeGetters.class.getDeclaredMethod("getMethod")));
    }
}
