package se.kth.castor.yasjf4j;


import org.kopitubruk.util.json.JSONException;
import org.kopitubruk.util.json.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JArrayImpl extends ArrayList implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			List a = (List) JSONParser.parseJSON(json);
			for(Object el: a) {
				if(el instanceof Map) {
					add(new JObjectImpl((Map) el));
				} else if (el instanceof List) {
					add(new JArrayImpl((List) el));
				} else {
					add(el);
				}
			}
		} catch (JSONException e) {
			throw new JException();
		}
	}

	public JArrayImpl(List json) throws JException {
		try {
			for(Object el: json) {
				if(el instanceof Map) {
					add(new JObjectImpl((Map) el));
				} else if (el instanceof List) {
					add(new JArrayImpl((List) el));
				} else {
					add(el);
				}
			}
		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public int YASJF4J_size() {
		return size();
	}

	@Override
	public Object YASJF4J_get(int i) throws JException {
		try {
			return get(i);
		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		try {
			set(i, o);

		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		try {
			add(o);
		} catch (JSONException e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		remove(i);
	}
}
