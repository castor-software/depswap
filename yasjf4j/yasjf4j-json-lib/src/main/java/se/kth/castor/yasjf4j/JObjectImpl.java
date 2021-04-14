package se.kth.castor.yasjf4j;



import org.kordamp.json.JSONNull;
import org.kordamp.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JObjectImpl extends HashMap implements JObject {

	public JObjectImpl() {
	}

	public JObjectImpl(Map json) throws JException {
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
	}

	public JObjectImpl(String json) throws JException {
		try {
			Map o = JSONObject.fromObject(json);
			for (Object key : o.keySet()) {
				Object el = o.get(key);
				if (el instanceof Map) {
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
		put(s, shield(o));
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		remove(s);
	}

	@Override
	public String YASJF4J_toString() {
		return JSONObject.fromObject(this).toString();
	}

	public static Object shield(Object o) {
		if (o instanceof JNull) return JSONNull.getInstance();
		else return o;
	}

	public static Object unshield(Object o) {
		if (o == JSONNull.getInstance()) return JNull.getInstance();
		else return o;
	}
}
