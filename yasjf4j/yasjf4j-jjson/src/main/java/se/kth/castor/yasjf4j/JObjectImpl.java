package se.kth.castor.yasjf4j;



import de.grobmeier.jjson.JSONArray;
import de.grobmeier.jjson.JSONBoolean;
import de.grobmeier.jjson.JSONNull;
import de.grobmeier.jjson.JSONNumber;
import de.grobmeier.jjson.JSONObject;
import de.grobmeier.jjson.JSONString;
import de.grobmeier.jjson.JSONValue;
import de.grobmeier.jjson.convert.JSONDecoder;
import de.grobmeier.jjson.shaded.org.apache.commons.lang3.ArrayUtils;
import se.kth.castor.yasjf4j.util.Utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class JObjectImpl extends JSONObject implements JObject {

	public JObjectImpl() {
	}

	public JObjectImpl(JSONObject json) throws JException {
		try {
			for(String key: json.getValue().keySet()) {
				JSONValue el = json.getValue().get(key);
				if(el instanceof JSONObject) {
					put(key, new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					put(key, new JArrayImpl((JSONArray) el));
				} else {
					put(key, el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(Map json) throws JException {
		try {
			for(Object okey: json.keySet()) {
				String key = okey.toString();
				JSONValue el = toJSONValue(json.get(key));
				if(el instanceof JSONObject) {
					put(key, new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					put(key, new JArrayImpl((JSONArray) el));
				} else {
					put(key, el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			JSONDecoder d = new JSONDecoder(json);
			JSONValue v = d.decode();
			if(!(v instanceof JSONObject)) throw new JException();
			JSONObject o = (JSONObject) v;
			for(String key: o.getValue().keySet()) {
				JSONValue el = o.getValue().get(key);
				if(el instanceof JSONObject) {
					put(key, new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					put(key, new JArrayImpl((JSONArray) el));
				} else {
					put(key, el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return getValue().keySet();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		if(!getValue().containsKey(s)) throw new JException();
		try {
			JSONValue v = getValue().get(s);
			return toObject(v);
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			put(s, toJSONValue(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		getValue().remove(s);
	}

	@Override
	public String YASJF4J_toString() {
		return toJSON();
	}

	public static JSONValue toJSONValue(Object o) throws JException {
		//null
		if(o == null) {
			return new JSONNull();
		} else if (o instanceof JNull) {
			return new JSONNull();
		} else if (o.getClass().isArray()) {
			return new JArrayImpl(Utils.autoBox(o));
		} else if (o instanceof List) {
			return new JArrayImpl((List) o);
		} else if (o instanceof Map) {
			return new JObjectImpl((Map) o);
		} else if (o instanceof JSONValue) {
			return (JSONValue) o;
		} else if (o instanceof  String) {
			return new JSONString((String) o);
		} else if (o instanceof  Number) {
			if(o instanceof Double) {
				return new JSONNumber((Double) o);
			} else if (o instanceof Float) {
				return new JSONNumber((Float) o);
			} else if (o instanceof BigDecimal) {
				return new JSONNumber((BigDecimal) o);
			} else if (o instanceof Integer) {
				return new JSONNumber((Integer) o);
			} else if (o instanceof Long) {
				return new JSONNumber((Long) o);
			} else {
				return new JSONNumber(((Number) o).longValue());
			}
		} else if (o instanceof  Boolean) {
			return new JSONBoolean((Boolean) o);
		} else if (o instanceof  Character) {
			return new JSONString(((Character) o).toString());
		} else {
			return new JSONString(o.toString());
		}
	}

	public static Object toObject(JSONValue o) throws ParseException {
		if(o instanceof JSONNumber) {
			return NumberFormat.getInstance().parse(((JSONNumber) o).getValue());
		} else if (o instanceof JSONString) {
			return ((JSONString) o).getValue().toString();
		} else if (o instanceof JSONBoolean) {
			return ((JSONBoolean) o).getValue();
		} else if (o instanceof JSONNull) {
			return JNull.getInstance();
		} else {
			return o;
		}
	}

//	public static List autoBox(Object value) {
//		if(value.getClass().getComponentType().isPrimitive()) {
//			if(value.getClass().getComponentType() == boolean.class) {
//				return Arrays.asList(ArrayUtils.toObject(((boolean[]) value)));
//			} else if(value.getClass().getComponentType() == byte.class) {
//				return Arrays.asList(ArrayUtils.toObject(((byte[]) value)));
//			} else if(value.getClass().getComponentType() == char.class) {
//				return Arrays.asList(ArrayUtils.toObject(((char[]) value)));
//			} else if(value.getClass().getComponentType() == short.class) {
//				return Arrays.asList(ArrayUtils.toObject(((short[]) value)));
//			} else if(value.getClass().getComponentType() == int.class) {
//				return Arrays.asList(ArrayUtils.toObject(((int[]) value)));
//			} else if(value.getClass().getComponentType() == long.class) {
//				return Arrays.asList(ArrayUtils.toObject(((long[]) value)));
//			} else if(value.getClass().getComponentType() == float.class) {
//				return Arrays.asList(ArrayUtils.toObject(((float[]) value)));
//			} else {
//				return Arrays.asList(ArrayUtils.toObject(((double[]) value)));
//			}
//		} else if (value.getClass().getComponentType().isArray()) {
//			List<List> metalist = new ArrayList<>();
//			Object[] ar = ((Object[]) value);
//			for(int i = 0; i < ar.length; i++) {
//				metalist.add(autoBox(ar[i]));
//			}
//			return metalist;
//		} else {
//			return Arrays.asList(((Object[]) value));
//		}
//	}
}
