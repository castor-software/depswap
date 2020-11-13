package se.kth.assertteam.depswap;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Ignore;
import org.junit.Test;
import se.kth.assertteam.jsonbench.ResultKind;
import se.kth.assertteam.jsonbench.parser.FastJson;
import se.kth.assertteam.jsonbench.parser.GsonParser;

import java.io.File;

import static org.junit.Assert.*;
import static se.kth.assertteam.jsonbench.Bench.testCorrectJson;

public class ProjectTest {

	@Test
	public void testPomTransform() throws TransformationFailedException {

		File pom = new File("./src/test/resources/dummies/jsonuser/pom.xml");
		File out = new File("./transformedPom.xml");

		String inG = "org.json";
		String inA = "json";
		String inV = null;

		String outG = "se.kth";
		String outA = "json-over-gson";
		String outV = "1.0-SNAPSHOT";

		Project.swapDependency(pom, out, inG, inA, inV, outG, outA, outV, "./lib");

		System.out.println("Done");
	}


	@Ignore
	@Test
	public void testSoloFile() {
		File dir = new File("../data/bench/correct");
		ResultKind res = testCorrectJson(new File(dir, "roundtrip10.json"), new GsonParser());
		System.out.println("Res: " + res.toString());

	}


	@Ignore
	@Test
	public void testFastJsonNull() {
		String own = "{\"a\":null}";

		DefaultJSONParser p = new DefaultJSONParser(own);
		Object o = p.parse();
		System.out.println(o.toString());
	}


	@Ignore
	@Test
	public void testGsonNull() {
		String own = "{\"a\":null}";

		JsonParser parser = new JsonParser();
		JsonElement o = parser.parse(own);
		System.out.println(o.toString());
	}

}