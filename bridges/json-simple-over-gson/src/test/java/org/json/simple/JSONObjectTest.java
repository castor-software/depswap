package org.json.simple;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JSONObjectTest {

	@Test
	public void testEquality() {
		JSONObject o0 = new JSONObject("{\"a\":\"b\"}");
		JSONObject o1 = new JSONObject("{\"a\":\"b\"}");
		Map<String, String> raw = new HashMap<>();
		raw.put("a","b");
		JSONObject o2 = new JSONObject(raw);
		JSONObject o3 = new JSONObject("{\"a\":\"c\"}");

		assertEquals(o0,o1);
		assertEquals(o0,o2);
		assertNotEquals(o0,o3);
	}

}