package se.kth.castor.yasjf4j;

import java.util.Set;

public interface JObject {

	Set<String> YASJF4J_getKeys();

	Object YASJF4J_get(String key) throws JException;

	void YASJF4J_put(String key, Object value) throws JException;

	void YASJF4J_remove(String key) throws JException;

	String YASJF4J_toString();

}
