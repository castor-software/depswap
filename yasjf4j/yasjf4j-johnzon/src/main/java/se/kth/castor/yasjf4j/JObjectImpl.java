package se.kth.castor.yasjf4j;


import org.apache.johnzon.core.JsonGeneratorFactoryImpl;
import org.apache.johnzon.core.JsonProviderImpl;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JObjectImpl extends HashMap<String, Object> implements JObject {

	public static JsonGeneratorFactoryImpl gen = new JsonGeneratorFactoryImpl(new HashMap<>());

	public JObjectImpl() {
	}

	public JObjectImpl(JsonObject json) throws JException {
		try {
			for(String key: json.keySet()) {
				Object el = json.get(key);
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

	public JObjectImpl(String json) throws JException {
		try {
			JsonProviderImpl provider = new JsonProviderImpl();
			JsonObject o = (JsonObject) provider.createParser(new StringReader(json)).getValue();
			for(String key: o.keySet()) {
				Object el = o.get(key);
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

	@Override
	public Set<String> YASJF4J_getKeys() {
		return keySet();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		if(!containsKey(s)) throw new JException();
		try {
			return unshield(get(s));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			put(s,o);
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		remove(s);
	}

	public JsonValue getAsJsonValue() {
		JsonProviderImpl provider = new JsonProviderImpl();
		JsonObjectBuilder ob = provider.createObjectBuilder();
		for(String key: keySet()) {
			Object val = get(key);
			if(val instanceof JObjectImpl) {
				ob.add(key, ((JObjectImpl) val).getAsJsonValue());
			} else if (val instanceof JArrayImpl) {
				ob.add(key, ((JArrayImpl) val).getAsJsonValue());
			} else {
				ob.add(key, shield(val));
			}
		}
		return ob.build();
	}

	@Override
	public String YASJF4J_toString() {
		StringWriter w = new StringWriter();
		JsonGenerator g = gen.createGenerator(w);
		g.write(getAsJsonValue());
		g.flush();
		return w.toString();
	}



	public static JsonValue shield(Object o) {
		JsonProviderImpl f = new JsonProviderImpl();

		if(o instanceof JsonValue) {
			return (JsonValue) o;
		} else if(o instanceof Long) {
			return f.createValue((Long) o);
		} else if(o instanceof Integer) {
			return f.createValue((Integer) o);
		} else if(o instanceof Byte) {
			return f.createValue((Byte) o);
		} else if(o instanceof Short) {
			return f.createValue((Short) o);
		} else if(o instanceof Double) {
			return f.createValue((Double) o);
		} else if(o instanceof Float) {
			return f.createValue((Float) o);
		} else if(o instanceof String) {
			return f.createValue((String) o);
		} else if(o instanceof BigInteger) {
			return f.createValue((BigInteger) o);
		} else if(o instanceof BigDecimal) {
			return f.createValue((BigDecimal) o);
		} else if(o instanceof Boolean) {
			return ((Boolean) o).booleanValue() ? JsonValue.TRUE : JsonValue.FALSE;
		} else if(o == null) {
			return JsonValue.NULL;
		} else if(o instanceof JNull) {
			return JsonValue.NULL;
		} else {
			return f.createValue(o.toString());
		}
	}



	public static Object unshield(Object o) {
		if(o instanceof JsonNumber) {
			return ((JsonNumber) o).numberValue();
		} else if(o == JsonValue.NULL) {
			return JNull.getInstance();
		} else if(o == JsonValue.TRUE) {
			return true;
		} else if(o == JsonValue.FALSE) {
			return false;
		} else if(o instanceof JsonString) {
			return ((JsonString) o).getString();
		} else {
			return o;
		}
	}
}
