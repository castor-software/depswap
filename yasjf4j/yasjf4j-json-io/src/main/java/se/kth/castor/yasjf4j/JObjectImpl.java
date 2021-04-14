package se.kth.castor.yasjf4j;




import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class JObjectImpl extends JsonObject implements JObject {

	public JObjectImpl() {
	}

	public JObjectImpl(JsonObject json) throws JException {
		try {
			for(Object key: json.keySet()) {
				Object el = json.get(key);
				if(el instanceof JsonObject) {
					JsonObject jo = ((JsonObject) el);
					put(escape(key.toString()), new JObjectImpl(jo));
				} else if (el == null) {
					put(escape(key.toString()), shield(el));
				} else if (el.getClass().isArray()) {
					put(escape(key.toString()), new JArrayImpl((Object[]) el));
				} else {
					put(escape(key.toString()), el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			//JSONObject o = new JSONObject(json);
			JsonObject o = (JsonObject) JsonReader.jsonToJava(json, customReadArgs);
			for(Object key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof JsonObject) {
					JsonObject jo = ((JsonObject) el);
					put(escape(key.toString()), new JObjectImpl(jo));
				} else if (el == null) {
					put(escape(key.toString()), shield(el));
				} else if (el.getClass().isArray()) {
					put(escape(key.toString()), new JArrayImpl((Object[]) el));
				} else {
					put(escape(key.toString()), el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		Set<String> r = new LinkedHashSet<>();
		keySet().stream().forEach(s -> r.add(unescape(s.toString())));
		return r;
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		if(!containsKey(escape(s))) throw new JException();
		try {
			return unshield(get(escape(s)));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			put(escape(s), shield(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		remove(escape(s));
	}

	@Override
	public String YASJF4J_toString() {
		return unescape(JsonWriter.objectToJson(this, customPrintArgs));
	}

	@Override
	public String toString() {
		return unescape(JsonWriter.objectToJson(this, customPrintArgs));
	}

	public static Map customPrintArgs;
	public static Map customReadArgs;

	static {
		customPrintArgs = new HashMap();
		customPrintArgs.put(JsonWriter.TYPE, false);
		customReadArgs = new HashMap();
		customReadArgs.put(JsonReader.USE_MAPS, true);
	}

	public static Object shield(Object o) {
		if (o instanceof JNull) return null;
		else return o;
	}

	public static Object unshield(Object o) {
		if (o == null) return JNull.getInstance();
		else return o;
	}

	public static String escape(String str) {
		return str.replace("@", "#@ç°,?45sé&");
		//return str;
	}

	public static String unescape(String str) {
		return str.replace("#@ç°,?45sé&", "@");
		//return str;
	}
}
