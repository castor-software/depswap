package se.kth.castor.yasjf4j;


import net.sf.corn.converter.json.JsTypeComplex;
import net.sf.corn.converter.json.JsTypeList;
import net.sf.corn.converter.json.JsTypeObject;
import net.sf.corn.converter.json.JsonStringParser;

import java.util.ArrayList;
import java.util.List;

public class JArrayImpl extends JsTypeList implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			JsTypeList o = (JsTypeList) JsonStringParser.parseJsonString(json);
			for(int i = 0; i < o.size(); i++) {
				JsTypeObject el = o.get(i);
				if(el instanceof JsTypeComplex) {
					add(new JObjectImpl((JsTypeComplex) el));
				} else if (el instanceof JsTypeList) {
					add(new JArrayImpl((JsTypeList) el));
				} else {
					add(JObjectImpl.shield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(JsTypeList json) throws JException {
		try {
			for(int i = 0; i < json.size(); i++) {
				JsTypeObject el = json.get(i);
				if(el instanceof JsTypeComplex) {
					add(new JObjectImpl((JsTypeComplex) el));
				} else if (el instanceof JsTypeList) {
					add(new JArrayImpl((JsTypeList) el));
				} else {
					add(JObjectImpl.shield(el));
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
			return JObjectImpl.unshield(get(i));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		List<JsTypeObject> toReAdd = new ArrayList();
		if(i < 0) throw new JException();
		if(i >= size()) add(JObjectImpl.shield(o));
		else {
			for(int j = i+1; j < size(); j++) {
				toReAdd.add(get(j));
			}
			for(int j =  size()-1; j >= i; j--) {
				remove(j);
			}
			add(JObjectImpl.shield(o));
			for(JsTypeObject a: toReAdd) {
				add(a);
			}
		}
		/*try {
			set(i, o);

		} catch (Exception e) {
			throw new JException();
		}*/
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		try {
			add(JObjectImpl.shield(o));
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
		return toString();
	}
}
