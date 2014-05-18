package com.gpigc.core;

public class FileUtils {
	public static String getExpandedFilePath(String relativeFilePath) {
		return System.getProperty("one-jar.expand.dir") + "/" + relativeFilePath;
	}
}
