package com.fasterxml.jackson.databind.jsontype;

import java.util.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Testing to verify that {@link JsonTypeInfo} works
 * for properties as well as types.
 */
@SuppressWarnings("serial")
public class TestPropertyTypeInfo extends BaseMapTest
{
    protected static class BooleanValue {
        public Boolean b;

        @JsonCreator
        public BooleanValue(Boolean value) { b = value; }

        @JsonValue public Boolean value() { return b; }
    }

    static class FieldWrapperBean
    {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object value;

        public FieldWrapperBean() { }
        public FieldWrapperBean(Object o) { value = o; }
    }

    static class FieldWrapperBeanList extends ArrayList<FieldWrapperBean> { }
    static class FieldWrapperBeanMap extends HashMap<String,FieldWrapperBean> { }
    static class FieldWrapperBeanArray {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public FieldWrapperBean[] beans;

        public FieldWrapperBeanArray() { }
        public FieldWrapperBeanArray(FieldWrapperBean[] beans) { this.beans = beans; }
    }
    
    static class MethodWrapperBean
    {
        protected Object value;
        
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object getValue() { return value; }

        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public void setValue(Object v) { value = v; }
        
        public MethodWrapperBean() { }
        public MethodWrapperBean(Object o) { value = o; }
    }
    
    static class MethodWrapperBeanList extends ArrayList<MethodWrapperBean> { }
    static class MethodWrapperBeanMap extends HashMap<String,MethodWrapperBean> { }
    static class MethodWrapperBeanArray {
        protected MethodWrapperBean[] beans;

        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public MethodWrapperBean[] getValue() { return beans; }

        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public void setValue(MethodWrapperBean[] v) { beans = v; }
        
        public MethodWrapperBeanArray() { }
        public MethodWrapperBeanArray(MethodWrapperBean[] beans) { this.beans = beans; }
    }

    static class OtherBean {
        public int x = 1, y = 1;
    }
    
    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    public void testSimpleField() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new FieldWrapperBean(new StringWrapper("foo")));
//System.out.println("JSON/field+object == "+json);
        FieldWrapperBean bean = mapper.readValue(json, FieldWrapperBean.class);
//ARGO_PLACEBO
assertNotNull(bean.value);
//ARGO_PLACEBO
assertEquals(StringWrapper.class, bean.value.getClass());
//ARGO_PLACEBO
assertEquals(((StringWrapper) bean.value).str, "foo");
    }

    public void testSimpleMethod() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(new FieldWrapperBean(new IntWrapper(37)));
//System.out.println("JSON/method+object == "+json);
        FieldWrapperBean bean = mapper.readValue(json, FieldWrapperBean.class);
//ARGO_PLACEBO
assertNotNull(bean.value);
//ARGO_PLACEBO
assertEquals(IntWrapper.class, bean.value.getClass());
//ARGO_PLACEBO
assertEquals(((IntWrapper) bean.value).i, 37);
    }

    public void testSimpleListField() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        FieldWrapperBeanList list = new FieldWrapperBeanList();
        list.add(new FieldWrapperBean(new OtherBean()));
        String json = mapper.writeValueAsString(list);
//System.out.println("JSON/field+list == "+json);
        FieldWrapperBeanList result = mapper.readValue(json, FieldWrapperBeanList.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result.size());
        FieldWrapperBean bean = list.get(0);
//ARGO_PLACEBO
assertEquals(OtherBean.class, bean.value.getClass());
//ARGO_PLACEBO
assertEquals(((OtherBean) bean.value).x, 1);
//ARGO_PLACEBO
assertEquals(((OtherBean) bean.value).y, 1);
    }

    public void testSimpleListMethod() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        MethodWrapperBeanList list = new MethodWrapperBeanList();
        list.add(new MethodWrapperBean(new BooleanValue(true)));
        list.add(new MethodWrapperBean(new StringWrapper("x")));
        list.add(new MethodWrapperBean(new OtherBean()));
        String json = mapper.writeValueAsString(list);
        MethodWrapperBeanList result = mapper.readValue(json, MethodWrapperBeanList.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(3, result.size());
        MethodWrapperBean bean = result.get(0);
//ARGO_PLACEBO
assertEquals(BooleanValue.class, bean.value.getClass());
//ARGO_PLACEBO
assertEquals(((BooleanValue) bean.value).b, Boolean.TRUE);
        bean = result.get(1);
//ARGO_PLACEBO
assertEquals(StringWrapper.class, bean.value.getClass());
//ARGO_PLACEBO
assertEquals(((StringWrapper) bean.value).str, "x");
        bean = result.get(2);
//ARGO_PLACEBO
assertEquals(OtherBean.class, bean.value.getClass());
    }

    public void testSimpleArrayField() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        FieldWrapperBeanArray array = new FieldWrapperBeanArray(new
                FieldWrapperBean[] { new FieldWrapperBean(new BooleanValue(true)) });
        String json = mapper.writeValueAsString(array);
        FieldWrapperBeanArray result = mapper.readValue(json, FieldWrapperBeanArray.class);
//ARGO_PLACEBO
assertNotNull(result);
        FieldWrapperBean[] beans = result.beans;
//ARGO_PLACEBO
assertEquals(1, beans.length);
        FieldWrapperBean bean = beans[0];
//ARGO_PLACEBO
assertEquals(BooleanValue.class, bean.value.getClass());
//ARGO_PLACEBO
assertEquals(((BooleanValue) bean.value).b, Boolean.TRUE);
    }

    public void testSimpleArrayMethod() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        MethodWrapperBeanArray array = new MethodWrapperBeanArray(new
                MethodWrapperBean[] { new MethodWrapperBean(new StringWrapper("A")) });
        String json = mapper.writeValueAsString(array);
        MethodWrapperBeanArray result = mapper.readValue(json, MethodWrapperBeanArray.class);
//ARGO_PLACEBO
assertNotNull(result);
        MethodWrapperBean[] beans = result.beans;
//ARGO_PLACEBO
assertEquals(1, beans.length);
        MethodWrapperBean bean = beans[0];
//ARGO_PLACEBO
assertEquals(StringWrapper.class, bean.value.getClass());
//ARGO_PLACEBO
assertEquals(((StringWrapper) bean.value).str, "A");
    }
    
    public void testSimpleMapField() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        FieldWrapperBeanMap map = new FieldWrapperBeanMap();
        map.put("foop", new FieldWrapperBean(new IntWrapper(13)));
        String json = mapper.writeValueAsString(map);
        FieldWrapperBeanMap result = mapper.readValue(json, FieldWrapperBeanMap.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result.size());
        FieldWrapperBean bean = result.get("foop");
//ARGO_PLACEBO
assertNotNull(bean);
        Object ob = bean.value;
//ARGO_PLACEBO
assertEquals(IntWrapper.class, ob.getClass());
//ARGO_PLACEBO
assertEquals(((IntWrapper) ob).i, 13);
    }

    public void testSimpleMapMethod() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        MethodWrapperBeanMap map = new MethodWrapperBeanMap();
        map.put("xyz", new MethodWrapperBean(new BooleanValue(true)));
        String json = mapper.writeValueAsString(map);
        MethodWrapperBeanMap result = mapper.readValue(json, MethodWrapperBeanMap.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(1, result.size());
        MethodWrapperBean bean = result.get("xyz");
//ARGO_PLACEBO
assertNotNull(bean);
        Object ob = bean.value;
//ARGO_PLACEBO
assertEquals(BooleanValue.class, ob.getClass());
//ARGO_PLACEBO
assertEquals(((BooleanValue) ob).b, Boolean.TRUE);
    }
}
