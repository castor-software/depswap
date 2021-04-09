package se.kth.castor.yasjf4j;


import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import java.util.ArrayList;

import static se.kth.castor.yasjf4j.JObjectImpl.shield;
import static se.kth.castor.yasjf4j.JObjectImpl.unshield;

public class JArrayImpl extends ArrayList implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			//JSONObject o = new JSONObject(json);
			Object[] o = (Object[]) JsonReader.jsonToJava(json, JObjectImpl.customReadArgs);
			for(int i = 0; i < o.length; i++) {
				Object el = o[i];
				if(el instanceof JsonObject) {
					JsonObject jo = ((JsonObject) el);
					add(new JObjectImpl(jo));
				} else if (el == null) {
					add(shield(el));
				} else if (el.getClass().isArray()) {
					add(new JArrayImpl((Object[]) el));
				} else {
					add(el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(Object[] json) throws JException {
		try {
			for(int i = 0; i < json.length; i++) {
				Object el = json[i];
				if(el instanceof JsonObject) {
					JsonObject jo = ((JsonObject) el);
					add(new JObjectImpl(jo));
				} else if (el == null) {
					add(shield(el));
				} else if (el.getClass().isArray()) {
					add(new JArrayImpl((Object[]) el));
				} else {
					add(el);
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
			add(shield(o));
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
		return JsonWriter.objectToJson(this, JObjectImpl.customPrintArgs);
	}

}
