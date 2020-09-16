package se.kth.castor.yasjf4j;

import org.junit.Test;
import org.yuanheng.cookjson.TextJsonParser;

import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import java.io.StringReader;

import static org.junit.Assert.*;

public class JFactoryProviderImplTest {

	@Test
	public void testObjectTreeConsistency() throws Exception {

		String sObject = "{\"a\":{\"b\":{\"c\":{\"d\":[5,{\"e\":7}]}}}}";
		JObject o = (JObject) JFactory.parse(sObject);
		assertEquals(sObject, o.YASJF4J_toString().replace(" ", "").replace("\n", ""));
	}


	@Test
	public void testArrayTreeConsistency() throws Exception {
		String sArray ="[0,{\"a\":{\"b\":{\"v\":{\"d\":[5,{\"e\":7}]}}}}]";
		JArray a = (JArray) JFactory.parse(sArray);
		assertEquals(sArray, a.YASJF4J_toString().replace(" ","").replace("\n",""));
	}

	@Test
	public void testServiceProvider() throws Exception {

		JObject obj = JFactory.createJObject();
		assertTrue(obj.YASJF4J_getKeys().isEmpty());
		obj.YASJF4J_put("int", 1);
		assertEquals(1, obj.YASJF4J_getKeys().size());
		obj.YASJF4J_put("str", "toto");
		assertEquals(2, obj.YASJF4J_getKeys().size());
		assertTrue(obj.YASJF4J_getKeys().contains("int"));
		assertTrue(obj.YASJF4J_getKeys().contains("str"));

		assertEquals(1, obj.YASJF4J_get("int"));
		assertEquals("toto", obj.YASJF4J_get("str"));

		JObject obj2 = JFactory.createJObject();
		obj2.YASJF4J_put("obj", obj);
		assertTrue(obj2.YASJF4J_getKeys().contains("obj"));
		assertEquals(obj, obj2.YASJF4J_get("obj"));


		JArray ar = JFactory.createJArray();
		assertEquals(0, ar.YASJF4J_size());
		ar.YASJF4J_add("test");
		assertEquals(1, ar.YASJF4J_size());
		assertEquals("test", ar.YASJF4J_get(0));
		ar.YASJF4J_set(0,obj2);
		assertEquals(1, ar.YASJF4J_size());
		assertEquals(obj2, ar.YASJF4J_get(0));

		JArray ar2 = (JArray) JFactory.parse("     [1,\"2\"]       ");
		assertEquals(2,ar2.YASJF4J_size());
		looseEqual(Long.valueOf(1), ar2.YASJF4J_get(0));
		assertEquals("2", ar2.YASJF4J_get(1));

		JObject obj3 = (JObject) JFactory.parse("\t\n{\"str\":null}");
		assertEquals(1, obj3.YASJF4J_getKeys().size());
		//assertNull(obj3.get("str"));

		JObject obj4 = (JObject) JFactory.parse("\t\n{\"nested\":{\"nested\":{\"nested\":[[0],[0]]}}}");
		assertTrue(obj4.YASJF4J_get("nested") instanceof JObject);
		assertTrue(((JObject) obj4.YASJF4J_get("nested")).YASJF4J_get("nested") instanceof JObject);
		assertTrue(((JObject) ((JObject) obj4.YASJF4J_get("nested")).YASJF4J_get("nested")).YASJF4J_get("nested") instanceof JArray);
		assertTrue(((JArray) ((JObject) ((JObject) obj4.YASJF4J_get("nested")).YASJF4J_get("nested")).YASJF4J_get("nested")).YASJF4J_get(0) instanceof JArray);
		assertTrue(((JArray) ((JObject) ((JObject) obj4.YASJF4J_get("nested")).YASJF4J_get("nested")).YASJF4J_get("nested")).YASJF4J_get(1) instanceof JArray);
		looseEqual(Long.valueOf(0), ((JArray) ((JArray) ((JObject) ((JObject) obj4.YASJF4J_get("nested")).YASJF4J_get("nested")).YASJF4J_get("nested")).YASJF4J_get(0)).YASJF4J_get(0));

	}

	public static void looseEqual(Object n1, Object n2) {
		Number a = (Number) n1;
		Number b = (Number) n2;
		assertEquals(a.longValue(), b.longValue());
	}

}