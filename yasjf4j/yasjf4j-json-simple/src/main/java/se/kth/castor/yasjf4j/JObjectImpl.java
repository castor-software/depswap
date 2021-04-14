package se.kth.castor.yasjf4j;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JObjectImpl extends JSONObject implements JObject {

	public JObjectImpl() {
	}

	public JObjectImpl(JSONObject json) throws JException {
		for(Object key: json.keySet()) {
			Object el = json.get(key);
			if(el instanceof JSONObject) {
				put(key, new JObjectImpl((JSONObject) el));
			} else if (el instanceof JSONArray) {
				put(key, new JArrayImpl((JSONArray) el));
			} else if (el instanceof Character) {
				put(key, ((Character) el).toString());
			} else {
				put(key, shield(el));
			}
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			JSONObject o = (JSONObject) JSONValue.parseWithException(json);
			for(Object key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof JSONObject) {
					put(key, new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					put(key, new JArrayImpl((JSONArray) el));
				} else if (el instanceof Character) {
					put(key, ((Character) el).toString());
				} else {
					put(key, shield(el));
				}
			}
		} catch (ParseException e) {
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
		return toJSONString();
	}
	public static Object shield(Object o) {
		if (o instanceof JNull) return null;
		if (o instanceof Character) return ((Character) o).toString();
		else return o;
	}

	public static Object unshield(Object o) {
		if (o == null) return JNull.getInstance();
		else return o;
	}
}
