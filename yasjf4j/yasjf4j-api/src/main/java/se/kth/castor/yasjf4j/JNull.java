package se.kth.castor.yasjf4j;

public class JNull {
	static JNull instance = new JNull();

	public static JNull getInstance() { return instance;}

	public String toString() { return "null";}


}
