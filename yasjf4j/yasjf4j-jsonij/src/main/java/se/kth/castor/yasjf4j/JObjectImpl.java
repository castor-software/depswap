package se.kth.castor.yasjf4j;



import cc.plural.jsonij.JSON;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Set;

public class JObjectImpl extends HashMap<String, Object> implements JObject {

	public JObjectImpl() {
	}

	public JObjectImpl(JSON.Object json) throws JException {
		try {
			for(Object key: json.keySet()) {
				Object el = json.get(key);
				if(el instanceof JSON.Object) {
					put(key.toString(), new JObjectImpl((JSON.Object) el));
				} else if (el instanceof JSON.Array) {
					put(key.toString(), new JArrayImpl((JSON.Array) el));
				} else {
					put(key.toString(), unshield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			Object a = JSON.parse(json);
			JSON.Object o = (JSON.Object) ((JSON) a).getRoot();
			for(Object key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof JSON.Object) {
					put(key.toString(), new JObjectImpl((JSON.Object) el));
				} else if (el instanceof JSON.Array) {
					put(key.toString(), new JArrayImpl((JSON.Array) el));
				} else {
					put(key.toString(), unshield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return keySet();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		if(!containsKey(s)) throw new JException();
		try {
			return get(s);
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			put(s, unshield(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		remove(s);
	}

	@Override
	public String YASJF4J_toString() {
		StringBuilder b = new StringBuilder();
		b.append("{");
		boolean isFirst = true;
		for(String key: keySet()) {
			if(isFirst) isFirst = false;
			else b.append(",");
			b.append("\"" + key + "\":");
			Object val = get(key);
			if(val instanceof JObjectImpl) {
				b.append(((JObjectImpl) val).YASJF4J_toString());
			} else if(val instanceof JArrayImpl) {
				b.append(((JArrayImpl) val).YASJF4J_toString());
			} else if(val instanceof String) {
				b.append("\"" + val.toString() + "\"");
			} else if(val instanceof Character) {
				b.append("\"" + val.toString() + "\"");
			} else if(val == null) {
				b.append("null");
			} else if(val == JNull.getInstance()) {
				b.append("null");
			} else {
				b.append(val.toString());
			}
		}
		b.append("}");
		return b.toString();
	}


	public static Object unshield(Object o) {
		if(o == JSON.NULL) {
			return JNull.getInstance();
		} else if(o == JSON.FALSE) {
			return false;
		} else if(o == JSON.TRUE) {
			return true;
		} else if(o instanceof JSON.Numeric) {
			return ((JSON.Numeric) o).getNumber().getNumber();
		} else if(o instanceof JSON.String) {
			return ((JSON.String) o).getString();
		}
		return o;
	}
}
