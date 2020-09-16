package se.kth.castor.yasjf4j;


import org.yuanheng.cookjson.TextJsonGenerator;
import org.yuanheng.cookjson.TextJsonParser;
import org.yuanheng.cookjson.value.CookJsonArray;
import org.yuanheng.cookjson.value.CookJsonObject;

import javax.json.JsonValue;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class JArrayImpl extends CookJsonArray implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			TextJsonParser p = new TextJsonParser(new StringReader(json));
			p.next();
			CookJsonArray o = (CookJsonArray) p.getValue();
			for(JsonValue el: o) {
				if(el instanceof CookJsonObject) {
					add(new JObjectImpl((CookJsonObject) el));
				} else if (el instanceof CookJsonArray) {
					add(new JArrayImpl((CookJsonArray) el));
				} else {
					add(el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(CookJsonArray json) throws JException {
		try {
			for(JsonValue el: json) {
				if(el instanceof CookJsonObject) {
					add(new JObjectImpl((CookJsonObject) el));
				} else if (el instanceof CookJsonArray) {
					add(new JArrayImpl((CookJsonArray) el));
				} else {
					add(el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(List l) throws JException {
		try {
			for(Object el: l) {
				if(el instanceof CookJsonObject) {
					add(new JObjectImpl((CookJsonObject) el));
				} else if (el instanceof CookJsonArray) {
					add(new JArrayImpl((CookJsonArray) el));
				} else {
					add(JObjectImpl.toJSONValue(el));
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
			return JObjectImpl.toObject(get(i));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		try {
			set(i, JObjectImpl.toJSONValue(o));

		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		try {
			add(JObjectImpl.toJSONValue(o));
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
		StringWriter a = new StringWriter();
		TextJsonGenerator g = new TextJsonGenerator(a);
		g.write(this);
		g.flush();
		return a.toString();
	}
}
