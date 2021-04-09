package se.kth.castor.yasjf4j.util;

import org.apache.commons.lang3.ArrayUtils;
import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

	public static List autoBox(Object value) {
		if(value.getClass().getComponentType().isPrimitive()) {
			if(value.getClass().getComponentType() == boolean.class) {
				return Arrays.asList(ArrayUtils.toObject(((boolean[]) value)));
			} else if(value.getClass().getComponentType() == byte.class) {
				return Arrays.asList(ArrayUtils.toObject(((byte[]) value)));
			} else if(value.getClass().getComponentType() == char.class) {
				return Arrays.asList(ArrayUtils.toObject(((char[]) value)));
			} else if(value.getClass().getComponentType() == short.class) {
				return Arrays.asList(ArrayUtils.toObject(((short[]) value)));
			} else if(value.getClass().getComponentType() == int.class) {
				return Arrays.asList(ArrayUtils.toObject(((int[]) value)));
			} else if(value.getClass().getComponentType() == long.class) {
				return Arrays.asList(ArrayUtils.toObject(((long[]) value)));
			} else if(value.getClass().getComponentType() == float.class) {
				/*float[] fa = ((float[]) value);
				double[] da = new double[fa.length];
				for(int i = 0; i < fa.length; i++) da[i] = fa[i];
				return Arrays.asList(ArrayUtils.toObject(da));*/
				return Arrays.asList(ArrayUtils.toObject(((float[]) value)));
			} else {
				return Arrays.asList(ArrayUtils.toObject(((double[]) value)));
			}
		} else if (value.getClass().getComponentType().isArray()) {
			List<List> metalist = new ArrayList<>();
			Object[] ar = ((Object[]) value);
			for(int i = 0; i < ar.length; i++) {
				metalist.add(autoBox(ar[i]));
			}
			return metalist;
		} else {
			return Arrays.asList(((Object[]) value));
		}
	}

	public static JObject recO(Map m) {
		JObject o = JFactory.createJObject();
		for(Object er : m.entrySet()) {
			Map.Entry e = (Map.Entry) er;
			try {
				Object v = e.getValue();
				if(v == null) {
					o.YASJF4J_put(e.getKey().toString(),v);
				} else if(v instanceof Map) {
					o.YASJF4J_put(e.getKey().toString(),recO((Map) v));
				} else if(v instanceof List) {
					o.YASJF4J_put(e.getKey().toString(),recA((List) v));
				} else if(v.getClass().isArray()) {
					o.YASJF4J_put(e.getKey().toString(),recA(autoBox(v)));
				} else {
					o.YASJF4J_put(e.getKey().toString(), v);
				}
			} catch (JException e1) {
				e1.printStackTrace();
			}
		}
		return o;
	}

	public static JArray recA(List m) {
		JArray a = JFactory.createJArray();
		for(Object e: m) {
			try {
				if(e == null) {
					a.YASJF4J_add(e);
				} else if(e instanceof Map) {
					a.YASJF4J_add(recO((Map) e));
				} else if(e instanceof List) {
					a.YASJF4J_add(recA((List) e));
				} else if(e.getClass().isArray()) {
					a.YASJF4J_add(recA(autoBox(e)));
				} else {
					a.YASJF4J_add(e);
				}
			} catch (JException e1) {
				e1.printStackTrace();
			}
		}
		return a;
	}
}
