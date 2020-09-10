package se.kth.castor.yasjf4j;


import se.kth.castor.yasjf4j.spi.JFactoryProvider;

public class JFactoryProviderImpl implements JFactoryProvider {
	@Override
	public JObject createJObject() {
		return new JObjectImpl();
	}

	@Override
	public JArray createJArray() {
		return new JArrayImpl();
	}

	private char firstNonWhitChar(String str) {
		return str.trim().charAt(0);
	}

	@Override
	public Object parse(String s) throws JException {
		if(s == null || s.length() == 0) throw new JException();
		char first = firstNonWhitChar(s);
		if(first == '{') {
			return new JObjectImpl(s);
		} else if (first == '[') {
			return new JArrayImpl(s);
		} else {
			throw new JException();
		}
	}

	@Override
	public void initialize() {

	}
}
