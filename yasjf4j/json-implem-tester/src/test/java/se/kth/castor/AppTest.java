package se.kth.castor;

import static org.junit.Assert.assertTrue;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.junit.Ignore;
import org.junit.Test;
import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JObject;

import java.io.File;
import java.io.FileReader;
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

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testImplem() throws Exception {
        test(getParser());
    }

    public static JP getParser() throws Exception {

        JP parser = new JP() {
            public String init() throws Exception {
                File pom = new File("pom.xml");
                String implemName = null;
                MavenXpp3Reader pomReader = new MavenXpp3Reader();
                try (FileReader reader = new FileReader(pom)) {
                    Model model = pomReader.read(reader);
                    Dependency target = null;

                    for(Dependency dependency :model.getDependencies()) {
                        String g = dependency.getGroupId();
                        String a = dependency.getArtifactId();

                        if(g.equals("se.kth.castor") && !a.equals("yasjf4j-api")) {
                            target = dependency;
                            break;
                        }
                    }

                    if(target == null) {
                        throw new Exception("Implem not found in targeted pom.");
                    }
                    implemName = target.getArtifactId();
                }
                return implemName;
            }

            @Override
            public Object parseString(String in) throws Exception {
                return JFactory.parse(in);
            }

            @Override
            public String print(JObject in) throws Exception {
                return in.YASJF4J_toString();
            }

            @Override
            public String print(JArray in) throws Exception {
                return in.YASJF4J_toString();
            }

            @Override
            public String print(Object in) throws Exception {
                if(in instanceof JObject) return print((JObject) in);
                else if(in instanceof JArray) return print((JArray) in);
                else return in.toString();
            }

            String name = init();

            @Override
            public String getName() {
                return name;
            }

            double epsilon = 0.00001d;

            @Override
            public boolean equivalence(Object o1, Object o2) {

                if(o1 == null) return o2 == null;
                if(o2 == null) return false;
                if(o1.equals(o2)) return true;

                if(o1 instanceof JObject) {
                    if(!(o2 instanceof JObject)) return false;
                    JObject jo1 = (JObject) o1;
                    JObject jo2 = (JObject) o2;
                    if(jo1.YASJF4J_getKeys().size() != jo2.YASJF4J_getKeys().size()) return false;
                    for(Object k: jo1.YASJF4J_getKeys()) {
                        try {
                            if (!jo2.YASJF4J_getKeys().contains(k) || !equivalence(jo1.YASJF4J_get(k.toString()), jo2.YASJF4J_get(k.toString())))
                                return false;
                        } catch (JException e) {
                            return false;
                        }
                    }
                    return true;
                }
                if(o2 instanceof JObject) return false;

                if(o1 instanceof JArray) {
                    if(!(o2 instanceof JArray)) return false;
                    JArray jo1 = (JArray) o1;
                    JArray jo2 = (JArray) o2;
                    if(jo1.YASJF4J_size() != jo2.YASJF4J_size()) return false;
                    for(int i = 0; i < jo1.YASJF4J_size(); i++) {
                        try {
                            if(!equivalence(jo1.YASJF4J_get(i), jo2.YASJF4J_get(i))) return false;
                        } catch (JException e) {
                            return false;
                        }
                    }
                    return true;
                }
                if(o2 instanceof JArray) return false;

                if(o1 instanceof Number) {
                    if(!(o2 instanceof Number)) return false;
                    Number n1 = (Number) o1;
                    Number n2 = (Number) o2;
                    if(Math.abs(n1.doubleValue() - n2.doubleValue()) <= epsilon) return true;
                    else return false;
                }

                return false;
            }
        };
        return parser;
    }




    public static void test(JP parser) throws IOException {
        File correct = new File("../../data/bench/correct");
        File errored = new File("../../data/bench/errored");
        File undefined = new File("../../data/bench/undefined");
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

    @Ignore
    @Test
    public void testSoloFile() throws Exception {
        File dir = new File("../../data/bench/correct");
        ResultKind res = testCorrectJson(new File(dir, "roundtrip10.json"), getParser());
        System.out.println("Res: " + res.toString());
    }
}
