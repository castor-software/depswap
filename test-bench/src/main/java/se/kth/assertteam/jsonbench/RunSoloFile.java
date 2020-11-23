package se.kth.assertteam.jsonbench;

import se.kth.assertteam.jsonbench.parser.Argo;
import se.kth.assertteam.jsonbench.parser.CookJson;
import se.kth.assertteam.jsonbench.parser.Corn;
import se.kth.assertteam.jsonbench.parser.FastJson;
import se.kth.assertteam.jsonbench.parser.FlexJson;
import se.kth.assertteam.jsonbench.parser.GensonP;
import se.kth.assertteam.jsonbench.parser.GsonParser;
import se.kth.assertteam.jsonbench.parser.Johnzon;
import se.kth.assertteam.jsonbench.parser.JsonIJ;
import se.kth.assertteam.jsonbench.parser.OrgJSON;
import se.kth.assertteam.jsonbench.parser.ProgsBaseJson;

import java.io.File;
import java.io.IOException;

import static se.kth.assertteam.jsonbench.Bench.test;
import static se.kth.assertteam.jsonbench.Bench.testCorrectJson;

public class RunSoloFile {

	public static void main(String[] args) throws IOException {
		File correct = new File("../data/bench/correct");
		File errored = new File("../data/bench/errored");
		File undefined = new File("../data/bench/undefined");

		//File toTest = new File(correct, "y_structure_string_empty.json");
		//File toTest = new File(correct, "org-json-pass1.json");
		//File toTest = new File(correct, "y_structure_lonely_string.json");
		//File toTest = new File(correct, "y_string_space.json");
		//File toTest = new File(correct, "pass01.json");
		//File toTest = new File(correct, "y_structure_lonely_false.json");
		//File toTest = new File(undefined, "i_number_huge_exp.json");
		//JP parser = new FastJson();
		File toTest = new File(correct, "y_number_int_with_exp.json");

		//JP parser = new OrgJSON();
		//JP parser = new FlexJson();
		JP parser = new ProgsBaseJson();
		ResultKind r = testCorrectJson(toTest, parser);


		System.out.println("Result: " + r.toString());
	}
}
