package org.json.simple;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JSONObject implements Map<Object,Object>, JSONAware, JSONStreamAware {
	JsonObject json;

	public JSONObject(Map in) {
		json = (JsonObject) JSONValue.lift(in);
	}

	public JSONObject(String in) {
		json = (JsonObject) JsonParser.parseString(in);
	}

	public JSONObject() {
		json = new JsonObject();
	}

	public JSONObject(JsonObject o) {
		json = o;
	}

	@Override
	public int size() {
		return json.keySet().size();
	}

	@Override
	public boolean isEmpty() {
		return json.size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return json.keySet().contains(key.toString());
	}

	@Override
	public boolean containsValue(Object value) {
		return json.entrySet().stream().filter(e -> e.getValue().equals(value)).findAny().isPresent();
	}

	@Override
	public Object get(Object key) {
		JsonElement o = json.get(key.toString());
		return JSONValue.down(o);
	}

	@Override
	public Object put(Object key, Object value) {
		Object res = json.get(key.toString());
		json.add(key.toString(), JSONValue.lift(value));
		return res;
	}

	@Override
	public Object remove(Object key) {
		return json.remove(key.toString());
	}

	@Override
	public void putAll(Map m) {
		for(Object k: m.keySet()) {
			json.add(k.toString(), JSONValue.lift(m.get(k)));
		}
	}

	@Override
	public void clear() {
		for(String k: json.keySet()) {
			json.remove(k);
		}
	}

	@Override
	public Set<Object> keySet() {
		return new HashSet<>(json.keySet());
	}

	@Override
	public Collection<Object> values() {
		List res = new ArrayList();

		for(String k: json.keySet()) {
			res.add(JSONValue.down(k));
		}
		return res;
	}

	@Override
	public Set<Entry<Object, Object>> entrySet() {
		HashSet<Entry<Object, Object>> res = new HashSet<>();
		for(String key: json.keySet()) {
			res.add(new HashMap.SimpleEntry<>(key, JSONValue.down(json.get(key))));
		}
		return res;
	}

	@Override
	public String toString() {
		return json.toString();
	}

	@Override
	public String toJSONString() {
		return json.toString();
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
		for (Entry<Object, Object> e0: entrySet()) {
			if(!other.containsKey(e0.getKey())) {
				return false;
			} else {
				if(!other.get(e0.getKey()).equals(e0.getValue())) {
					return false;
				}
			}
		}
		return true;
	}
}
