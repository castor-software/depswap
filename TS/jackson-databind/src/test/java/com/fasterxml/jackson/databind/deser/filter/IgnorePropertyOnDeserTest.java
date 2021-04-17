package com.fasterxml.jackson.databind.deser.filter;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IgnorePropertyOnDeserTest extends BaseMapTest
{
    // [databind#426]
    @JsonIgnoreProperties({ "userId" })
    static class User {
        public String firstName;
        Integer userId; 

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(CharSequence id) {
            userId = Integer.valueOf(id.toString());
        }

        public void setUserId(Integer v) {
            this.userId = v;
        }

        public void setUserId(User u) {
            // bogus
        }

        public void setUserId(boolean b) {
            // bogus
        }
    }

    // [databind#1217]
    static class IgnoreObject {
        public int x = 1;
        public int y = 2;
    }

    final static class TestIgnoreObject {
        @JsonIgnoreProperties({ "x" })
        public IgnoreObject obj;

        @JsonIgnoreProperties({ "y" })
        public IgnoreObject obj2;
    }

    // [databind#1595]
    @JsonIgnoreProperties(value = {"name"}, allowSetters = true)
    @JsonPropertyOrder(alphabetic=true)
    static class Simple1595 {
        private int id;
        private String name;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // [databind#2627]

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class MyPojoValue {
        @JsonIgnoreProperties(ignoreUnknown = true)
        MyPojo2627 value;

        public MyPojo2627 getValue() {
            return value;
        }
    }

    static class MyPojo2627 {
        public String name;
    }

    /*
    /****************************************************************
    /* Unit tests
    /****************************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    // [databind#426]
    public void testIssue426() throws Exception
    {
        final String JSON = aposToQuotes("{'userId': 9, 'firstName': 'Mike' }");
        User result = MAPPER.readerFor(User.class).readValue(JSON);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals("Mike", result.firstName);
//ARGO_PLACEBO
assertNull(result.userId);
    }

    // [databind#1217]
    public void testIgnoreOnProperty1217() throws Exception
    {
        TestIgnoreObject result = MAPPER.readValue(
                aposToQuotes("{'obj':{'x': 10, 'y': 20}, 'obj2':{'x': 10, 'y': 20}}"),
                TestIgnoreObject.class);
//ARGO_PLACEBO
assertEquals(20, result.obj.y);
//ARGO_PLACEBO
assertEquals(10, result.obj2.x);

//ARGO_PLACEBO
assertEquals(1, result.obj.x);
//ARGO_PLACEBO
assertEquals(2, result.obj2.y);

        TestIgnoreObject result1 = MAPPER.readValue(
                  aposToQuotes("{'obj':{'x': 20, 'y': 30}, 'obj2':{'x': 20, 'y': 40}}"),
                  TestIgnoreObject.class);
//ARGO_PLACEBO
assertEquals(1, result1.obj.x);
//ARGO_PLACEBO
assertEquals(30, result1.obj.y);
       
//ARGO_PLACEBO
assertEquals(20, result1.obj2.x);
//ARGO_PLACEBO
assertEquals(2, result1.obj2.y);
    }

    public void testIgnoreViaConfigOverride1217() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configOverride(Point.class)
            .setIgnorals(JsonIgnoreProperties.Value.forIgnoredProperties("y"));
        Point p = mapper.readValue(aposToQuotes("{'x':1,'y':2}"), Point.class);
        // bind 'x', but ignore 'y'
//ARGO_PLACEBO
assertEquals(1, p.x);
//ARGO_PLACEBO
assertEquals(0, p.y);
    }

    // [databind#1595]
    public void testIgnoreGetterNotSetter1595() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Simple1595 config = new Simple1595();
        config.setId(123);
        config.setName("jack");
        String json = mapper.writeValueAsString(config);
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'id':123}"), json);
        Simple1595 des = mapper.readValue(aposToQuotes("{'id':123,'name':'jack'}"), Simple1595.class);
//ARGO_PLACEBO
assertEquals("jack", des.getName());
    }

    // [databind#2627]
    public void testIgnoreUnknownOnField() throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"value\": {\"name\": \"my_name\", \"extra\": \"val\"}, \"type\":\"Json\"}";
        MyPojoValue value = objectMapper.readValue(json, MyPojoValue.class);
//ARGO_PLACEBO
assertNotNull(value);
//ARGO_PLACEBO
assertNotNull(value.getValue());
//ARGO_PLACEBO
assertEquals("my_name", value.getValue().name);
    }
}

