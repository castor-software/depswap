package se.kth.castor.yasjf4j;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static se.kth.castor.yasjf4j.JObjectImpl.shield;
import static se.kth.castor.yasjf4j.JObjectImpl.unshield;

public class JArrayImpl extends JSONArray implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			JSONArray a = new JSONArray(json);
			for(Object el: a) {
				if(el instanceof JSONObject) {
					put(new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					put(new JArrayImpl((JSONArray) el));
				} else {
					put(shield(el));
				}
			}
		} catch (JSONException e) {
			throw new JException();
		}
	}

	public JArrayImpl(JSONArray json) throws JException {
		try {
			for(Object el: json) {
				if(el instanceof JSONObject) {
					put(new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					put(new JArrayImpl((JSONArray) el));
				} else {
					put(shield(el));
				}
			}
		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public int YASJF4J_size() {
		return length();
	}

	@Override
	public Object YASJF4J_get(int i) throws JException {
		try {
			return unshield(get(i));
		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		try {
			put(i, shield(o));

		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		try {
			put(shield(o));
		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		remove(i);
	}

	@Override
	public String YASJF4J_toString() {
		return toString();
	}

	@Override protected void finalize() {
		System.out.println(this + " was finalized!");
	}
}
