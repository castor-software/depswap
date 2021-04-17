package com.fasterxml.jackson.databind.type;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.JavaType;

// for [databind#1604], [databind#2577]
public class TestTypeFactory1604 extends BaseMapTest
{
    static class Data1604<T> { }

    static class DataList1604<T> extends Data1604<List<T>> { }

    static class RefinedDataList1604<T> extends DataList1604<T> { }

    public static class SneakyDataList1604<BOGUS,T> extends Data1604<List<T>> { }

    static class TwoParam1604<KEY,VALUE> { }

    static class SneakyTwoParam1604<V,K> extends TwoParam1604<K,List<V>> { }

    // [databind#2577]

    static class Either<L, R> { }

    static class EitherWrapper<L, R> {
        public Either<L, R> value;
    }

    static class Left<V> extends Either<V, Void> { }
    static class Right<V> extends Either<Void, V> { }

    public void testCustomTypesRefinedSimple()
    {
        TypeFactory tf = newTypeFactory();
        JavaType base = tf.constructType(new TypeReference<Data1604<List<Long>>>() { });
//ARGO_PLACEBO
assertEquals(Data1604.class, base.getRawClass());
//ARGO_PLACEBO
assertEquals(1, base.containedTypeCount());
//ARGO_PLACEBO
assertEquals(List.class, base.containedType(0).getRawClass());

        JavaType subtype = tf.constructSpecializedType(base, DataList1604.class);
//ARGO_PLACEBO
assertEquals(DataList1604.class, subtype.getRawClass());
//ARGO_PLACEBO
assertEquals(1, subtype.containedTypeCount());
        JavaType paramType = subtype.containedType(0);
//ARGO_PLACEBO
assertEquals(Long.class, paramType.getRawClass());
    }

    public void testCustomTypesRefinedNested()
    {
        TypeFactory tf = newTypeFactory();
        JavaType base = tf.constructType(new TypeReference<Data1604<List<Long>>>() { });
//ARGO_PLACEBO
assertEquals(Data1604.class, base.getRawClass());

        JavaType subtype = tf.constructSpecializedType(base, RefinedDataList1604.class);
//ARGO_PLACEBO
assertEquals(RefinedDataList1604.class, subtype.getRawClass());
//ARGO_PLACEBO
assertEquals(DataList1604.class, subtype.getSuperClass().getRawClass());

//ARGO_PLACEBO
assertEquals(1, subtype.containedTypeCount());
        JavaType paramType = subtype.containedType(0);
//ARGO_PLACEBO
assertEquals(Long.class, paramType.getRawClass());
    }

    public void testCustomTypesRefinedSneaky()
    {
        TypeFactory tf = newTypeFactory();
        JavaType base = tf.constructType(new TypeReference<Data1604<List<Long>>>() { });
//ARGO_PLACEBO
assertEquals(Data1604.class, base.getRawClass());

        JavaType subtype = tf.constructSpecializedType(base, SneakyDataList1604.class);
//ARGO_PLACEBO
assertEquals(SneakyDataList1604.class, subtype.getRawClass());
//ARGO_PLACEBO
assertEquals(2, subtype.containedTypeCount());
//ARGO_PLACEBO
assertEquals(Long.class, subtype.containedType(1).getRawClass());
        // first one, "bogus", has to be essentially "unknown"
//ARGO_PLACEBO
assertEquals(Object.class, subtype.containedType(0).getRawClass());

        // and have correct parent too
//ARGO_PLACEBO
assertEquals(Data1604.class, subtype.getSuperClass().getRawClass());
    }

    public void testTwoParamSneakyCustom()
    {
        TypeFactory tf = newTypeFactory();
        JavaType type = tf.constructType(new TypeReference<TwoParam1604<String,List<Long>>>() { });
//ARGO_PLACEBO
assertEquals(TwoParam1604.class, type.getRawClass());
//ARGO_PLACEBO
assertEquals(String.class, type.containedType(0).getRawClass());
        JavaType ct = type.containedType(1);
//ARGO_PLACEBO
assertEquals(List.class, ct.getRawClass());
//ARGO_PLACEBO
assertEquals(Long.class, ct.getContentType().getRawClass());

        JavaType subtype = tf.constructSpecializedType(type, SneakyTwoParam1604.class);
//ARGO_PLACEBO
assertEquals(SneakyTwoParam1604.class, subtype.getRawClass());
//ARGO_PLACEBO
assertEquals(TwoParam1604.class, subtype.getSuperClass().getRawClass());
//ARGO_PLACEBO
assertEquals(2, subtype.containedTypeCount());

        // should properly resolve type parameters despite sneaky switching, including "unwounding"
        // `List` wrapper
        JavaType first = subtype.containedType(0);
//ARGO_PLACEBO
assertEquals(Long.class, first.getRawClass());
        JavaType second = subtype.containedType(1);
//ARGO_PLACEBO
assertEquals(String.class, second.getRawClass());
    }

    // Also: let's not allow mismatching binding
    public void testErrorForMismatch()
    {
        TypeFactory tf = newTypeFactory();
        // NOTE: plain `String` NOT `List<String>`
        JavaType base = tf.constructType(new TypeReference<Data1604<String>>() { });

        try {
            tf.constructSpecializedType(base, DataList1604.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (IllegalArgumentException e) {
            verifyException(e, "Failed to specialize");
            verifyException(e, "Data1604");
            verifyException(e, "DataList1604");
        }
    }

    // [databind#2577]
    public void testResolveGenericPartialSubtypes()
    {
        TypeFactory tf = newTypeFactory();
        JavaType base = tf.constructType(new TypeReference<Either<Object, Object>>() { });

        JavaType lefty = tf.constructSpecializedType(base, Left.class);
//ARGO_PLACEBO
assertEquals(Left.class, lefty.getRawClass());
        JavaType[] params = tf.findTypeParameters(lefty, Either.class);
//ARGO_PLACEBO
assertEquals(2, params.length);
//ARGO_PLACEBO
assertEquals(Object.class, params[0].getRawClass());
//ARGO_PLACEBO
assertEquals(Void.class, params[1].getRawClass());

        JavaType righty = tf.constructSpecializedType(base, Right.class);
//ARGO_PLACEBO
assertEquals(Right.class, righty.getRawClass());
        
        params = tf.findTypeParameters(righty, Either.class);
//ARGO_PLACEBO
assertEquals(2, params.length);
//ARGO_PLACEBO
assertEquals(Void.class, params[0].getRawClass());
//ARGO_PLACEBO
assertEquals(Object.class, params[1].getRawClass());
    }
}
