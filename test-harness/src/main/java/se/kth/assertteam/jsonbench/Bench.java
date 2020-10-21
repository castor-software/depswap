package se.kth.assertteam.jsonbench;


import se.kth.assertteam.jsonbench.parser.FastJson;
import se.kth.assertteam.jsonbench.parser.GsonParser;
import se.kth.assertteam.jsonbench.parser.Jackson;
import se.kth.assertteam.jsonbench.parser.JsonSimple;
import se.kth.assertteam.jsonbench.parser.OrgJSON;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Bench {
	public static void main(String[] args) throws IOException {

		JP orgJson = new OrgJSON();
		test(orgJson);

		JP gson = new GsonParser();
		test(gson);

		JP simple = new JsonSimple();
		test(simple);

		JP fastjson = new FastJson();
		test(fastjson);

		JP jackson = new Jackson();
		test(jackson);
	}

	public static void test(JP parser) throws IOException {
		File correct = new File("../data/bench/correct");
		File errored = new File("../data/bench/errored");
		File undefined = new File("../data/bench/undefined");
		System.out.println("Start testing " + parser.getName());
		printResults(parser, "correct", testAllCorrectJson(correct, parser));
		printResults(parser, "errored", testAllIncorrectJson(errored, parser));
		printResults(parser, "undefined", testAllCorrectJson(undefined, parser));

	}

	public static void printResults(JP parser, String category, Map<String,ResultKind> results) throws IOException {
		File dir = new File("results");
		File output = new File(dir,parser.getName() + "_" + category + "_results.csv");
		System.out.println("Print result from " + parser.getName() + " to " + output.getName());
		if(output.exists()) {
			output.delete();
		}
		output.createNewFile();
		try {
			Files.write(output.toPath(), "Parser,Category,File,Result\n".getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
		results.forEach((k,v) -> {
			String line = parser.getName() + "," + category + "," + k + "," + v + "\n";
			try {
				Files.write(output.toPath(), line.getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static List<File> findFiles(String dir, String suffix) throws IOException {
		List<File> files = new ArrayList<>();

		Files.walk(Paths.get(dir))
				.filter(Files::isRegularFile)
				.forEach((f)->{
					String file = f.toString();
					if( file.endsWith(suffix))
						files.add(new File(file));
				});

		return files;
	}

	public static String readFile(File f) throws IOException {
		return Files.lines(f.toPath(), Charset.forName("UTF-8")).collect(Collectors.joining("\n"));
	}

	public static Map<String,ResultKind> testAllCorrectJson(File inDir, JP parser) throws IOException {
		Map<String,ResultKind> results = new HashMap<>();
		for(File f: findFiles(inDir.getAbsolutePath(),".json")) {
			ResultKind r = testCorrectJson(f, parser);
			results.put(f.getName(), r);
		}
		return results;
	}

	public static Map<String,ResultKind> testAllIncorrectJson(File inDir, JP parser) throws IOException {
		Map<String,ResultKind> results = new HashMap<>();
		for(File f: findFiles(inDir.getAbsolutePath(),".json")) {
			ResultKind r = testIncorrectJson(f, parser);
			results.put(f.getName(), r);
		}
		return results;
	}

	public static ResultKind testCorrectJson(File f, JP parser)  {
		String jsonIn = null;
		try {
			jsonIn = readFile(f);
		} catch (Exception e) {
			return ResultKind.FILE_ERROR;
		}
		Object jsonObject = null;
		String jsonOut;
		try {
			try {
				jsonObject = parser.parseString(jsonIn);
				if(jsonObject == null)
					return ResultKind.NULL_OBJECT;
			} catch (Exception e) {
				return ResultKind.PARSE_EXCEPTION;
			}
			if(jsonObject != null) {
				try {
					jsonOut = parser.print(jsonObject);
					if(jsonOut.equalsIgnoreCase(jsonIn)) {
						return ResultKind.OK;
					}
					if(parser.equivalence(jsonObject,parser.parseString(jsonOut))) {
						return ResultKind.EQUIVALENT_OBJECT;
					} else {
						return ResultKind.NON_EQUIVALENT_OBJECT;
					}
				} catch (Exception e) {
					return ResultKind.PRINT_EXCEPTION;
				}
			}
		} catch (Error e) {
			return ResultKind.CRASH;
		}
		return null;
	}

	public static ResultKind testIncorrectJson(File f, JP parser)  {
		String jsonIn = null;
		try {
			jsonIn = readFile(f);
		} catch (Exception e) {
			return ResultKind.FILE_ERROR;
		}
		try {
			Object jsonObject = null;
			String jsonOut;
			try {
				try {
					jsonObject = parser.parseString(jsonIn);
					if (jsonObject != null)
						return ResultKind.UNEXPECTED_OBJECT;
					else
						return ResultKind.NULL_OBJECT;
				} catch (Exception e) {
					return ResultKind.OK;
				}
			} catch (Error e) {
				return ResultKind.CRASH;
			}
		} catch (Exception e) {
			return null;
		}
	}


	//read file
	//get jsons
	//parse test
	//print test
}
