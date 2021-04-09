package se.kth.castor.yasjf4j;


import mjson.JArrayImpl;
import mjson.JObjectImpl;
import mjson.Json;
import se.kth.castor.yasjf4j.spi.JFactoryProvider;

public class JFactoryProviderImpl implements JFactoryProvider {
	@Override
	public JObject createJObject() {
		try {
			return new JObjectImpl(Json.object());
		} catch (JException e) {
			return null;
		}
	}

	@Override
	public JArray createJArray() {
		try {
			return new JArrayImpl(Json.array());
		} catch (JException e) {
			return null;
		}
	}

	private char firstNonWhitChar(String str) {
		return str.trim().charAt(0);
	}

	@Override
	public Object parse(String s) throws JException {
		if(s == null || s.length() == 0) throw new JException();
		char first = firstNonWhitChar(s);
		if(first == '{') {
			try {
				return new JObjectImpl(s);
			} catch (JException e) {
				throw new JException();
			}
		} else if (first == '[') {
			try {
				return new JArrayImpl(s);
			} catch (JException e) {
				throw new JException();
			}
		} else {
			throw new JException();
		}
	}

	@Override
	public void initialize() {

	}
}
