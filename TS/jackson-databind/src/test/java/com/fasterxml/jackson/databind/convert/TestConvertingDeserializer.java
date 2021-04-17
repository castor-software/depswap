package com.fasterxml.jackson.databind.convert;

import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

public class TestConvertingDeserializer
extends com.fasterxml.jackson.databind.BaseMapTest
{
    @JsonDeserialize(converter=ConvertingBeanConverter.class)
    static class ConvertingBean
    {
        protected int x, y;

        protected ConvertingBean(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    static class Point
    {
        protected int x, y;
    
        public Point(int v1, int v2) {
            x = v1;
            y = v2;
        }
    }

    static class ConvertingBeanContainer
    {
        public List<ConvertingBean> values;

        public ConvertingBeanContainer() { }
        public ConvertingBeanContainer(ConvertingBean... beans) {
            values = Arrays.asList(beans);
        }
    }

    static class ConvertingBeanConverter extends StdConverter<int[],ConvertingBean>
    {
        @Override
        public ConvertingBean convert(int[] values) {
            return new ConvertingBean(values[0], values[1]);
        }
    }
    
    private static class PointConverter extends StdConverter<int[], Point>
    {
        @Override public Point convert(int[] value) {
            return new Point(value[0], value[1]);
        }
    }

    static class PointWrapper {
        @JsonDeserialize(converter=PointConverter.class)
        public Point value;

        protected PointWrapper() { }
        public PointWrapper(int x, int y) {
            value = new Point(x, y);
        }
    }
    
    static class PointListWrapperArray {
        @JsonDeserialize(contentConverter=PointConverter.class)
        public Point[] values;
    }

    static class PointListWrapperList {
        @JsonDeserialize(contentConverter=PointConverter.class)
        public List<Point> values;
    }

    static class PointListWrapperMap {
        @JsonDeserialize(contentConverter=PointConverter.class)
        public Map<String,Point> values;
    }

    static class LowerCaser extends StdConverter<String, String>
    {
        @Override
        public String convert(String value) {
            return value.toLowerCase();
        }
        
    }

    static class LowerCaseText {
        @JsonDeserialize(converter=LowerCaser.class)
        public String text;
    }

    static class LowerCaseTextArray {
        @JsonDeserialize(contentConverter=LowerCaser.class)
        public String[] texts;
    }

    // for [databind#795]
    
    static class ToNumberConverter extends StdConverter<String,Number>
    {
        @Override
        public Number convert(String value) {
            return new BigDecimal(value);
        }
    }

    static class Issue795Bean
    {
        @JsonDeserialize(converter=ToNumberConverter.class)
        public Number value;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testClassAnnotationSimple() throws Exception
    {
        ConvertingBean bean = objectReader(ConvertingBean.class).readValue("[1,2]");
//ARGO_PLACEBO
assertNotNull(bean);
//ARGO_PLACEBO
assertEquals(1, bean.x);
//ARGO_PLACEBO
assertEquals(2, bean.y);
    }

    public void testClassAnnotationForLists() throws Exception
    {
        ConvertingBeanContainer container = objectReader(ConvertingBeanContainer.class)
                .readValue("{\"values\":[[1,2],[3,4]]}");
//ARGO_PLACEBO
assertNotNull(container);
//ARGO_PLACEBO
assertNotNull(container.values);
//ARGO_PLACEBO
assertEquals(2, container.values.size());
//ARGO_PLACEBO
assertEquals(4, container.values.get(1).y);
    }

    public void testPropertyAnnotationSimple() throws Exception
    {
        PointWrapper wrapper = objectReader(PointWrapper.class).readValue("{\"value\":[3,4]}");
//ARGO_PLACEBO
assertNotNull(wrapper);
//ARGO_PLACEBO
assertNotNull(wrapper.value);
//ARGO_PLACEBO
assertEquals(3, wrapper.value.x);
//ARGO_PLACEBO
assertEquals(4, wrapper.value.y);
    }

    public void testPropertyAnnotationLowerCasing() throws Exception
    {
        LowerCaseText text = objectReader(LowerCaseText.class).readValue("{\"text\":\"Yay!\"}");
//ARGO_PLACEBO
assertNotNull(text);
//ARGO_PLACEBO
assertNotNull(text.text);
//ARGO_PLACEBO
assertEquals("yay!", text.text);
    }

    public void testPropertyAnnotationArrayLC() throws Exception
    {
        LowerCaseTextArray texts = objectReader(LowerCaseTextArray.class).readValue("{\"texts\":[\"ABC\"]}");
//ARGO_PLACEBO
assertNotNull(texts);
//ARGO_PLACEBO
assertNotNull(texts.texts);
//ARGO_PLACEBO
assertEquals(1, texts.texts.length);
//ARGO_PLACEBO
assertEquals("abc", texts.texts[0]);
    }

    public void testPropertyAnnotationForArrays() throws Exception
    {
        PointListWrapperArray array = objectReader(PointListWrapperArray.class)
                .readValue("{\"values\":[[4,5],[5,4]]}");
//ARGO_PLACEBO
assertNotNull(array);
//ARGO_PLACEBO
assertNotNull(array.values);
//ARGO_PLACEBO
assertEquals(2, array.values.length);
//ARGO_PLACEBO
assertEquals(5, array.values[1].x);
    }

    public void testPropertyAnnotationForLists() throws Exception
    {
        PointListWrapperList array = objectReader(PointListWrapperList.class)
                .readValue("{\"values\":[[7,8],[8,7]]}");
//ARGO_PLACEBO
assertNotNull(array);
//ARGO_PLACEBO
assertNotNull(array.values);
//ARGO_PLACEBO
assertEquals(2, array.values.size());
//ARGO_PLACEBO
assertEquals(7, array.values.get(0).x);
    }

    public void testPropertyAnnotationForMaps() throws Exception
    {
        PointListWrapperMap map = objectReader(PointListWrapperMap.class)
                .readValue("{\"values\":{\"a\":[1,2]}}");
//ARGO_PLACEBO
assertNotNull(map);
//ARGO_PLACEBO
assertNotNull(map.values);
//ARGO_PLACEBO
assertEquals(1, map.values.size());
        Point p = map.values.get("a");
//ARGO_PLACEBO
assertNotNull(p);
//ARGO_PLACEBO
assertEquals(1, p.x);
//ARGO_PLACEBO
assertEquals(2, p.y);
    }

    // [databind#795]
    public void testConvertToAbstract() throws Exception
    {
        Issue795Bean bean = objectReader(Issue795Bean.class)
                .readValue("{\"value\":\"1.25\"}");
//ARGO_PLACEBO
assertNotNull(bean.value);
//ARGO_PLACEBO
assertTrue("Type not BigDecimal but "+bean.value.getClass(),
                bean.value instanceof BigDecimal);
//ARGO_PLACEBO
assertEquals(new BigDecimal("1.25"), bean.value);
    }
}
