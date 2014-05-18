package uk.co.gpigc.emitterlauncher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
	public static String getExpandedFilePath(String relativeFilePath) {
		return System.getProperty("one-jar.expand.dir") + "/" + relativeFilePath;
	}
	public static String readString(String stringFilename) throws IOException {
		String str = new String(Files.readAllBytes(Paths.get(stringFilename)));
		return str;
	}
}
