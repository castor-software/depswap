package se.kth;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws JSONException {
        JSONObject o = CreateJson.createNestedJSONObject();
        JSONObject o2 = CreateJson.createNestedJSONObject();
        m();
        System.out.println( "Hello  " + o.toString());
    }

    public static void m() throws JSONException {
        JSONObject o2 = CreateJson.createNestedJSONObject();

    }
}
