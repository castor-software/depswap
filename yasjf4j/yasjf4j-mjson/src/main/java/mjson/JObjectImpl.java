package mjson;

import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JObjectImpl extends Json.ObjectJson implements JObject {

	public JObjectImpl(Json json) throws JException {
		for(String key: json.asJsonMap().keySet()) {
			Object el = json.at(key);
			if(el instanceof Json) {
				if(((Json) el).isObject())
					set(key, new JObjectImpl((Json) el));
				else if(((Json) el).isArray())
					set(key, new JArrayImpl((Json) el));
				else
					set(key, el);
			} else {
				set(key, el);
			}
		}
	}


	public JObjectImpl(String json) throws JException {
		Json o = Json.read(json);
		for(String key: o.asJsonMap().keySet()) {
			Object el = o.at(key);
			if(el instanceof Json) {
				if(((Json) el).isObject())
					set(key, new JObjectImpl((Json) el));
				else if(((Json) el).isArray())
					set(key, new JArrayImpl((Json) el));
				else
					set(key, el);
			} else {
				set(key, el);
			}
		}
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return isObject() ? asJsonMap().keySet() : new HashSet<String>();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		return at(s);
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		set(s,o);
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		remove(s);
	}
}
