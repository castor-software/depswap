package mjson;

import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;

import java.util.List;
import java.util.Map;

import static mjson.JObjectImpl.shield;
import static mjson.JObjectImpl.unshield;

public class JArrayImpl extends Json.ArrayJson implements JArray {

	public JArrayImpl(String json) throws JException {
		try {
			Json a = Json.read(json);
			fill(a.asList());
		} catch (Exception e) {
			throw new JException();
		}
	}

	public void fill(List a) throws JException {
		try {
			for(Object el: a) {
				if(el instanceof Json) {
					if (((Json) el).isObject())
						add(new JObjectImpl((Json) el));
					else if (((Json) el).isArray())
						add(new JArrayImpl((Json) el));
					else
						add(el);
				} else if(el instanceof List) {
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
		fill(json);
	}

	public JArrayImpl(Json json) throws JException {
		fill(json.asList());
	}

	@Override
	public int YASJF4J_size() {
		return asList().size();
	}

	@Override
	public Object YASJF4J_get(int i) throws JException {
		Object o = at(i);
		if(o instanceof NumberJson) {
			return ((NumberJson) o).val;
		} else if(o instanceof StringJson) {
			return ((StringJson) o).asString();
		}
		return unshield(o);
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		set(i, shield(o));
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		add( shield(o));
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