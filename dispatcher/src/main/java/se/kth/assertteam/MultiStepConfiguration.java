package se.kth.assertteam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public abstract class MultiStepConfiguration implements Configuration {

	boolean isFinished() {
		return ((JSONArray) data.get("steps")).size() ==  ((JSONArray) results.get("steps")).size();
	}

	synchronized public void  addResult(String step, JSONObject result) {
		System.out.println("addResult");
		result.put("step", step);
		((JSONArray) results.get("steps")).add(result);
		//stepInProgress = step;
		System.out.println("done");
	}

	synchronized public String getStepInprogress() {
		return ((String) ((JSONObject) ((JSONArray) data.get("steps")).get(((JSONArray) results.get("steps")).size())).get("step"));
		//return stepInProgress;
	}

	public JSONObject data = new JSONObject();
	public JSONObject results = new JSONObject();
	public String name;
	//String stepInProgress;

	@Override
	public JSONObject toJSON() {
		return data;
	}

	@Override
	public String getName() {
		return name;
	}
}
