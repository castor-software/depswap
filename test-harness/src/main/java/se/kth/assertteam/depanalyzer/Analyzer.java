package se.kth.assertteam.depanalyzer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Analyzer {


	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			System.err.println("Usage: java -jar myJar.jar pathToAnalyze(,pathToAnalyze) package(,package)");
			System.exit(-1);
		}

		HashSet<String> packagesToLookFor = new HashSet<>();
		String[] packs = args[1].split(",");
		for(String p: packs) {
			packagesToLookFor.add(p.replace(".", "/"));
		}

		LibrariesUsage librariesUsages = new LibrariesUsage(packagesToLookFor);

		String[] pathsToAnalyze = args[0].split(",");
		for(String pathToAnalyze: pathsToAnalyze) {
			if(pathToAnalyze.endsWith(".jar")) {
				processJar(new JarFile(pathToAnalyze), librariesUsages);
			} else {
				prcessDir(pathToAnalyze, librariesUsages);
			}
		}

		//Print usages

		System.out.println(librariesUsages.toJSONString());
	}

	public static void processJar(JarFile jarFile, LibrariesUsage librariesUsages) throws IOException {
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			if (entryName.endsWith(".class")) {
				//System.out.println("process: " + entry.getName());
				InputStream classFileInputStream = jarFile.getInputStream(entry);
				inputStreamProcess(classFileInputStream, librariesUsages);
			}
		}
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

	public static void prcessDir(String pathToDir, LibrariesUsage librariesUsages) throws IOException {
		for(File classFile: findFiles(pathToDir, ".class")) {
			//System.out.println("process: " + classFile.getPath());
			inputStreamProcess(new FileInputStream(classFile), librariesUsages);
		}
	}

	public static void inputStreamProcess(InputStream is, LibrariesUsage librariesUsages) throws IOException {
		try {
			ClassReader cr = new ClassReader(is);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			ClassAdapter ca = new ClassAdapter(cw, librariesUsages);

			cr.accept(ca, 0);
		} finally {
			is.close();
		}
	}
}
