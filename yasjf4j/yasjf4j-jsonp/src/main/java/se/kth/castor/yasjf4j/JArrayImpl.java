package se.kth.castor.yasjf4j;


import org.glassfish.json.WriterAccess;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class JArrayImpl extends ArrayList<JsonValue> implements JsonArray, JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			JsonReader jsonReader = Json.createReader(new StringReader(json));
			JsonArray o = jsonReader.readArray();
			for(JsonValue el: o) {
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
			for(JsonValue el: json) {
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

	public JArrayImpl(List l) throws JException {
		try {
			for(Object el: l) {
				if(el instanceof JsonObject) {
					add(new JObjectImpl((JsonObject) el));
				} else if (el instanceof JsonArray) {
					add(new JArrayImpl((JsonArray) el));
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
		return this.toString();
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		try (JsonWriter jw = new WriterAccess(sw, JObjectImpl.bufferPool)) {
			jw.write(this);
		}
		return sw.toString();
	}

	@Override
	public JsonObject getJsonObject(int i) {
		return (JsonObject) get(i);
	}

	@Override
	public JsonArray getJsonArray(int i) {
		return (JsonArray) get(i);
	}

	@Override
	public JsonNumber getJsonNumber(int i) {
		return (JsonNumber) get(i);
	}

	@Override
	public JsonString getJsonString(int i) {
		return (JsonString) get(i);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends JsonValue> List<T> getValuesAs(Class<T> aClass) {
		return (List<T>) this;
	}

	@Override
	public String getString(int i) {
		return ((JsonString) get(i)).getString();
	}

	@Override
	public String getString(int i, String s) {
		try {
			return getString(i);
		} catch (Exception e) {
			return s;
		}
	}

	@Override
	public int getInt(int i) {
		return ((JsonNumber) get(i)).intValue();
	}

	@Override
	public int getInt(int i, int i1) {
		try {
			return getInt(i);
		} catch (Exception e) {
			return i1;
		}
	}

	@Override
	public boolean getBoolean(int i) {
		JsonValue jsonValue = get(i);
		if (jsonValue == JsonValue.TRUE) {
			return true;
		} else if (jsonValue == JsonValue.FALSE) {
			return false;
		} else {
			throw new ClassCastException();
		}
	}

	@Override
	public boolean getBoolean(int i, boolean b) {
		try {
			return getBoolean(i);
		} catch (Exception e) {
			return b;
		}
	}

	@Override
	public boolean isNull(int i) {
		return get(i).equals(JsonValue.NULL);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.ARRAY;
	}
}
