package se.kth.castor.yasjf4j;


import org.apache.johnzon.core.JsonProviderImpl;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import static se.kth.castor.yasjf4j.JObjectImpl.gen;
import static se.kth.castor.yasjf4j.JObjectImpl.shield;

public class JArrayImpl extends ArrayList implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			JsonProviderImpl provider = new JsonProviderImpl();
			JsonArray o = (JsonArray) provider.createParser(new StringReader(json)).getValue();
			for(Object el: o) {
				if(el instanceof JsonObject) {
					add(new JObjectImpl((JsonObject) el));
				} else if (el instanceof JsonArray) {
					add(new JArrayImpl((JsonArray) el));
				} else {
					add(el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(JsonArray json) throws JException {
		try {
			for(Object el: json) {
				if(el instanceof JsonObject) {
					add(new JObjectImpl((JsonObject) el));
				} else if (el instanceof JsonArray) {
					add(new JArrayImpl((JsonArray) el));
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
			return JObjectImpl.unshield(get(i));
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

	public JsonValue getAsJsonValue() {
		JsonProviderImpl provider = new JsonProviderImpl();
		JsonArrayBuilder ob = provider.createArrayBuilder();
		for(Object val: this) {
			if(val instanceof JObjectImpl) {
				ob.add(((JObjectImpl) val).getAsJsonValue());
			} else if (val instanceof JArrayImpl) {
				ob.add(((JArrayImpl) val).getAsJsonValue());
			} else {
				ob.add(shield(val));
			}
		}
		return ob.build();
	}

	@Override
	public String YASJF4J_toString() {
		StringWriter w = new StringWriter();
		JsonGenerator g = gen.createGenerator(w);
		g.write(getAsJsonValue());
		g.flush();
		return w.toString();
	}

}
