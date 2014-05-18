package com.gpigc.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Config {
	private Path applicationDataDirectory;
	
	public Config(String defaultConfigDirectory) throws ConfigException {
		applicationDataDirectory = generateApplicationDataDirectory();
		
		try {
			if (!Files.exists(applicationDataDirectory)) {
				Files.createDirectory(applicationDataDirectory);
			}
			
			Path defaultConfigDirectoryPath = Paths.get(defaultConfigDirectory);
			
			Files.walkFileTree(defaultConfigDirectoryPath, new CopyFileVisitor(defaultConfigDirectoryPath, applicationDataDirectory));
		} catch (IOException e) {
			throw new ConfigException("IO Error", e);
		}
	}
	
	public File getConfigFile(String fileName) {
		return applicationDataDirectory.resolve(fileName).toFile();
	}
	
	private Path getApplicationDataDirectory() {
		return applicationDataDirectory;
	}
	
	private Path generateApplicationDataDirectory() throws ConfigException
	{
	    String os = System.getProperty("os.name").toUpperCase();
	    
	    String applicationDataDirectory = new String();
	    
	    if (os.contains("WIN"))
	        applicationDataDirectory = System.getenv("APPDATA") ;
	    else if (os.contains("MAC"))
	    	applicationDataDirectory = System.getProperty("user.home") + "/Library/Application "
	                + "Support";
	    else if (os.contains("NUX"))
	    	applicationDataDirectory = System.getProperty("user.home");
	    else
	    	applicationDataDirectory = System.getProperty("user.dir");
	    
	    if (applicationDataDirectory.isEmpty()) {
	    	throw new ConfigException("Could not determine application data directory");
	    }
	    
	    applicationDataDirectory += "/.gpigc";
	    
	    return Paths.get(applicationDataDirectory);
	}
	
	public class ConfigException extends Exception {
		private static final long serialVersionUID = 1L;

		public ConfigException() {
		}

		public ConfigException(String message) {
			super(message);
		}

		public ConfigException(String message, Exception cause) {
			super(message, cause);
		}
	}
	
	private class CopyFileVisitor extends SimpleFileVisitor<Path> {
		private Path copyFromDirectory;
		private Path copyToDirectory;
		
		public CopyFileVisitor(Path copyFromDirectory, Path copyToDirectory) {
			super();
			this.copyFromDirectory = copyFromDirectory;
			this.copyToDirectory = copyToDirectory;
		}

		@Override
		public FileVisitResult visitFile(Path fileFrom,
				BasicFileAttributes attr) throws IOException {
			Path relativeFilePath = copyFromDirectory.relativize(fileFrom);
			Path fileTo =  copyToDirectory.resolve(relativeFilePath);
			
			if (!Files.exists(fileTo)) {
				Files.copy(fileFrom, fileTo);
			}
			
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path directoryFrom, BasicFileAttributes attrs)
				throws IOException {
			if (directoryFrom != copyFromDirectory) {
				Path relativeFilePath = copyFromDirectory.relativize(directoryFrom);
				Path directoryTo = copyToDirectory.resolve(relativeFilePath);

				if (!Files.exists(directoryTo)) {
					Files.copy(directoryFrom, directoryTo);
				}
			}
			return FileVisitResult.CONTINUE;
		}
	}
}
