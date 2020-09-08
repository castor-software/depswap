package se.kth.castor.yasjf4j;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class JObjectImpl extends JSONObject implements JObject {

	public JObjectImpl() {
	}

	public JObjectImpl(JSONObject json) throws JException {
		try {
			for(String key: json.keySet()) {
				Object el = json.get(key);
				if(el instanceof JSONObject) {
					put(key, new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					put(key, new JArrayImpl((JSONArray) el));
				} else {
					put(key, el);
				}
			}
		} catch (JSONException e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			JSONObject o = new JSONObject(json);
			for(String key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof JSONObject) {
					put(key, new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					put(key, new JArrayImpl((JSONArray) el));
				} else {
					put(key, el);
				}
			}
		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return keySet();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		try {
			return get(s);
		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			put(s,o);
		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		remove(s);
	}

	@Override
	public String YASJF4J_toString() {
		return toString();
	}
}
