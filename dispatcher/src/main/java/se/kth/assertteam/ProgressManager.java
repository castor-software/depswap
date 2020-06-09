package se.kth.assertteam;

import org.json.simple.JSONObject;

public interface ProgressManager {

	void giveConfiguration(Configuration cfg, String hostName);

	void receiveConfigurationResults(String hostname, JSONObject result);

	String getNewHostName();

	String getHtmlOverview();
}
