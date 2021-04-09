package org.json.simple;


import static junit.framework.Assert.assertTrue;

public class JSONTestUtils {
	static double epsilon = 0.00001d;
	public static boolean equivalent(Object o1, Object o2) {
		if(o1 == null) return o2 == null;
		if(o2 == null) return false;
		if(o1.equals(o2)) return true;

		if(o1 instanceof JSONObject) {
			if(!(o2 instanceof JSONObject)) return false;
			JSONObject jo1 = (JSONObject) o1;
			JSONObject jo2 = (JSONObject) o2;
			if(jo1.keySet().size() != jo2.keySet().size()) return false;
			for(Object k: jo1.keySet()) {
				if(!jo2.containsKey(k) || !equivalent(jo1.get(k), jo2.get(k))) return false;
			}
			return true;
		}
		if(o2 instanceof JSONObject) return false;

		if(o1 instanceof JSONArray) {
			if(!(o2 instanceof JSONArray)) return false;
			JSONArray jo1 = (JSONArray) o1;
			JSONArray jo2 = (JSONArray) o2;
			if(jo1.size() != jo2.size()) return false;
			for(int i = 0; i < jo1.size(); i++) {
				if(!equivalent(jo1.get(i), jo2.get(i))) return false;
			}
			return true;
		}
		if(o2 instanceof JSONArray) return false;

		if(o1 instanceof Number) {
			if(!(o2 instanceof Number)) return false;
			Number n1 = (Number) o1;
			Number n2 = (Number) o2;
			if(Math.abs(n1.doubleValue() - n2.doubleValue()) <= epsilon) return true;
			else return false;
		}

		return false;
	}

	public static void assertEquivalent(String jsonString1, String jsonString2) {
		assertTrue("Expected: <" + jsonString1 + ">, Actual: <" + jsonString2 + ">", equivalent(JSONValue.parse(jsonString1), JSONValue.parse(jsonString2)));
	}
}
