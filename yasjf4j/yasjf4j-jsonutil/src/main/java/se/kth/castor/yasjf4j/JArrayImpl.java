package se.kth.castor.yasjf4j;


import org.kopitubruk.util.json.JSONException;
import org.kopitubruk.util.json.JSONParser;
import org.kopitubruk.util.json.JSONUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static se.kth.castor.yasjf4j.JObjectImpl.shield;
import static se.kth.castor.yasjf4j.JObjectImpl.unshield;

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
					add(shield(el));
				}
			}
		} catch (Exception e) {
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
					add(shield(el));
				}
			}
		} catch (Exception e) {
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
			return unshield(get(i));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		try {
			set(i, shield(o));

		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		try {
			add( shield(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		remove(i);
	}

	@Override
	public String YASJF4J_toString() {
		return JSONUtil.toJSON(this);
	}
}
