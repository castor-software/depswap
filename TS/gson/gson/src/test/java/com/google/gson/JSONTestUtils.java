package com.google.gson;

import static org.junit.Assert.assertTrue;

public class JSONTestUtils {
	static double epsilon = 0.00001d;
	public static boolean equivalent(Object o1, Object o2) {
		if(o1 == null) return o2 == null;
		if(o2 == null) return false;
		if(o1.equals(o2)) return true;

		if(o1 instanceof JsonObject) {
			if(!(o2 instanceof JsonObject)) return false;
			JsonObject jo1 = (JsonObject) o1;
			JsonObject jo2 = (JsonObject) o2;
			if(jo1.keySet().size() != jo2.keySet().size()) return false;
			for(String k: jo1.keySet()) {
				if(!jo2.has(k) || !equivalent(jo1.get(k), jo2.get(k))) return false;
			}
			return true;
		}
		if(o2 instanceof JsonObject) return false;

		if(o1 instanceof JsonArray) {
			if(!(o2 instanceof JsonArray)) return false;
			JsonArray jo1 = (JsonArray) o1;
			JsonArray jo2 = (JsonArray) o2;
			if(jo1.size() != jo2.size()) return false;
			for(int i = 0; i < jo1.size(); i++) {
				if(!equivalent(jo1.get(i), jo2.get(i))) return false;
			}
			return true;
		}
		if(o2 instanceof JsonArray) return false;

		if(o1 instanceof Number) {
			if(!(o2 instanceof Number)) return false;
			Number n1 = (Number) o1;
			Number n2 = (Number) o2;
			if(Math.abs(n1.doubleValue() - n2.doubleValue()) <= epsilon) return true;
			else return false;
		}

		return false;
	}

	public static void assertEquivalent(Object o1, Object o2) {
		assertTrue("Expected: <" + o1.toString() + ">, Actual: <" + o2.toString() + ">", equivalent(o1,o2));
	}

	public static void assertEquivalent(String o1, String o2, Gson gson) {
		assertTrue("Expected: <" + o1 + ">, Actual: <" + o2 + ">", equivalent(gson.fromJson(o1, JsonElement.class), gson.fromJson(o2, JsonElement.class)));
	}

	/*public static void assertEquivalent(String jsonString1, String jsonString2) {
		assertTrue("Expected: <" + jsonString1 + ">, Actual: <" + jsonString2 + ">",equivalent(JSONValue.parse(jsonString1), JSONValue.parse(jsonString2)));
	}*/
}
