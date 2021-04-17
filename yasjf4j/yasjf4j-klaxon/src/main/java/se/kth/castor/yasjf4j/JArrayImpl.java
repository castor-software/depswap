package se.kth.castor.yasjf4j;



import com.beust.klaxon.JsonArray;
import com.beust.klaxon.JsonValue;
import com.beust.klaxon.Klaxon;
import com.beust.klaxon.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JArrayImpl extends ArrayList implements JArray {

	static Parser parser = new Parser();
	static Klaxon klaxon = new Klaxon();

	public JArrayImpl() {}

	public JArrayImpl(String json) throws JException {
		try {
			List a = (JsonArray) parser.parse(new StringBuilder(json));
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
		return unshield(get(i));
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
		return klaxon.toJsonString(this);
	}



	public static Object shield(Object o) {
		if (o instanceof JNull) return null;
		else return o;
	}

	public static Object unshield(Object o) {
		if (o == null) return JNull.getInstance();
		else return o;
	}
}
