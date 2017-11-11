package com.sharptop.cloudcard.csvimporter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sharptop.cloudcard.csvimporter.service.CloudCardService;
import com.sharptop.cloudcard.csvimporter.service.ImportFileService;

@SpringBootApplication
public class CsvImporterApplication implements CommandLineRunner {

	@Autowired
	private CloudCardService cloudCardService;
	
	@Autowired
	private CsvImporterProperties csvImporterProperties;
	
	@Autowired
	private ImportFileService importFileService;

	@Override
	public void run(String... args) throws IllegalAccessException, IOException {
		// TODO: This is stupid
		CardHolder.setOrganizationId(csvImporterProperties.getOrgIdKey());
		
		new CsvImporter(csvImporterProperties, cloudCardService, importFileService).execute();
	}

	public static void main(String[] args) {
		SpringApplication.run(CsvImporterApplication.class, args);
	}
}
