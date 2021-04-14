package mjson;

import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JNull;
import se.kth.castor.yasjf4j.JObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JObjectImpl extends Json.ObjectJson implements JObject {

	public JObjectImpl(Json json) throws JException {
		try {
			for(String key: json.asJsonMap().keySet()) {
				Object el = json.at(key);
				if(el instanceof Json) {
					if(((Json) el).isObject())
						set(key, new JObjectImpl((Json) el));
					else if(((Json) el).isArray())
						set(key, new JArrayImpl((Json) el));
					else
						set(key, shield(el));
				} else {
					set(key, shield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}


	public JObjectImpl(String json) throws JException {
		try {
			Json o = Json.read(json);
			for(String key: o.asJsonMap().keySet()) {
				Object el = o.at(key);
				if(el instanceof Json) {
					if(((Json) el).isObject())
						set(key, new JObjectImpl((Json) el));
					else if(((Json) el).isArray())
						set(key, new JArrayImpl((Json) el));
					else
						set(key, shield(el));
				} else {
					set(key, shield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return isObject() ? asJsonMap().keySet() : new HashSet<String>();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		if(!has(s)) throw new JException();
		Object o = at(s);
		if(o instanceof NumberJson) {
			return ((NumberJson) o).val;
		} else if(o instanceof StringJson) {
			return ((StringJson) o).asString();
		}
		return unshield(o);
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		set(s,shield(o));
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		remove(s);
	}

	@Override
	public String YASJF4J_toString() {
		return toString();
	}

	public static Object shield(Object o) {
		if (o instanceof JNull) return Json.nil();
		else return o;
	}

	public static Object unshield(Object o) {
		if (o == Json.nil()) return JNull.getInstance();
		else return o;
	}
}
