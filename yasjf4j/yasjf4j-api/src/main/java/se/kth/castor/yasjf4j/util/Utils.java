package se.kth.castor.yasjf4j.util;

import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JObject;

import java.util.function.Function;


public class Utils {

	public static Object deepTranslate(JObject o, Function<JObject, Object> constructorO, Function<JArray, Object> constructorA) throws JException {
		for(String key: o.YASJF4J_getKeys()) {
			Object value = o.YASJF4J_get(key);
			if(value instanceof JObject) {
				o.YASJF4J_put(key, constructorO.apply((JObject) value));
			} else if(value instanceof JArray) {
				o.YASJF4J_put(key, constructorA.apply((JArray) value));
			} else {
				o.YASJF4J_put(key, value);
			}
		}
		return o;
	}

	public static Object deepTranslate(JArray a, Function<JObject, Object> constructorO, Function<JArray, Object> constructorA) throws JException {
		for(int i = 0; i < a.YASJF4J_size(); i++) {
			Object value = a.YASJF4J_get(i);
			if(value instanceof JObject) {
				a.YASJF4J_set(i, constructorO.apply((JObject) value));
			} else if(value instanceof JArray) {
				a.YASJF4J_set(i, constructorA.apply((JArray) value));
			} else {
				a.YASJF4J_set(i, value);
			}
		}
		return a;
	}
}
