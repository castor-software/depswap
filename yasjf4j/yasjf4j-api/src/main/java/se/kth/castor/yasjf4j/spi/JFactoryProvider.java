package se.kth.castor.yasjf4j.spi;

import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JObject;

public interface JFactoryProvider {

	JObject createJObject();

	JArray createJArray();

	Object parse(String raw) throws JException;

	void initialize();
}
