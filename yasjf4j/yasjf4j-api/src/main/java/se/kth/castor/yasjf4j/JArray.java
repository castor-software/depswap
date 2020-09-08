package se.kth.castor.yasjf4j;

public interface JArray {

	int YASJF4J_size();

	Object YASJF4J_get(int i) throws JException;

	void YASJF4J_set(int i, Object o) throws JException;

	void YASJF4J_add(Object o) throws JException;

	void YASJF4J_remove(int i) throws JException;

	String YASJF4J_toString();
}
