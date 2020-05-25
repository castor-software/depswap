package se.kth.assertteam;

import org.json.simple.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

public class ProgressManager {
	ConcurrentHashMap<String,Configuration> store = new ConcurrentHashMap<>();

	int workerCount = 0;

	public void giveConfiguration(Configuration cfg, String hostName) {
		store.put(hostName, cfg);
	}

	public void receiveConfigurationResults(String hostname, JSONObject result) {
		store.remove(hostname);

		System.out.println("[" + hostname + "]" + result.toString());
	}

	public synchronized String getNewHostName() {
		String r = "Worker-" + workerCount;
		workerCount++;
		return r;
	}

	public String getHtmlOverview() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html>");
		stringBuilder.append("<header>");
		stringBuilder.append("<title>Paillasse Dashboard</title>");
		stringBuilder.append("</header>");


		stringBuilder.append("<body>");
		stringBuilder.append("<h2>Paillasse Dashboard</h2>");

		stringBuilder.append("<h3>Pending (" + store.size() + ")</h3>");

		stringBuilder.append("<table>");
		stringBuilder.append("<thead>");
		stringBuilder.append("<td>Name</td><td>Host</td>");
		stringBuilder.append("</thead>");

		for(String host: store.keySet()) {
			Configuration cfg = store.get(host);
			stringBuilder.append("<tr><td>" + cfg.getName() + "</td><td>" + host + "</td></tr>");
		}
		stringBuilder.append("</table>");

		stringBuilder.append("</body>");

		stringBuilder.append("</html>");
		return stringBuilder.toString();
	}
}
