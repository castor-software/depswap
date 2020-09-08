package se.kth.assertteam.depswap;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class SwapTestDep {

	public static void main(String[] args) throws IOException {
		if(args.length < 4) {
			System.err.println("Usage: java -jar myjar.jar projectDir g:a:v g:a:v /path/to/libs ?r");
			System.exit(-1);
		}
		String projectDir = args[0];
		String targetedGAV = args[1];
		String replacementGAV = args[2];
		String pathToJars = args[3];

		if(args.length == 5) {
			//Restore
			for (File pom : Project.findFiles(projectDir, "/old_pom.xml")) {
				File savedPom = new File(pom.getParentFile(), "pom.xml");
				FileUtils.deleteQuietly(savedPom);
				FileUtils.moveFile(pom, savedPom);
			}
		} else {
			for (File pom : Project.findFiles(projectDir, "/pom.xml")) {
				File savedPom = new File(pom.getParentFile(), "old_pom.xml");
				FileUtils.deleteQuietly(savedPom);
				FileUtils.moveFile(pom, savedPom);
				String[] tgav = targetedGAV.split(":");
				String[] rgav = replacementGAV.split(":");
				try {
					Project.swapDependency(
							savedPom, new File(savedPom.getParentFile(), "pom.xml"),
							tgav[0], tgav[1], (tgav[2].equals("*") ? null : tgav[2]),
							rgav[0], rgav[1], rgav[2],
							pathToJars
					);
				} catch (TransformationFailedException e) {
					System.out.println("Pom: " + pom.getPath() + " does not include the targeted dependency. Restoring original pom.");
					FileUtils.moveFile(savedPom, pom);
				}
			}
		}
	}
}
