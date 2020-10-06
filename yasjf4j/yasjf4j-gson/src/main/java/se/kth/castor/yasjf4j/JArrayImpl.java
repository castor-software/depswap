package se.kth.castor.yasjf4j;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class JArrayImpl extends JsonElement implements JArray {
	public JsonArray contained;

	public JArrayImpl() {
		contained = new JsonArray();
	}

	@Override
	public JsonElement deepCopy() {
		if (contained.size() != 0) {
			JArrayImpl result = new JArrayImpl();
			for (JsonElement element : contained) {
				result.contained.add(element.deepCopy());
			}
			return result;
		}
		return new JArrayImpl();
	}

	public JArrayImpl(String json) throws JException {
		contained = new JsonArray();
		try {
			Gson gson = new Gson();
			JsonArray a = gson.fromJson(json, JsonArray.class);
			for(Object el: a) {
				if(el instanceof JsonObject) {
					contained.add(new JObjectImpl((JsonObject) el));
				} else if (el instanceof JsonArray) {
					contained.add(new JArrayImpl((JsonArray) el));
				} else {
					contained.add((JsonElement) el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(JsonArray json) throws JException {
		contained = new JsonArray();
		try {
			for(Object el: json) {
				if(el instanceof JsonObject) {
					contained.add(new JObjectImpl((JsonObject) el));
				} else if (el instanceof JsonArray) {
					contained.add(new JArrayImpl((JsonArray) el));
				} else {
					contained.add((JsonElement) el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(List json) throws JException {
		contained = new JsonArray();
		try {
			for(Object el: json) {
				contained.add(JObjectImpl.toJSONValue(el));
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public boolean isJsonArray() {
		return contained.isJsonArray();
	}

	@Override
	public JsonArray getAsJsonArray() {
		return contained;
	}

	@Override
	public int YASJF4J_size() {
		return contained.size();
	}

	@Override
	public Object YASJF4J_get(int i) throws JException {
		try {
			return JObjectImpl.toObject(contained.get(i));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		try {
			contained.set(i, JObjectImpl.toJSONValue(o));

		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		try {
			contained.add(JObjectImpl.toJSONValue(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		contained.remove(i);
	}

	@Override
	public String YASJF4J_toString() {
		return contained.toString();
	}
}
