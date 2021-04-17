package com.fasterxml.jackson.databind.jsontype.deftyping;

import java.util.*;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.testutil.NoCheckSubTypeValidator;
import com.fasterxml.jackson.databind.util.TokenBuffer;

public class TestDefaultForObject
    extends BaseMapTest
{
    static abstract class AbstractBean { }
    
    static class StringBean extends AbstractBean { // ha, punny!
        public String name;

        public StringBean() { this(null); }
        protected StringBean(String n)  { name = n; }
    }

    final static class FinalStringBean extends StringBean {
        protected FinalStringBean() { this(null); }
        public FinalStringBean(String n)  { super(n); }
    }

    enum Choice { YES, NO; }

    /**
     * Another enum type, but this time forcing sub-classing
     */
    enum ComplexChoice {
        MAYBE(true), PROBABLY_NOT(false);

        private boolean state;
    	
        private ComplexChoice(boolean b) { state = b; }
    	
        @Override
        public String toString() { return String.valueOf(state); }
    }

    static class PolymorphicType {
        public String foo;
        public Object bar;
        
        public PolymorphicType() { }
        public PolymorphicType(String foo, int bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }

    final static class BeanHolder
    {
        public AbstractBean bean;
        
        public BeanHolder() { }
        public BeanHolder(AbstractBean b) { bean = b; }
    }

    final static class ObjectHolder
    {
        public Object value;

        public ObjectHolder() { }
        public ObjectHolder(Object v) { value = v; }
    }

    static class DomainBean {
        public int weight;
    }

    static class DiscussBean extends DomainBean {
        public String subject;
    }

    static class DomainBeanWrapper {
        public String name;
        public Object myBean;
    }

    @SuppressWarnings("serial")
    static class BlockAllPTV extends PolymorphicTypeValidator.Base
    {
        @Override
        public Validity validateBaseType(MapperConfig<?> config, JavaType baseType) {
            return Validity.DENIED;
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    /**
     * Unit test that verifies that a bean is stored with type information,
     * when declared type is <code>Object.class</code> (since it is within
     * Object[]), and default type information is enabled.
     */
    public void testBeanAsObject() throws Exception
    {
        ObjectMapper m = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance)
                .build();
        // note: need to wrap, to get declared as Object
        String str = m.writeValueAsString(new Object[] { new StringBean("abc") });

        _verifySerializationAsMap(str);
        
        // Ok: serialization seems to work as expected. Now deserialize:
        Object ob = m.readValue(str, Object[].class);
//ARGO_PLACEBO
assertNotNull(ob);
        Object[] result = (Object[]) ob;
//ARGO_PLACEBO
assertNotNull(result[0]);
//ARGO_PLACEBO
assertEquals(StringBean.class, result[0].getClass());
//ARGO_PLACEBO
assertEquals("abc", ((StringBean) result[0]).name);
    }

    // with 2.5, another test to check that "as-property" is valid option
    public void testBeanAsObjectUsingAsProperty() throws Exception
    {
        ObjectMapper m = JsonMapper.builder()
                .activateDefaultTypingAsProperty(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        ".hype")
                .build();
        // note: need to wrap, to get declared as Object
        String json = m.writeValueAsString(new StringBean("abc"));
        
        // Ok: serialization seems to work as expected. Now deserialize:
        Object result = m.readValue(json, Object.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertEquals(StringBean.class, result.getClass());
//ARGO_PLACEBO
assertEquals("abc", ((StringBean) result).name);
    }

    // [databind#2840]: ensure "as-property" uses PTV passed
    public void testAsPropertyWithPTV() throws Exception {
        ObjectMapper m = JsonMapper.builder()
                .activateDefaultTypingAsProperty(new BlockAllPTV(),
                        ObjectMapper.DefaultTyping.NON_FINAL,
                        "@classy")
                .build();
        String json = m.writeValueAsString(new StringBean("abc"));
        try {
            /*Object result =*/ m.readValue(json, Object.class);
//ARGO_PLACEBO
fail("Should not pass");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "Configured `PolymorphicTypeValidator`");
            verifyException(e, "denied resolution of all subtypes of ");
        }
    }
    
    /**
     * Unit test that verifies that an abstract bean is stored with type information
     * if default type information is enabled for non-concrete types.
     */
    public void testAbstractBean() throws Exception
    {
        // First, let's verify that we'd fail without enabling default type info
        ObjectMapper m = new ObjectMapper();
        AbstractBean[] input = new AbstractBean[] { new StringBean("xyz") };
        String serial = m.writeValueAsString(input);
        try {
            m.readValue(serial, AbstractBean[].class);
//ARGO_PLACEBO
fail("Should have failed");
        } catch (JsonMappingException e) {
            // let's use whatever is currently thrown exception... may change tho
            verifyException(e, "cannot construct");
        }
        // and then that we will succeed with default type info
        m = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE)
                .build();
        serial = m.writeValueAsString(input);
        AbstractBean[] beans = m.readValue(serial, AbstractBean[].class);
//ARGO_PLACEBO
assertEquals(1, beans.length);
//ARGO_PLACEBO
assertEquals(StringBean.class, beans[0].getClass());
//ARGO_PLACEBO
assertEquals("xyz", ((StringBean) beans[0]).name);
    }

    /**
     * Unit test to verify that type information is included for
     * all non-final types, if default typing suitably configured
     */
    public void testNonFinalBean() throws Exception
    {
        ObjectMapper m = JsonMapper.builder()
                // first: use "object or abstract" typing: should produce no type info:
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE)
                .build();
        StringBean bean = new StringBean("x");
//ARGO_PLACEBO
assertEquals("{\"name\":\"x\"}", m.writeValueAsString(bean));
        // then non-final, and voila:
        m = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL)
                .build();
//ARGO_PLACEBO
assertEquals("[\""+StringBean.class.getName()+"\",{\"name\":\"x\"}]",
            m.writeValueAsString(bean));
    }

    public void testNullValue() throws Exception
    {
        ObjectMapper m = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL)
                .build();
        BeanHolder h = new BeanHolder();
        String json = m.writeValueAsString(h);
//ARGO_PLACEBO
assertNotNull(json);
        BeanHolder result = m.readValue(json, BeanHolder.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertNull(result.bean);
    }
    
    public void testEnumAsObject() throws Exception
    {
        // wrapping to be declared as object
        Object[] input = new Object[] { Choice.YES };
        Object[] input2 = new Object[] { ComplexChoice.MAYBE};
        // first, without type info:
//ARGO_PLACEBO
assertEquals("[\"YES\"]", serializeAsString(input));
//ARGO_PLACEBO
assertEquals("[\"MAYBE\"]", serializeAsString(input2));

        // and then with it
        ObjectMapper m = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance)
                .build();

        String json = m.writeValueAsString(input);
//ARGO_PLACEBO
assertEquals("[[\""+Choice.class.getName()+"\",\"YES\"]]", json);

        // which we should get back same way
        Object[] output = m.readValue(json, Object[].class);
//ARGO_PLACEBO
assertEquals(1, output.length);
//ARGO_PLACEBO
assertEquals(Choice.YES, output[0]);

        // ditto for more complicated enum
        json = m.writeValueAsString(input2);
//ARGO_PLACEBO
assertEquals("[[\""+ComplexChoice.class.getName()+"\",\"MAYBE\"]]", json);
        output = m.readValue(json, Object[].class);
//ARGO_PLACEBO
assertEquals(1, output.length);
//ARGO_PLACEBO
assertEquals(ComplexChoice.MAYBE, output[0]);
    }

    @SuppressWarnings("unchecked")
    public void testEnumSet() throws Exception
    {
        EnumSet<Choice> set = EnumSet.of(Choice.NO);
        Object[] input = new Object[] { set };
        ObjectMapper m = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance)
                .build();
        String json = m.writeValueAsString(input);
        Object[] output = m.readValue(json, Object[].class);
//ARGO_PLACEBO
assertEquals(1, output.length);
        Object ob = output[0];
//ARGO_PLACEBO
assertTrue(ob instanceof EnumSet<?>);
        EnumSet<Choice> set2 = (EnumSet<Choice>) ob;
//ARGO_PLACEBO
assertEquals(1, set2.size());
//ARGO_PLACEBO
assertTrue(set2.contains(Choice.NO));
//ARGO_PLACEBO
assertFalse(set2.contains(Choice.YES));
    }

    @SuppressWarnings("unchecked")
    public void testEnumMap() throws Exception
    {
        EnumMap<Choice,String> map = new EnumMap<Choice,String>(Choice.class);
        map.put(Choice.NO, "maybe");
        Object[] input = new Object[] { map };
        ObjectMapper m = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance)
                .build();
        String json = m.writeValueAsString(input);
        Object[] output = m.readValue(json, Object[].class);
//ARGO_PLACEBO
assertEquals(1, output.length);
        Object ob = output[0];
//ARGO_PLACEBO
assertTrue(ob instanceof EnumMap<?,?>);
        EnumMap<Choice,String> map2 = (EnumMap<Choice,String>) ob;
//ARGO_PLACEBO
assertEquals(1, map2.size());
//ARGO_PLACEBO
assertEquals("maybe", map2.get(Choice.NO));
//ARGO_PLACEBO
assertNull(map2.get(Choice.YES));
    }

    public void testJackson311() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL)
                .build();
        String json = mapper.writeValueAsString(new PolymorphicType("hello", 2));
        PolymorphicType value = mapper.readValue(json, PolymorphicType.class);
//ARGO_PLACEBO
assertEquals("hello", value.foo);
//ARGO_PLACEBO
assertEquals(Integer.valueOf(2), value.bar);
    }

    // Also, let's ensure TokenBuffer gets properly handled
    public void testTokenBuffer() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL)
                .build();

        // Ok, first test JSON Object containing buffer:
        TokenBuffer buf = new TokenBuffer(mapper, false);
        buf.writeStartObject();
        buf.writeNumberField("num", 42);
        buf.writeEndObject();
        String json = mapper.writeValueAsString(new ObjectHolder(buf));
        ObjectHolder holder = mapper.readValue(json, ObjectHolder.class);
//ARGO_PLACEBO
assertNotNull(holder.value);
//ARGO_PLACEBO
assertSame(TokenBuffer.class, holder.value.getClass());
        JsonParser jp = ((TokenBuffer) holder.value).asParser();
//ARGO_PLACEBO
assertToken(JsonToken.START_OBJECT, jp.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.FIELD_NAME, jp.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_INT, jp.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.END_OBJECT, jp.nextToken());
//ARGO_PLACEBO
assertNull(jp.nextToken());
        jp.close();
        buf.close();

        // then as an array:
        buf = new TokenBuffer(mapper, false);
        buf.writeStartArray();
        buf.writeBoolean(true);
        buf.writeEndArray();
        json = mapper.writeValueAsString(new ObjectHolder(buf));
        holder = mapper.readValue(json, ObjectHolder.class);
//ARGO_PLACEBO
assertNotNull(holder.value);
//ARGO_PLACEBO
assertSame(TokenBuffer.class, holder.value.getClass());
        jp = ((TokenBuffer) holder.value).asParser();
//ARGO_PLACEBO
assertToken(JsonToken.START_ARRAY, jp.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_TRUE, jp.nextToken());
//ARGO_PLACEBO
assertToken(JsonToken.END_ARRAY, jp.nextToken());
//ARGO_PLACEBO
assertNull(jp.nextToken());
        jp.close();
        buf.close();

        // and finally as scalar
        buf = new TokenBuffer(mapper, false);
        buf.writeNumber(321);
        json = mapper.writeValueAsString(new ObjectHolder(buf));
        holder = mapper.readValue(json, ObjectHolder.class);
//ARGO_PLACEBO
assertNotNull(holder.value);
//ARGO_PLACEBO
assertSame(TokenBuffer.class, holder.value.getClass());
        jp = ((TokenBuffer) holder.value).asParser();
//ARGO_PLACEBO
assertToken(JsonToken.VALUE_NUMBER_INT, jp.nextToken());
//ARGO_PLACEBO
assertEquals(321, jp.getIntValue());
//ARGO_PLACEBO
assertNull(jp.nextToken());
        jp.close();
        buf.close();
    }

    public void testIssue352() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY)
                .build();
        DiscussBean d1 = new DiscussBean();
        d1.subject = "mouse";
        d1.weight=88;
        DomainBeanWrapper wrapper = new DomainBeanWrapper();
        wrapper.name = "mickey";
        wrapper.myBean = d1;
        String json = mapper.writeValueAsString(wrapper);
        DomainBeanWrapper result = mapper.readValue(json, DomainBeanWrapper.class);
//ARGO_PLACEBO
assertNotNull(result);
//ARGO_PLACEBO
assertNotNull(wrapper.myBean);
//ARGO_PLACEBO
assertSame(DiscussBean.class, wrapper.myBean.getClass());
    }    

    // Test to ensure we can also use "As.PROPERTY" inclusion and custom property name
    public void testFeature432() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .activateDefaultTypingAsProperty(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "*CLASS*")
                .build();
        String json = mapper.writeValueAsString(new BeanHolder(new StringBean("punny")));
//ARGO_PLACEBO
assertEquals("{\"bean\":{\"*CLASS*\":\"com.fasterxml.jackson.databind.jsontype.deftyping.TestDefaultForObject$StringBean\",\"name\":\"punny\"}}", json);
    }

    public void testNoGoWithExternalProperty() throws Exception
    {
        try {
            /*ObjectMapper mapper =*/ JsonMapper.builder()
                    .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT,
                        JsonTypeInfo.As.EXTERNAL_PROPERTY)
                    .build();
//ARGO_PLACEBO
fail("Should not have passed");
        } catch (IllegalArgumentException e) {
            verifyException(e, "Cannot use includeAs of EXTERNAL_PROPERTY");
        }
    }

    // [databind#2349]
    public void testWithFinalClass() throws Exception
    {
        // First: type info NOT included
        ObjectMapper mapper = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL)
                .build();
//ARGO_PLACEBO
assertEquals(aposToQuotes("{'name':'abc'}"),
                mapper.writeValueAsString(new FinalStringBean("abc")));

        mapper = JsonMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.EVERYTHING)
                .build();
//ARGO_PLACEBO
assertEquals(aposToQuotes("['"+FinalStringBean.class.getName()+"',{'name':'abc'}]"),
                mapper.writeValueAsString(new FinalStringBean("abc")));
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */
    
    @SuppressWarnings("unchecked")
    private void _verifySerializationAsMap(String str) throws Exception
    {
        // First: validate that structure looks correct (as Map etc)
        // note: should look something like:
        // "[["org.codehaus.jackson.map.jsontype.TestDefaultForObject$StringBean",{"name":"abc"}]]")

        // note: must have default mapper, default typer NOT enabled (to get 'plain' map)
        ObjectMapper m = new ObjectMapper();
        List<Object> list = m.readValue(str, List.class);
//ARGO_PLACEBO
assertEquals(1, list.size()); // no type for main List, just single entry
        Object entryOb = list.get(0);
//ARGO_PLACEBO
assertTrue(entryOb instanceof List<?>);
        // but then type wrapper for bean
        List<?> entryList = (List<?>)entryOb;
//ARGO_PLACEBO
assertEquals(2, entryList.size());
//ARGO_PLACEBO
assertEquals(StringBean.class.getName(), entryList.get(0));
//ARGO_PLACEBO
assertTrue(entryList.get(1) instanceof Map);
        Map<?,?> map = (Map<?,?>) entryList.get(1);
//ARGO_PLACEBO
assertEquals(1, map.size());
//ARGO_PLACEBO
assertEquals("abc", map.get("name"));
    }
}
