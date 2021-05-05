package org.json.simple;

import org.json.simple.parser.ParseException;
import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JError;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JNull;
import se.kth.castor.yasjf4j.JObject;
import se.kth.castor.yasjf4j.util.Utils;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.json.simple.JSONValue.autoBox;
import static org.json.simple.JSONValue.recA;
import static org.json.simple.JSONValue.recO;

public class JSONObject extends HashMap implements Map, JSONAware, JSONStreamAware {
	JObject json;

	public JSONObject(Map in) {

		json = JFactory.createJObject();
		in.forEach((k,v) -> {
			try {
				if(v == null) {
					json.YASJF4J_put(k.toString(), JNull.getInstance());
				} else if(v instanceof Map) {
					json.YASJF4J_put(k.toString(),recO((Map) v));
				} else if (v instanceof  List) {
					json.YASJF4J_put(k.toString(),recA((List) v));
				} else if(v.getClass().isArray()) {
					json.YASJF4J_put(k.toString(),recA(autoBox(v)));
				} else if(v instanceof Character) {
					json.YASJF4J_put(k.toString(),v.toString());
				} else {
					json.YASJF4J_put(k.toString(), JSONValue.unshield(v));
				}
				//json.YASJF4J_put(k.toString(), JSONValue.unshield(v));
			} catch (JException e) {
				e.printStackTrace();
			}
		});
		//new org.json.JSONObject(in);
	}

	public JSONObject(String in) throws ParseException {
		try {
			json = (JObject) JFactory.parse(in);
		} catch (JException e) {
			throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
		}
	}

	public JSONObject() {
		json = JFactory.createJObject();
	}

	public JSONObject(JObject o) {
		json = o;
		/*try {
			json = (JObject) Utils.deepTranslate(o, JSONObject::new, JSONArray::new);
		} catch (JException e) {
			e.printStackTrace();
		}*/
	}

	public static String toJSONString(Map map) {
		return new JSONObject(map).toJSONString();
	}

	public static void writeJSONString(Map map, Writer out) throws IOException {
		if(map == null){
			out.write("null");
			return;
		}

		boolean first = true;
		Iterator iter=map.entrySet().iterator();

		out.write('{');
		while(iter.hasNext()){
			if(first)
				first = false;
			else
				out.write(',');
			Map.Entry entry=(Map.Entry)iter.next();
			out.write('\"');
			out.write(escape(String.valueOf(entry.getKey())));
			out.write('\"');
			out.write(':');
			JSONValue.writeJSONString(entry.getValue(), out);
		}
		out.write('}');
	}

	public static String escape(String s){
		return JSONValue.escape(s);
	}

	@Override
	public int size() {
		return json.YASJF4J_getKeys().size();
	}

	@Override
	public boolean isEmpty() {
		return json.YASJF4J_getKeys().size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return json.YASJF4J_getKeys().contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for(String key: json.YASJF4J_getKeys()) {
			try {
				if (json.YASJF4J_get(key).equals(JSONValue.unshield(value))) return true;
			} catch (JException e) {}
		}
		return false;
	}

	@Override
	public Object get(Object key) {
		try {
			return JSONValue.shield(json.YASJF4J_get(key.toString()));
		} catch (JException e) {
			return null;
		}
	}

	@Override
	public Object put(Object key, Object value) {
		try {
			Object v = null;
			if (json.YASJF4J_getKeys().contains(key.toString()))
				v = json.YASJF4J_get(key.toString());

			json.YASJF4J_put(key.toString(), JSONValue.unshield(value));
			return v;
		} catch (JException e) {
			return null;
		}
	}

	@Override
	public Object remove(Object key) {
		try {
			Object v = null;
			if (json.YASJF4J_getKeys().contains(key.toString()))
				v = json.YASJF4J_get(key.toString());

			json.YASJF4J_remove(key.toString());
			return v;
		} catch (JException e) {
			return null;
		}
	}

	@Override
	public void putAll(Map m) {
		for(Object k: m.keySet()) {
			try {
				json.YASJF4J_put(k.toString(),JSONValue.unshield(m.get(k)));
			} catch (JException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void clear() {
		json = JFactory.createJObject();
	}

	@Override
	public Set<String> keySet() {
		return new HashSet<>(json.YASJF4J_getKeys());
	}

	@Override
	public Collection<Object> values() {
		List values = new ArrayList<>();
		for(String k: json.YASJF4J_getKeys()) {
			try {
				values.add(JSONValue.shield(json.YASJF4J_get(k)));
			} catch (JException e) {
				e.printStackTrace();
			}
		}
		return values;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		HashSet<Entry<String, Object>> res = new HashSet<>();
		for(String key: json.YASJF4J_getKeys()) {
			try {
				res.add(new HashMap.SimpleEntry<>(key, JSONValue.shield(json.YASJF4J_get(key))));
			} catch (JException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	@Override
	public String toString() {
		return json.YASJF4J_toString();
	}

	@Override
	public String toJSONString() {
		return json.YASJF4J_toString();
	}

	@Override
	public void writeJSONString(Writer out) throws IOException {
		out.write(json.toString());
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof JSONObject)) return false;
		JSONObject other = ((JSONObject) o);
		if(other.size() != size()) return false;
		for (Entry<String, Object> e0: entrySet()) {
			if(!other.containsKey(e0.getKey())) {
				return false;
			} else {
				Object v0 = e0.getValue();
				Object v1 = other.get(e0.getKey());
				if(v0 == null && v1 != null) return false;
				else if(v0 == null) continue;
				else if(!v0.equals(v1)) {
					return false;
				}
			}
		}
		return true;
	}
}
