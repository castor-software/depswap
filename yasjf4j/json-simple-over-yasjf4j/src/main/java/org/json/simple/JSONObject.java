package org.json.simple;

import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JObject;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JSONObject implements Map<Object,Object>, JSONAware, JSONStreamAware {
	JObject json;

	public JSONObject(Map in) {

		json = JFactory.createJObject();
		in.forEach((k,v) -> {
			try {
				json.YASJF4J_put(k.toString(),v);
			} catch (JException e) {
				e.printStackTrace();
			}
		});
		//new org.json.JSONObject(in);
	}

	public JSONObject(String in) {
		try {
			json = (JObject) JFactory.parse(in);
		} catch (JException e) {
			e.printStackTrace();
		}
	}

	public JSONObject() {
		json = JFactory.createJObject();
	}

	public JSONObject(JObject o) {
		json = o;
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
				if (json.YASJF4J_get(key).equals(value)) return true;
			} catch (JException e) {}
		}
		return false;
	}

	@Override
	public Object get(Object key) {
		try {
			return json.YASJF4J_get(key.toString());
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

			json.YASJF4J_put(key.toString(), value);
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
				json.YASJF4J_put(k.toString(),m.get(k));
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
	public Set<Object> keySet() {
		return new HashSet<>(json.YASJF4J_getKeys());
	}

	@Override
	public Collection<Object> values() {
		List values = new ArrayList<>();
		for(String k: json.YASJF4J_getKeys()) {
			try {
				values.add(json.YASJF4J_get(k));
			} catch (JException e) {
				e.printStackTrace();
			}
		}
		return values;
	}

	@Override
	public Set<Entry<Object, Object>> entrySet() {
		HashSet<Entry<Object, Object>> res = new HashSet<>();
		for(String key: json.YASJF4J_getKeys()) {
			try {
				res.add(new HashMap.SimpleEntry<>(key, json.YASJF4J_get(key)));
			} catch (JException e) {
				e.printStackTrace();
			}
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
