package se.kth.castor.yasjf4j;


import org.apache.commons.lang3.ArrayUtils;
import org.glassfish.json.JsonProviderImpl;
import org.glassfish.json.WriterAccess;
import org.glassfish.json.api.BufferPool;


import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.spi.JsonProvider;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class JObjectImpl extends LinkedHashMap<String, JsonValue> implements JsonObject, JObject {
	public static JsonProvider provider = new JsonProviderImpl();
	public static BufferPool bufferPool = new BufferPoolImpl();

	public JObjectImpl() {
	}

	public JObjectImpl(JsonObject json) throws JException {
		try {
			for(String key: json.keySet()) {
				JsonValue el = json.get(key);
				if(el instanceof JsonObject) {
					put(key, new JObjectImpl((JsonObject) el));
				} else if (el instanceof JsonArray) {
					put(key, new JArrayImpl((JsonArray) el));
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
				Object el = json.get(key);
				if(el instanceof JsonObject) {
					put(key, new JObjectImpl((JsonObject) el));
				} else if (el instanceof JsonArray) {
					put(key, new JArrayImpl((JsonArray) el));
				} else {
					put(key, toJSONValue(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			JsonReader jsonReader = Json.createReader(new StringReader(json));
			JsonObject o = jsonReader.readObject();
			for(String key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof JsonObject) {
					put(key, new JObjectImpl((JsonObject) el));
				} else if (el instanceof JsonArray) {
					put(key, new JArrayImpl((JsonArray) el));
				} else {
					put(key, toJSONValue(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return keySet();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		if(!containsKey(s)) throw new JException();
		try {
			return toObject(get(s));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			put(s,toJSONValue(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		remove(s);
	}

	@Override
	public String YASJF4J_toString() {
		return this.toString();
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		try (JsonWriter jw = new WriterAccess(sw, JObjectImpl.bufferPool)) {
			jw.write(this);
		}
		return sw.toString();
	}

	public static JsonValue toJSONValue(Object o) throws JException {
		//null
		if(o == null) {
			return JsonValue.NULL;
		} else if (o instanceof JNull) {
			return JsonValue.NULL;
		} else if (o instanceof JsonValue) {
			return (JsonValue) o;
		} else if (o instanceof  Map) {
			return new JObjectImpl((Map) o);
		} else if (o instanceof  List) {
			return new JArrayImpl((List) o);
		} else if (o.getClass().isArray()) {
			return new JArrayImpl(autoBox(o));
		} else if (o instanceof  String) {
			return provider.createValue((String) o);
		} else if (o instanceof  Number) {
			if(o instanceof Double) {
				return provider.createValue((Double) o);
			} else if (o instanceof Float) {
				return provider.createValue((Float) o);
			} else if (o instanceof BigDecimal) {
				return provider.createValue((BigDecimal) o);
			} else if (o instanceof Integer) {
				return provider.createValue((Integer) o);
			} else if (o instanceof Long) {
				return provider.createValue((Long) o);
			} else {
				return provider.createValue(((Number) o).intValue());
			}
		} else if (o instanceof  Boolean) {
			return ((Boolean) o) ? JsonValue.TRUE : JsonValue.FALSE;
		} else if (o instanceof  Character) {
			return provider.createValue(((Character) o) .toString());
		} else {
			return provider.createValue(o.toString());
		}
	}

	public static Object toObject(JsonValue o) throws ParseException {
		if(o instanceof JsonNumber) {
			return ((JsonNumber) o).numberValue();
		} else if (o instanceof JsonString) {
			return ((JsonString) o).getString();
		} else if (o == JsonValue.TRUE) {
			return true;
		} else if (o == JsonValue.FALSE) {
			return false;
		} else if (o == JsonValue.NULL) {
			return JNull.getInstance();
		} else {
			return o;
		}
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

	@Override
	public JsonArray getJsonArray(String s) {
		return (JsonArray) get(s);
	}

	@Override
	public JsonObject getJsonObject(String s) {
		return (JsonObject) get(s);
	}

	@Override
	public JsonNumber getJsonNumber(String s) {
		return (JsonNumber) get(s);
	}

	@Override
	public JsonString getJsonString(String s) {
		return (JsonString) get(s);
	}

	@Override
	public String getString(String s) {
		return ((JsonString) get(s)).getString();
	}

	@Override
	public String getString(String s, String s1) {
		return containsKey(s) ? ((JsonString) get(s)).getString() : s1;
	}

	@Override
	public int getInt(String s) {
		return ((JsonNumber) get(s)).intValue();
	}

	@Override
	public int getInt(String s, int i) {
		return containsKey(s) ? ((JsonNumber) get(s)).intValue() : i;
	}

	@Override
	public boolean getBoolean(String s) {
		JsonValue value = get(s);
		if (value == null) {
			throw new NullPointerException();
		} else if (value == JsonValue.TRUE) {
			return true;
		} else if (value == JsonValue.FALSE) {
			return false;
		} else {
			throw new ClassCastException();
		}
	}

	@Override
	public boolean getBoolean(String s, boolean b) {
		try {
			return getBoolean(s);
		} catch (Exception e) {
			return b;
		}
	}

	@Override
	public boolean isNull(String s) {
		return get(s).equals(JsonValue.NULL);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.OBJECT;
	}
}
