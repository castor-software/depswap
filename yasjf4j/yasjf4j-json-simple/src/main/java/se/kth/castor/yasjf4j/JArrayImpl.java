package se.kth.castor.yasjf4j;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class JArrayImpl extends JSONArray implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			JSONArray a = (JSONArray) JSONValue.parseWithException(json);
			for(Object el: a) {
				if(el instanceof JSONObject) {
					add(new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					add(new JArrayImpl((JSONArray) el));
				} else {
					add(el);
				}
			}
		} catch (ParseException e) {
			throw new JException();
		}
	}

	public JArrayImpl(JSONArray json) throws JException {
		for(Object el: json) {
			if(el instanceof JSONObject) {
				add(new JObjectImpl((JSONObject) el));
			} else if (el instanceof JSONArray) {
				add(new JArrayImpl((JSONArray) el));
			} else {
				add(el);
			}
		}
	}

	@Override
	public int YASJF4J_size() {
		return size();
	}

	@Override
	public Object YASJF4J_get(int i) throws JException {
		return get(i);
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		set(i, o);
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		add(o);
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		remove(i);
	}

	@Override
	public String YASJF4J_toString() {
		return toJSONString();
	}
}
