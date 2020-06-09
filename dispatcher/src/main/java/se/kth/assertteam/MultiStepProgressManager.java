package se.kth.assertteam;

import org.json.simple.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

public class MultiStepProgressManager implements ProgressManager {
	ConcurrentHashMap<String,Configuration> store = new ConcurrentHashMap<>();

	int workerCount = 0;
	@Override
	synchronized public void giveConfiguration(Configuration cfg, String hostName) {
		store.put(hostName, cfg);
	}

	@Override
	synchronized public void receiveConfigurationResults(String hostname, JSONObject result) {

		MultiStepConfiguration cfg = (MultiStepConfiguration) store.get(hostname);
		System.out.println("receiveConfigurationResults");
		cfg.addResult((String) result.get("step"), (JSONObject) result.get("result"));

		System.out.println("[" + hostname + "]" + cfg.toJSON().toJSONString());

		if(cfg.isFinished())  store.remove(hostname);

		System.out.println("receiveConfigurationResults done");
	}

	@Override
	synchronized public String getNewHostName() {
		String r = "Worker-" + workerCount;
		workerCount++;
		return r;
	}

	@Override
	synchronized public String getHtmlOverview() {
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
		stringBuilder.append("<td>Name</td><td>Host</td><td>Step</td>");
		stringBuilder.append("</thead>");

		for(String host: store.keySet()) {
			MultiStepConfiguration cfg = (MultiStepConfiguration) store.get(host);
			stringBuilder.append("<tr><td>" + cfg.getName() + "</td><td>" + host + "</td><td>" + cfg.getStepInprogress() + "</td></tr>");
		}
		stringBuilder.append("</table>");

		stringBuilder.append("</body>");

		stringBuilder.append("</html>");
		return stringBuilder.toString();
	}
}
