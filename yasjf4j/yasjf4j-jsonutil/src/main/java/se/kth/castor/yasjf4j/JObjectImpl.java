package se.kth.castor.yasjf4j;



import org.kopitubruk.util.json.JSONException;
import org.kopitubruk.util.json.JSONParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JObjectImpl extends HashMap implements JObject {

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
					put(key, el);
				}
			}
		} catch (JSONException e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			Map o = (Map) JSONParser.parseJSON(json);
			for(Object key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof Map) {
					put(key, new JObjectImpl((Map) el));
				} else if (el instanceof List) {
					put(key, new JArrayImpl((List) el));
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
}
