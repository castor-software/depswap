package com.fasterxml.jackson.databind.deser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class TestCachingOfDeser extends BaseMapTest
{
    // For [databind#735]
    public static class TestMapNoCustom {

        public Map<String, Integer> map;
    }

    public static class TestMapWithCustom {

        @JsonDeserialize(contentUsing = CustomDeserializer735.class)
        public Map<String, Integer> map;
    }

    public static class TestListWithCustom {
        @JsonDeserialize(contentUsing = CustomDeserializer735.class)
        public List<Integer> list;
    }

    public static class TestListNoCustom {
        public List<Integer> list;
    }

    public static class CustomDeserializer735 extends StdDeserializer<Integer> {
        public CustomDeserializer735() {
            super(Integer.class);
        }

        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return 100 * p.getValueAsInt();
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    final static String MAP_INPUT = "{\"map\":{\"a\":1}}";
    final static String LIST_INPUT = "{\"list\":[1]}";

    
    // Ok: first, use custom-annotated instance first, then standard
    public void testCustomMapCaching1() throws Exception
    {

        ObjectMapper mapper = new ObjectMapper();
        TestMapWithCustom mapC = mapper.readValue(MAP_INPUT, TestMapWithCustom.class);
        TestMapNoCustom mapStd = mapper.readValue(MAP_INPUT, TestMapNoCustom.class);

//ARGO_PLACEBO
assertNotNull(mapC.map);
//ARGO_PLACEBO
assertNotNull(mapStd.map);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(100), mapC.map.get("a"));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(1), mapStd.map.get("a"));
    }
        
    // And then standard first, custom next
    public void testCustomMapCaching2() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        TestMapNoCustom mapStd = mapper.readValue(MAP_INPUT, TestMapNoCustom.class);
        TestMapWithCustom mapC = mapper.readValue(MAP_INPUT, TestMapWithCustom.class);

//ARGO_PLACEBO
assertNotNull(mapStd.map);
//ARGO_PLACEBO
assertNotNull(mapC.map);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(1), mapStd.map.get("a"));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(100), mapC.map.get("a"));
    }

    // Ok: first, use custom-annotated instance first, then standard
    public void testCustomListCaching1() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        TestListWithCustom listC = mapper.readValue(LIST_INPUT, TestListWithCustom.class);
        TestListNoCustom listStd = mapper.readValue(LIST_INPUT, TestListNoCustom.class);

//ARGO_PLACEBO
assertNotNull(listC.list);
//ARGO_PLACEBO
assertNotNull(listStd.list);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(100), listC.list.get(0));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(1), listStd.list.get(0));
    }

    // First custom-annotated, then standard
    public void testCustomListCaching2() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        TestListNoCustom listStd = mapper.readValue(LIST_INPUT, TestListNoCustom.class);
        TestListWithCustom listC = mapper.readValue(LIST_INPUT, TestListWithCustom.class);

//ARGO_PLACEBO
assertNotNull(listC.list);
//ARGO_PLACEBO
assertNotNull(listStd.list);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(100), listC.list.get(0));
//ARGO_PLACEBO
assertEquals(Integer.valueOf(1), listStd.list.get(0));
    }
}
