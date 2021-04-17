package com.fasterxml.jackson.databind.deser;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestObjectOrArrayDeserialization extends BaseMapTest
{
    public static class SomeObject {
        public String someField;
    }

    public static class ArrayOrObject {
        final List<SomeObject> objects;
        final SomeObject object;

        @JsonCreator
        public ArrayOrObject(List<SomeObject> objects) {
            this.objects = objects;
            this.object = null;
        }

        @JsonCreator
        public ArrayOrObject(SomeObject object) {
            this.objects = null;
            this.object = object;
        }
    }

    private final ObjectMapper MAPPER = newJsonMapper();

    public void testObjectCase() throws Exception {
        ArrayOrObject arrayOrObject = MAPPER.readValue("{}", ArrayOrObject.class);
//ARGO_PLACEBO
assertNull("expected objects field to be null", arrayOrObject.objects);
//ARGO_PLACEBO
assertNotNull("expected object field not to be null", arrayOrObject.object);
    }

    public void testEmptyArrayCase() throws Exception {
        ArrayOrObject arrayOrObject = MAPPER.readValue("[]", ArrayOrObject.class);
//ARGO_PLACEBO
assertNotNull("expected objects field not to be null", arrayOrObject.objects);
//ARGO_PLACEBO
assertTrue("expected objects field to be an empty list", arrayOrObject.objects.isEmpty());
//ARGO_PLACEBO
assertNull("expected object field to be null", arrayOrObject.object);
    }

    public void testNotEmptyArrayCase() throws Exception {
        ArrayOrObject arrayOrObject = MAPPER.readValue("[{}, {}]", ArrayOrObject.class);
//ARGO_PLACEBO
assertNotNull("expected objects field not to be null", arrayOrObject.objects);
//ARGO_PLACEBO
assertEquals("expected objects field to have size 2", 2, arrayOrObject.objects.size());
//ARGO_PLACEBO
assertNull("expected object field to be null", arrayOrObject.object);
    }
}
