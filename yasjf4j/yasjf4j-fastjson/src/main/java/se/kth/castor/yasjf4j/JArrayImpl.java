package se.kth.castor.yasjf4j;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.serializer.SerializerFeature;

import static se.kth.castor.yasjf4j.JObjectImpl.shield;
import static se.kth.castor.yasjf4j.JObjectImpl.unshield;

public class JArrayImpl extends JSONArray implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {
			DefaultJSONParser p = new DefaultJSONParser(json);
			JSONArray a = (JSONArray) p.parse();
			for(Object el: a) {
				if(el instanceof JSONObject) {
					add(new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					add(new JArrayImpl((JSONArray) el));
				} else {
					add(shield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(JSONArray json) throws JException {
		try {
			for(Object el: json) {
				if(el instanceof JSONObject) {
					add(new JObjectImpl((JSONObject) el));
				} else if (el instanceof JSONArray) {
					add(new JArrayImpl((JSONArray) el));
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
		return JSONArray.toJSONString(this, SerializerFeature.WriteMapNullValue);
	}

	@Override protected void finalize() {
		System.out.println(this + " was finalized!");
	}
}
