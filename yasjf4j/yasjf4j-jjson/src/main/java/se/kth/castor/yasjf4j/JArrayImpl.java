package se.kth.castor.yasjf4j;



import de.grobmeier.jjson.JSONArray;
import de.grobmeier.jjson.JSONObject;
import de.grobmeier.jjson.JSONValue;
import de.grobmeier.jjson.convert.JSONDecoder;

import java.util.List;

public class JArrayImpl extends JSONArray implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			JSONDecoder d = new JSONDecoder(json);
			JSONValue v = d.decode();
			if(!(v instanceof JSONArray)) throw new JException();
			JSONArray o = (JSONArray) v;
			for(JSONValue el: o.getValue()) {
				if(el instanceof JSONObject) {
					add(new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					add(new JArrayImpl((JSONArray) el));
				} else {
					add(el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(JSONArray json) throws JException {
		try {
			for(JSONValue el: json.getValue()) {
				if(el instanceof JSONObject) {
					add(new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					add(new JArrayImpl((JSONArray) el));
				} else {
					add(el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(List json) throws JException {
		try {
			for(Object o: json) {
				JSONValue el = JObjectImpl.toJSONValue(o);
				if(el instanceof JSONObject) {
					add(new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					add(new JArrayImpl((JSONArray) el));
				} else {
					add(el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public int YASJF4J_size() {
		return getValue().size();
	}

	@Override
	public Object YASJF4J_get(int i) throws JException {
		try {
			JSONValue v = getValue().get(i);
			return JObjectImpl.toObject(v);
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		try {
			getValue().set(i, JObjectImpl.toJSONValue(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		try {
			getValue().add(JObjectImpl.toJSONValue(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		getValue().remove(i);
	}

	@Override
	public String YASJF4J_toString() {
		return toJSON();
	}

}
