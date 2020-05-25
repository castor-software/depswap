package se.kth.assertteam;

import org.json.simple.JSONObject;

public interface Configuration {
	JSONObject toJSON();

	String getName();
}
