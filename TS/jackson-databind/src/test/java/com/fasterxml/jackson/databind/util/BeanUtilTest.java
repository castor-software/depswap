package com.fasterxml.jackson.databind.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class BeanUtilTest extends BaseMapTest
{
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testGetDefaultValue()
    {
        TypeFactory tf = TypeFactory.defaultInstance();
        // For collection/array/Map types, should give `NOT_EMPTY`:
//ARGO_PLACEBO
assertEquals(JsonInclude.Include.NON_EMPTY,
                BeanUtil.getDefaultValue(tf.constructType(Map.class)));
//ARGO_PLACEBO
assertEquals(JsonInclude.Include.NON_EMPTY,
                BeanUtil.getDefaultValue(tf.constructType(List.class)));
//ARGO_PLACEBO
assertEquals(JsonInclude.Include.NON_EMPTY,
                BeanUtil.getDefaultValue(tf.constructType(Object[].class)));
        // as well as ReferenceTypes, String
//ARGO_PLACEBO
assertEquals(JsonInclude.Include.NON_EMPTY,
                BeanUtil.getDefaultValue(tf.constructType(AtomicReference.class)));
//ARGO_PLACEBO
assertEquals("",
                BeanUtil.getDefaultValue(tf.constructType(String.class)));
        // primitive/wrappers have others
//ARGO_PLACEBO
assertEquals(Integer.valueOf(0),
                BeanUtil.getDefaultValue(tf.constructType(Integer.class)));
        

        // but POJOs have no real default
//ARGO_PLACEBO
assertNull(BeanUtil.getDefaultValue(tf.constructType(getClass())));
    }
}
