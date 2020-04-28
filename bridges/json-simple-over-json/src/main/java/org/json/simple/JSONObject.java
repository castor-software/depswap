package org.json.simple;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JSONObject implements Map<Object,Object>, JSONAware, JSONStreamAware {
	org.json.JSONObject json;

	public JSONObject(Map in) {
		json = new org.json.JSONObject(in);
	}

	public JSONObject(String in) {
		json = new org.json.JSONObject(in);
	}

	public JSONObject() {
		json = new org.json.JSONObject();
	}

	public JSONObject(org.json.JSONObject o) {
		json = o;
	}

	@Override
	public int size() {
		return json.keySet().size();
	}

	@Override
	public boolean isEmpty() {
		return json.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return json.keySet().contains(key.toString());
	}

	@Override
	public boolean containsValue(Object value) {
		return json.toMap().containsValue(value);
	}

	@Override
	public Object get(Object key) {
		Object o = json.get(key.toString());
		if(o instanceof org.json.JSONObject) o = new JSONObject((org.json.JSONObject) o);
		else if(o instanceof org.json.JSONArray) o = new JSONArray((org.json.JSONArray) o);
		return o;
	}

	@Override
	public Object put(Object key, Object value) {
		return json.put(key.toString(), value);
	}

	@Override
	public Object remove(Object key) {
		return json.remove(key.toString());
	}

	@Override
	public void putAll(Map m) {
		for(Object k: m.keySet()) {
			json.put(k.toString(),m.get(k));
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
		return json.toMap().values();
	}

	@Override
	public Set<Entry<Object, Object>> entrySet() {
		HashSet<Entry<Object, Object>> res = new HashSet<>();
		for(String key: json.keySet()) {
			res.add(new HashMap.SimpleEntry<>(key, json.get(key)));
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
