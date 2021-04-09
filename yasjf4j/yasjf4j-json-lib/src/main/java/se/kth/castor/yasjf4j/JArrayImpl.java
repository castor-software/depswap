package se.kth.castor.yasjf4j;



import org.kordamp.json.JSONArray;
import org.kordamp.json.util.JSONUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static se.kth.castor.yasjf4j.JObjectImpl.shield;
import static se.kth.castor.yasjf4j.JObjectImpl.unshield;

public class JArrayImpl extends ArrayList implements JArray {
	public JArrayImpl() {}

	public JArrayImpl(String json) throws JException {
		try {
			List a = JSONArray.fromObject(json);
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
		for(Object el: json) {
			if(el instanceof Map) {
				add(new JObjectImpl((Map) el));
			} else if (el instanceof List) {
				add(new JArrayImpl((List) el));
			} else {
				add(shield(el));
			}
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
		set(i, shield(o));
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		add(shield(o));
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		remove(i);
	}

	@Override
	public String YASJF4J_toString() {
		return JSONArray.fromObject(this).toString();
	}
}
