package se.kth.assertteam.depswap;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

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

}