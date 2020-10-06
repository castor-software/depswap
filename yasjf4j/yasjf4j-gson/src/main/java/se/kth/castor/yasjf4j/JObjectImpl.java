package se.kth.castor.yasjf4j;




import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JObjectImpl extends JsonElement implements JObject {
	public JsonObject contained;

	@Override
	public JObjectImpl deepCopy() {
		JObjectImpl result = new JObjectImpl();
		for (Map.Entry<String, JsonElement> entry : contained.entrySet()) {
			result.contained.add(entry.getKey(), entry.getValue().deepCopy());
		}
		return result;
	}

	public JObjectImpl() {
		contained = new JsonObject();
	}

	public JObjectImpl(JsonObject json) throws JException {
		contained = new JsonObject();
		try {
			for(String key: json.keySet()) {
				Object el = json.get(key);
				if(el instanceof JsonObject) {
					contained.add(key, new JObjectImpl((JsonObject) el));
				} else if (el instanceof JsonArray) {
					contained.add(key, new JArrayImpl((JsonArray) el));
				} else {
					contained.add(key, (JsonElement) el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		contained = new JsonObject();
		try {
			Gson gson = new Gson();
			JsonObject o = gson.fromJson(json, JsonObject.class);
			for(String key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof JsonObject) {
					contained.add(key, new JObjectImpl((JsonObject) el));
				} else if (el instanceof JsonArray) {
					contained.add(key, new JArrayImpl((JsonArray) el));
				} else {
					contained.add(key, (JsonElement) el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(Map<String, ?> json) throws JException {
		contained = new JsonObject();
		try {
			for(String key: json.keySet()) {
				Object el = json.get(key);
				contained.add(key,toJSONValue(el));
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public boolean isJsonObject() {
		return contained.isJsonObject();
	}

	@Override
	public JsonObject getAsJsonObject() {
		return contained;
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return contained.keySet();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		try {
			return toObject(contained.get(s));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			contained.add(s,  toJSONValue(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		contained.remove(s);
	}

	@Override
	public String YASJF4J_toString() {
		return contained.toString();
	}

	@Override
	public boolean equals(Object o) {
		return (o == this) || (o instanceof JObjectImpl
				&& ((JObjectImpl) o).contained.equals(contained));
	}

	@Override
	public int hashCode() {
		return contained.hashCode();
	}

	public static JsonElement toJSONValue(Object o) throws JException {
		//null
		if(o == null) {
			return JsonNull.INSTANCE;
		} else if (o instanceof JsonPrimitive) {
			return (JsonPrimitive) o;
		} else if (o instanceof  Map) {
			return new JObjectImpl((Map) o);
		} else if (o instanceof List) {
			return new JArrayImpl((List) o);
		} else if (o.getClass().isArray()) {
			return new JArrayImpl(autoBox(o));
		} else if (o instanceof  String) {
			return new JsonPrimitive((String) o);
		} else if (o instanceof  Number) {
			return new JsonPrimitive((Number) o);
		} else if (o instanceof  Boolean) {
			return new JsonPrimitive((Boolean) o);
		} else if (o instanceof  Character) {
			return new JsonPrimitive((Character) o);
		} else if (o instanceof  JsonElement) {
			return (JsonElement) o;
		} else {
			return new JsonPrimitive(o.toString());
		}
	}

	public static Object toObject(JsonElement o) throws ParseException {
		if(o instanceof JsonPrimitive) {
			JsonPrimitive p = (JsonPrimitive) o;
			if(p.isNumber()) {
				return p.getAsNumber();
			} else if(p.isString()) {
				return p.getAsString();
			} else if(p.isBoolean()) {
				return p.getAsBoolean();
			}
			return null;
		} else if (o == JsonNull.INSTANCE) {
			return null;
		} else {
			return o;
		}
	}

	public static List autoBox(Object value) {
		if(value.getClass().getComponentType().isPrimitive()) {
			//ClassUtils.primitivesToWrappers(value.getClass().getComponentType());
			//value.getClass().getComponentType().
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
}
