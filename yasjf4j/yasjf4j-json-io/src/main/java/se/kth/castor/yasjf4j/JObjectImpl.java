package se.kth.castor.yasjf4j;




import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class JObjectImpl extends JsonObject implements JObject {

	public JObjectImpl() {
	}

	public JObjectImpl(JsonObject json) throws JException {
		try {
			for(Object key: json.keySet()) {
				Object el = json.get(key);
				if(el instanceof JsonObject) {
					JsonObject jo = ((JsonObject) el);
					put(key, new JObjectImpl(jo));
				} else if (el == null) {
					put(key, el);
				} else if (el.getClass().isArray()) {
					put(key, new JArrayImpl((Object[]) el));
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
			//JSONObject o = new JSONObject(json);
			JsonObject o = (JsonObject) JsonReader.jsonToJava(json, customReadArgs);
			for(Object key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof JsonObject) {
					JsonObject jo = ((JsonObject) el);
					put(key, new JObjectImpl(jo));
				} else if (el == null) {
					put(key, el);
				} else if (el.getClass().isArray()) {
					put(key, new JArrayImpl((Object[]) el));
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
		try {
			return get(s);
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

	@Override
	public String YASJF4J_toString() {
		return JsonWriter.objectToJson(this, customPrintArgs);
	}

	public static Map customPrintArgs;
	public static Map customReadArgs;

	static {
		customPrintArgs = new HashMap();
		customPrintArgs.put(JsonWriter.TYPE, false);
		customReadArgs = new HashMap();
		customReadArgs.put(JsonReader.USE_MAPS, true);
	}
}
