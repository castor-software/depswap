package se.kth.castor.yasjf4j;



import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JObjectImpl extends HashMap implements JObject {
	public static JSONDeserializer deserializer = new JSONDeserializer();
	public static JSONSerializer serializer = new JSONSerializer();

	public JObjectImpl() {
	}

	public JObjectImpl(Map json) throws JException {
		try {
			for(Object key: json.keySet()) {
				Object el = json.get(key);
				if(el instanceof Map) {
					put(key, new JObjectImpl((Map) el));
				} else if (el instanceof List) {
					put(key, new JArrayImpl((List) el));
				} else {
					put(key, shield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {

			Map o = (Map) deserializer.deserialize(json);
			for(Object key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof Map) {
					put(key, new JObjectImpl((Map) el));
				} else if (el instanceof List) {
					put(key, new JArrayImpl((List) el));
				} else {
					put(key, shield(el));
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
			put(s,shield(o));
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
		return serializer.deepSerialize(this);
	}

	public static Object shield(Object o) {
		if (o instanceof JNull) return null;
		else return o;
	}

	public static Object unshield(Object o) {
		if (o == null) return JNull.getInstance();
		else return o;
	}
}
