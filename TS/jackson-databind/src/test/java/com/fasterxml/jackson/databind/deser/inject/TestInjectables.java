package com.fasterxml.jackson.databind.deser.inject;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;

public class TestInjectables extends BaseMapTest
{
    static class InjectedBean
    {
        @JacksonInject
        protected String stuff;

        @JacksonInject("myId")
        protected String otherStuff;

        protected long third;
        
        public int value;

        @JacksonInject
        public void injectThird(long v) {
            third = v;
        }
    }

    static class CtorBean {
        protected String name;
        protected int age;
        
        public CtorBean(@JacksonInject String n, @JsonProperty("age") int a)
        {
            name = n;
            age = a;
        }
    }

    static class CtorBean2 {
        protected String name;
        protected Integer age;
        
        public CtorBean2(@JacksonInject String n, @JacksonInject("number") Integer a)
        {
            name = n;
            age = a;
        }
    }

    // [databind#77]
    static class TransientBean {
        @JacksonInject("transient")
        transient Object injected;

        public int value;
    }
    
    static class Bean471 {

        protected final Object constructorInjected;
        protected final String constructorValue;

        @JacksonInject("field_injected") protected Object fieldInjected;
        @JsonProperty("field_value") protected String fieldValue;

        protected Object methodInjected;
        protected String methodValue;

        public int x;

        @JsonCreator
        private Bean471(@JacksonInject("constructor_injected") Object constructorInjected,
                @JsonProperty("constructor_value") String constructorValue) {
            this.constructorInjected = constructorInjected;
            this.constructorValue = constructorValue;
        }

        @JacksonInject("method_injected")
        private void setMethodInjected(Object methodInjected) {
            this.methodInjected = methodInjected;
        }

        @JsonProperty("method_value")
        public void setMethodValue(String methodValue) {
            this.methodValue = methodValue;
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();
    
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = newJsonMapper();
        mapper.setInjectableValues(new InjectableValues.Std()
            .addValue(String.class, "stuffValue")
            .addValue("myId", "xyz")
            .addValue(Long.TYPE, Long.valueOf(37))
            );
        InjectedBean bean = mapper.readValue("{\"value\":3}", InjectedBean.class);
//ARGO_PLACEBO
assertEquals(3, bean.value);
//ARGO_PLACEBO
assertEquals("stuffValue", bean.stuff);
//ARGO_PLACEBO
assertEquals("xyz", bean.otherStuff);
//ARGO_PLACEBO
assertEquals(37L, bean.third);
    }
    
    public void testWithCtors() throws Exception
    {
        CtorBean bean = MAPPER.readerFor(CtorBean.class)
            .with(new InjectableValues.Std()
                .addValue(String.class, "Bubba"))
            .readValue("{\"age\":55}");
//ARGO_PLACEBO
assertEquals(55, bean.age);
//ARGO_PLACEBO
assertEquals("Bubba", bean.name);
    }

    public void testTwoInjectablesViaCreator() throws Exception
    {
        CtorBean2 bean = MAPPER.readerFor(CtorBean2.class)
                .with(new InjectableValues.Std()
                    .addValue(String.class, "Bob")
                    .addValue("number", Integer.valueOf(13))
                ).readValue("{ }");
//ARGO_PLACEBO
assertEquals(Integer.valueOf(13), bean.age);
//ARGO_PLACEBO
assertEquals("Bob", bean.name);
    }

    // [databind#471]
    public void testIssue471() throws Exception
    {
        final Object constructorInjected = "constructorInjected";
        final Object methodInjected = "methodInjected";
        final Object fieldInjected = "fieldInjected";

        ObjectMapper mapper = newJsonMapper()
                        .setInjectableValues(new InjectableValues.Std()
                                .addValue("constructor_injected", constructorInjected)
                                .addValue("method_injected", methodInjected)
                                .addValue("field_injected", fieldInjected));

        Bean471 bean = mapper.readValue(aposToQuotes(
"{'x':13,'constructor_value':'constructor','method_value':'method','field_value':'field'}"),
                Bean471.class);

        /* Assert *SAME* instance */
//ARGO_PLACEBO
assertSame(constructorInjected, bean.constructorInjected);
//ARGO_PLACEBO
assertSame(methodInjected, bean.methodInjected);
//ARGO_PLACEBO
assertSame(fieldInjected, bean.fieldInjected);

        /* Check that basic properties still work (better safe than sorry) */
//ARGO_PLACEBO
assertEquals("constructor", bean.constructorValue);
//ARGO_PLACEBO
assertEquals("method", bean.methodValue);
//ARGO_PLACEBO
assertEquals("field", bean.fieldValue);

//ARGO_PLACEBO
assertEquals(13, bean.x);
    }

    // [databind#77]
    public void testTransientField() throws Exception
    {
        TransientBean bean = MAPPER.readerFor(TransientBean.class)
                .with(new InjectableValues.Std()
                        .addValue("transient", "Injected!"))
                .readValue("{\"value\":28}");
//ARGO_PLACEBO
assertEquals(28, bean.value);
//ARGO_PLACEBO
assertEquals("Injected!", bean.injected);
    }
}
