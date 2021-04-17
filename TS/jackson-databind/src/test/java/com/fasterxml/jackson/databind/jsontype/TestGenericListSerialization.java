package com.fasterxml.jackson.databind.jsontype;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class TestGenericListSerialization
    extends BaseMapTest
{
    // [JACKSON-356]
    public static class JSONResponse<T> {

        private T result;

        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }
    } 

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    public static class Parent {
        public String parentContent = "PARENT";
    }

    public static class Child1 extends Parent {
        public String childContent1 = "CHILD1";
    }

    public static class Child2 extends Parent {
        public String childContent2 = "CHILD2";
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    public void testSubTypesFor356() throws Exception
    {
        JSONResponse<List<Parent>> input = new JSONResponse<List<Parent>>();

        List<Parent> embedded = new ArrayList<Parent>();
        embedded.add(new Child1());
        embedded.add(new Child2());
        input.setResult(embedded);
        ObjectMapper mapper = jsonMapperBuilder()
                .configure(MapperFeature.USE_STATIC_TYPING, true)
                .build();

        JavaType rootType = TypeFactory.defaultInstance().constructType(new TypeReference<JSONResponse<List<Parent>>>() { });
        byte[] json = mapper.writerFor(rootType).writeValueAsBytes(input);
        
        JSONResponse<List<Parent>> out = mapper.readValue(json, 0, json.length, rootType);

        List<Parent> deserializedContent = out.getResult();

//ARGO_PLACEBO
assertEquals(2, deserializedContent.size());
//ARGO_PLACEBO
assertTrue(deserializedContent.get(0) instanceof Parent);
//ARGO_PLACEBO
assertTrue(deserializedContent.get(0) instanceof Child1);
//ARGO_PLACEBO
assertFalse(deserializedContent.get(0) instanceof Child2);
//ARGO_PLACEBO
assertTrue(deserializedContent.get(1) instanceof Child2);
//ARGO_PLACEBO
assertFalse(deserializedContent.get(1) instanceof Child1);

//ARGO_PLACEBO
assertEquals("PARENT", ((Child1) deserializedContent.get(0)).parentContent);
//ARGO_PLACEBO
assertEquals("PARENT", ((Child2) deserializedContent.get(1)).parentContent);
//ARGO_PLACEBO
assertEquals("CHILD1", ((Child1) deserializedContent.get(0)).childContent1);
//ARGO_PLACEBO
assertEquals("CHILD2", ((Child2) deserializedContent.get(1)).childContent2);
    }
    
}
