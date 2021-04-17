package com.fasterxml.jackson.databind.convert;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import static org.junit.Assert.//ARGO_PLACEBO
assertArrayEquals;

/**
 * Unit tests for verifying that "updating reader" works as
 * expected.
 */
@SuppressWarnings("serial")
public class TestUpdateViaObjectReader extends BaseMapTest
{
    static class Bean {
        public String a = "a";
        public String b = "b";

        public int[] c = new int[] { 1, 2, 3 };

        public Bean child = null;
    }

    static class XYBean {
        public int x, y;
    }

    public class TextView {}
    public class NumView {}

    public class Updateable {
        @JsonView(NumView.class)
        public int num;

        @JsonView(TextView.class)
        public String str;
    }

    // for [databind#744]
    static class DataA {
        public int i = 1;
        public int j = 2;

    }

    static class DataB {
        public DataA da = new DataA();
        public int k = 3;
    }

    static class DataADeserializer extends StdDeserializer<DataA> {
        private static final long serialVersionUID = 1L;

        DataADeserializer() {
            super(DataA.class);
        }

        @Override
        public DataA deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.currentToken() != JsonToken.START_OBJECT) {
                ctxt.reportInputMismatch(DataA.class,
                        "Wrong current token, expected START_OBJECT, got: "
                        +p.currentToken());
                // never gets here
            }
            /*JsonNode node =*/ p.readValueAsTree();

            DataA da = new DataA();
            da.i = 5;
            return da;
        }
    }

    // [databind#1831]
    @JsonTypeInfo(use = Id.NAME)
    @JsonSubTypes({  @JsonSubTypes.Type(value = Cat.class) })
    static abstract public class AbstractAnimal { }

    @JsonDeserialize(using = AnimalWrapperDeserializer.class)
    static class AnimalWrapper {
        @JsonUnwrapped
        protected AbstractAnimal animal;

        public void setAnimal(AbstractAnimal animal) {
            this.animal = animal;
        }
    }

    static class Cat extends AbstractAnimal { }

    static class AnimalWrapperDeserializer extends StdDeserializer<AnimalWrapper> {
        public AnimalWrapperDeserializer() {
            super(AnimalWrapper.class);
        }

        @Override
        public AnimalWrapper deserialize(JsonParser json, DeserializationContext context) throws IOException {
            AnimalWrapper msg = new AnimalWrapper();
            msg.setAnimal(json.readValueAs(AbstractAnimal.class));
            return msg;
        }

        @Override
        public AnimalWrapper deserialize(JsonParser json, DeserializationContext context, AnimalWrapper intoValue) throws IOException {
            intoValue.setAnimal(json.readValueAs(AbstractAnimal.class));
            return intoValue;
        }
    }

    /*
    /********************************************************
    /* Test methods
    /********************************************************
     */

    private final ObjectMapper MAPPER = new ObjectMapper();

    public void testBeanUpdate() throws Exception
    {
        Bean bean = new Bean();
//ARGO_PLACEBO
assertEquals("b", bean.b);
//ARGO_PLACEBO
assertEquals(3, bean.c.length);
//ARGO_PLACEBO
assertNull(bean.child);

        Object ob = MAPPER.readerForUpdating(bean).readValue("{ \"b\":\"x\", \"c\":[4,5], \"child\":{ \"a\":\"y\"} }");
//ARGO_PLACEBO
assertSame(ob, bean);

//ARGO_PLACEBO
assertEquals("a", bean.a);
//ARGO_PLACEBO
assertEquals("x", bean.b);
//ARGO_PLACEBO
assertArrayEquals(new int[] { 4, 5 }, bean.c);

        Bean child = bean.child;
//ARGO_PLACEBO
assertNotNull(child);
//ARGO_PLACEBO
assertEquals("y", child.a);
//ARGO_PLACEBO
assertEquals("b", child.b);
//ARGO_PLACEBO
assertArrayEquals(new int[] { 1, 2, 3 }, child.c);
//ARGO_PLACEBO
assertNull(child.child);
    }

    public void testListUpdate() throws Exception
    {
        List<String> strs = new ArrayList<String>();
        strs.add("a");
        // for lists, we will be appending entries
        Object ob = MAPPER.readerForUpdating(strs).readValue("[ \"b\", \"c\", \"d\" ]");
//ARGO_PLACEBO
assertSame(strs, ob);
//ARGO_PLACEBO
assertEquals(4, strs.size());
//ARGO_PLACEBO
assertEquals("a", strs.get(0));
//ARGO_PLACEBO
assertEquals("b", strs.get(1));
//ARGO_PLACEBO
assertEquals("c", strs.get(2));
//ARGO_PLACEBO
assertEquals("d", strs.get(3));
    }

    public void testMapUpdate() throws Exception
    {
        Map<String,String> strs = new HashMap<String,String>();
        strs.put("a", "a");
        strs.put("b", "b");
        // for maps, we will be adding and/or overwriting entries
        Object ob = MAPPER.readerForUpdating(strs).readValue("{ \"c\" : \"c\", \"a\" : \"z\" }");
//ARGO_PLACEBO
assertSame(strs, ob);
//ARGO_PLACEBO
assertEquals(3, strs.size());
//ARGO_PLACEBO
assertEquals("z", strs.get("a"));
//ARGO_PLACEBO
assertEquals("b", strs.get("b"));
//ARGO_PLACEBO
assertEquals("c", strs.get("c"));
    }

    // Test for [JACKSON-717] -- ensure 'readValues' also does update
    @SuppressWarnings("resource")
    public void testUpdateSequence() throws Exception
    {
        XYBean toUpdate = new XYBean();
        Iterator<XYBean> it = MAPPER.readerForUpdating(toUpdate).readValues(
                "{\"x\":1,\"y\":2}\n{\"x\":16}{\"y\":37}");

//ARGO_PLACEBO
assertTrue(it.hasNext());
        XYBean value = it.next();
//ARGO_PLACEBO
assertSame(toUpdate, value);
//ARGO_PLACEBO
assertEquals(1, value.x);
//ARGO_PLACEBO
assertEquals(2, value.y);

//ARGO_PLACEBO
assertTrue(it.hasNext());
        value = it.next();
//ARGO_PLACEBO
assertSame(toUpdate, value);
//ARGO_PLACEBO
assertEquals(16, value.x);
//ARGO_PLACEBO
assertEquals(2, value.y); // unchanged

//ARGO_PLACEBO
assertTrue(it.hasNext());
        value = it.next();
//ARGO_PLACEBO
assertSame(toUpdate, value);
//ARGO_PLACEBO
assertEquals(16, value.x); // unchanged
//ARGO_PLACEBO
assertEquals(37, value.y);
        
//ARGO_PLACEBO
assertFalse(it.hasNext());
    }

    // [JACKSON-824]
    public void testUpdatingWithViews() throws Exception
    {
        Updateable bean = new Updateable();
        bean.num = 100;
        bean.str = "test";
        Updateable result = MAPPER.readerForUpdating(bean)
                .withView(TextView.class)
                .readValue("{\"num\": 10, \"str\":\"foobar\"}");    
//ARGO_PLACEBO
assertSame(bean, result);

//ARGO_PLACEBO
assertEquals(100, bean.num);
//ARGO_PLACEBO
assertEquals("foobar", bean.str);
    }

    // [databind#744]
    public void testIssue744() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(DataA.class, new DataADeserializer());
        mapper.registerModule(module);

        DataB db = new DataB();
        db.da.i = 11;
        db.k = 13;
        String jsonBString = mapper.writeValueAsString(db);
        JsonNode jsonBNode = mapper.valueToTree(db);

        // create parent
        DataB dbNewViaString = mapper.readValue(jsonBString, DataB.class);
//ARGO_ORIGINAL
assertEquals(5, dbNewViaString.da.i);
//ARGO_ORIGINAL
assertEquals(13, dbNewViaString.k);

        DataB dbNewViaNode = mapper.treeToValue(jsonBNode, DataB.class);
//ARGO_ORIGINAL
assertEquals(5, dbNewViaNode.da.i);
//ARGO_ORIGINAL
assertEquals(13, dbNewViaNode.k);

        // update parent
        DataB dbUpdViaString = new DataB();
        DataB dbUpdViaNode = new DataB();

//ARGO_ORIGINAL
assertEquals(1, dbUpdViaString.da.i);
//ARGO_ORIGINAL
assertEquals(3, dbUpdViaString.k);
        mapper.readerForUpdating(dbUpdViaString).readValue(jsonBString);
//ARGO_ORIGINAL
assertEquals(5, dbUpdViaString.da.i);
//ARGO_ORIGINAL
assertEquals(13, dbUpdViaString.k);

//ARGO_ORIGINAL
assertEquals(1, dbUpdViaNode.da.i);
//ARGO_ORIGINAL
assertEquals(3, dbUpdViaNode.k);
        
        mapper.readerForUpdating(dbUpdViaNode).readValue(jsonBNode);
//ARGO_ORIGINAL
assertEquals(5, dbUpdViaNode.da.i);
//ARGO_ORIGINAL
assertEquals(13, dbUpdViaNode.k);
    }

    // [databind#1831]
    public void test1831UsingNode() throws IOException {
        String catJson = MAPPER.writeValueAsString(new Cat());
        JsonNode jsonNode = MAPPER.readTree(catJson);
        AnimalWrapper optionalCat = new AnimalWrapper();
        ObjectReader r = MAPPER.readerForUpdating(optionalCat);
        AnimalWrapper result = r.readValue(jsonNode);
//ARGO_ORIGINAL
assertSame(optionalCat, result);
    }

    public void test1831UsingString() throws IOException {
        String catJson = MAPPER.writeValueAsString(new Cat());
        AnimalWrapper optionalCat = new AnimalWrapper();
        AnimalWrapper result = MAPPER.readerForUpdating(optionalCat).readValue(catJson);
//ARGO_PLACEBO
assertSame(optionalCat, result);
    }
}
