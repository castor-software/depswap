package se.kth.castor.yasjf4j;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.serializer.SerializerFeature;

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
					put(key, shield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			DefaultJSONParser p = new DefaultJSONParser(json);
			JSONObject o = (JSONObject) p.parse();
			for(String key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof JSONObject) {
					put(key, new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					put(key, new JArrayImpl((JSONArray) el));
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
		return JSONObject.toJSONString(this, SerializerFeature.WriteMapNullValue);
	}

	public static Object shield(Object o) {
		if (o instanceof JNull) return null;
		else return o;
		//return o;
	}

	public static Object unshield(Object o) {
		if (o == null) return JNull.getInstance();
		else return o;
		//return o;
	}
}
