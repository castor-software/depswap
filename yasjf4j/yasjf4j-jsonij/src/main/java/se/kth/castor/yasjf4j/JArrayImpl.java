package se.kth.castor.yasjf4j;


import cc.plural.jsonij.JSON;

import java.util.ArrayList;

public class JArrayImpl extends ArrayList implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			Object a = JSON.parse(json);
			JSON.Array o = (JSON.Array) ((JSON) a).getRoot();
			for(Object el: o) {
				if(el instanceof JSON.Object) {
					add(new JObjectImpl((JSON.Object) el));
				} else if (el instanceof JSON.Array) {
					add(new JArrayImpl((JSON.Array) el));
				} else {
					add(JObjectImpl.unshield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(JSON.Array json) throws JException {
		try {
			for(Object el: json) {
				if(el instanceof JSON.Object) {
					add(new JObjectImpl((JSON.Object) el));
				} else if (el instanceof JSON.Array) {
					add(new JArrayImpl((JSON.Array) el));
				} else {
					add(JObjectImpl.unshield(el));
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
			return get(i);
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		try {
			set(i, o);

		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		try {
			add(o);
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
		StringBuilder b = new StringBuilder();
		b.append("[");
		boolean isFirst = true;
		for(Object val: this) {
			if(isFirst) isFirst = false;
			else b.append(",");
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
			} else {
				b.append(val.toString());
			}
		}
		b.append("]");
		return b.toString();
	}
}
