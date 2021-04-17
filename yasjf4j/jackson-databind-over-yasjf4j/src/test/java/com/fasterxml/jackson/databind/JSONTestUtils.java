package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class JSONTestUtils {
	static double epsilon = 0.00001d;
	public static boolean equivalent(Object o1, Object o2) {
		if(o1 == null) return o2 == null;
		if(o2 == null) return false;
		if(o1.equals(o2)) return true;

		if(o1 instanceof ObjectNode) {
			if(!(o2 instanceof ObjectNode)) return false;
			ObjectNode jo1 = (ObjectNode) o1;
			ObjectNode jo2 = (ObjectNode) o2;
			if(jo1.size() != jo2.size()) return false;

			Iterator<Map.Entry<String,JsonNode>> it = jo1.fields();
			while(it.hasNext()) {
				String k = it.next().getKey();
				if(!jo2.has(k) || !equivalent(jo1.get(k), jo2.get(k))) return false;
			}
			return true;
		}
		if(o2 instanceof ObjectNode) return false;

		if(o1 instanceof ArrayNode) {
			if(!(o2 instanceof ArrayNode)) return false;
			ArrayNode jo1 = (ArrayNode) o1;
			ArrayNode jo2 = (ArrayNode) o2;
			if(jo1.size() != jo2.size()) return false;
			for(int i = 0; i < jo1.size(); i++) {
				if(!equivalent(jo1.get(i), jo2.get(i))) return false;
			}
			return true;
		}
		if(o2 instanceof ArrayNode) return false;

		if(o1 instanceof Number) {
			if(!(o2 instanceof Number)) return false;
			Number n1 = (Number) o1;
			Number n2 = (Number) o2;
			if(Math.abs(n1.doubleValue() - n2.doubleValue()) <= epsilon) return true;
			else return false;
		}

		return false;
	}
	public static ObjectMapper mapper = new ObjectMapper();

	public static void assertNonEquivalent(JsonNode o1, JsonNode o2) {
		assertFalse("Expected: <" + o1.toString() + ">, Actual: <" + o2.toString() + ">", equivalent(o1,o2));
	}

	public static void assertEquivalent(JsonNode o1, JsonNode o2) {
		assertTrue("Expected: <" + o1.toString() + ">, Actual: <" + o2.toString() + ">", equivalent(o1,o2));
	}

	public static void assertEquivalent(String o1, JsonNode o2) throws JsonProcessingException {
		assertTrue("Expected: <" + o1 + ">, Actual: <" + o2 + ">", equivalent(mapper.readTree(o1), o2));
	}

	public static void assertEquivalent(JsonNode o1, String o2) throws JsonProcessingException {
		assertTrue("Expected: <" + o1 + ">, Actual: <" + o2 + ">", equivalent(o1, mapper.readTree(o2)));
	}

	public static void assertEquivalent(String jsonString1, String jsonString2) throws JsonProcessingException {
		assertTrue("Expected: <" + jsonString1 + ">, Actual: <" + jsonString2 + ">",equivalent(mapper.readTree(jsonString1), mapper.readTree(jsonString2)));
	}
}