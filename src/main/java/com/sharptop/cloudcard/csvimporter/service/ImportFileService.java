package com.sharptop.cloudcard.csvimporter.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ImportFileService {
	
	public File[] loadFiles(String inputDir, String extFilter) throws FileNotFoundException;
	
	public List<String> getLines(File file) throws IOException;
	
	public void moveFile(File file, String targetDir) throws IOException;
	
	public void writeFile(String directory, String name, String... content) throws IOException;
}
