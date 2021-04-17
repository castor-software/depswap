package se.kth.castor.yasjf4j;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NullWrapingTest {


	@Test
	public void testArrayNullPrint() throws Exception {
		String sArray ="[null]";
		JArray a = (JArray) JFactory.parse(sArray);
		assertEquals(sArray, a.YASJF4J_toString());
		assertEquals(JNull.getInstance(), a.YASJF4J_get(0));
	}
	@Test
	public void testObjectNullPrint() throws Exception {
		String sObject ="{\"key\":null}";
		JObject a = (JObject) JFactory.parse(sObject);
		assertEquals(sObject, a.YASJF4J_toString());
		assertEquals(JNull.getInstance(), a.YASJF4J_get("key"));
	}
}
