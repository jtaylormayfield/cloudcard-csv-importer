package com.sharptop.cloudcard.csvimporter.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ImportFileServiceImpl implements ImportFileService {

	@Override
	public File[] loadFiles(String inputDir, String extFilter) throws FileNotFoundException {
		if (inputDir == null) {
			throw new IllegalArgumentException("inputDir");
		}
		if (extFilter == null) {
			throw new IllegalArgumentException("extFilter");
		}
		
		File dir = new File(inputDir);
		
		if (!dir.exists() || !dir.isDirectory()) {
			throw new FileNotFoundException("Input directory not found.");
		}
		
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir1, String name) {
				return name.endsWith(extFilter);
			}
		});
		
		return files;
	}

	@Override
	public List<String> getLines(File file) throws IOException {
    	if (file == null || file.isDirectory()) {
    		throw new IllegalArgumentException("file");
    	}
    	
    	return Files.lines(Paths.get(file.getAbsolutePath()))
    			.filter(line -> line != null)
    			.collect(Collectors.toList());
	}

	@Override
	public void moveFile(File file, String targetDir) throws IOException {
    	if (file == null) {
    		throw new IllegalArgumentException("file");
    	}
    	if (targetDir == null) {
    		throw new IllegalArgumentException("targetDir");
    	}
		
		Files.move(Paths.get(file.getAbsolutePath()), Paths.get(targetDir + "/" + file.getName()));
	}

	@Override
	public void writeFile(String directory, String name, String... content) throws IOException {
		if (directory == null) {
			throw new IllegalArgumentException("directory");
		}
		if (name == null) {
			throw new IllegalArgumentException("name");
		}
		
		File file = new File(directory + "/" + name);

		if (!file.exists()) {
			file.createNewFile();
		}
		
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
		
		for (String stuff : content) {
			bufferedWriter.write(stuff);	
		}

		bufferedWriter.close();
	}

}
