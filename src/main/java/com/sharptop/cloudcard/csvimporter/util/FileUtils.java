package com.sharptop.cloudcard.csvimporter.util;

import java.io.File;

public class FileUtils {
	
	public static String stripFileExtension(File file) {
    	if (file == null || file.isDirectory()) {
    		throw new IllegalArgumentException("file");
    	}
		
		return file.getName().replaceFirst("[.][^.]+$", "");
	}
}
