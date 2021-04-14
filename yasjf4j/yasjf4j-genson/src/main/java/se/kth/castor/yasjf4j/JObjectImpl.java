package se.kth.castor.yasjf4j;




import com.owlike.genson.Genson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static se.kth.castor.yasjf4j.JArrayImpl.shield;
import static se.kth.castor.yasjf4j.JArrayImpl.unshield;

public class JObjectImpl extends HashMap<String, Object> implements JObject {

	public static Genson genson = new Genson();

	public JObjectImpl() {
	}

	public JObjectImpl(Map json) throws JException {
		try {
			for(Object key: json.keySet()) {
				Object el = json.get(key);
				if(el instanceof Map) {
					put(key.toString(), new JObjectImpl((Map) el));
				} else if (el instanceof List) {
					put(key.toString(), new JArrayImpl((List) el));
				} else {
					put(key.toString(), shield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			Map o = (Map) genson.deserialize(json, Object.class);
			for(Object key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof Map) {
					put(key.toString(), new JObjectImpl((Map) el));
				} else if (el instanceof List) {
					put(key.toString(), new JArrayImpl((List) el));
				} else {
					put(key.toString(), shield(el));
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
			put(s, shield(o));
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
		return genson.serialize(this);
	}
}
