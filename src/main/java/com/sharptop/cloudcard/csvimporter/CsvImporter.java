package com.sharptop.cloudcard.csvimporter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sharptop.cloudcard.csvimporter.service.CloudCardService;
import com.sharptop.cloudcard.csvimporter.service.ImportFileService;
import com.sharptop.cloudcard.csvimporter.util.FileUtils;

public class CsvImporter {

	public static final String REPORT_FILE_NAME_DATE_FORMAT = "yyyy-MM-dd HH" + "\u02f8" + "mm" + "\u02f8" + "ss";
	public static final String DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
	
	private static final Logger log = LoggerFactory.getLogger(CsvImporter.class);
	
	private final CsvImporterProperties properties;
	private final CloudCardService cloudCardService;
	private final ImportFileService importFileService;

	public CsvImporter(
			CsvImporterProperties properties,
			CloudCardService cloudCardService,
			ImportFileService importFileService) {
		this.properties = properties;
		this.cloudCardService = cloudCardService;
		this.importFileService = importFileService;
	}
	
	public void execute() throws IOException {
		File[] files = importFileService.loadFiles(this.properties.getInputDir(), ".csv");
		
		if (files.length == 0) {
			log.warn("No input files found.");
		} else {
			for (File inputFile : files) {
				if (this.properties.isTestMode()) {
					log.info("Test mode detected. Would have imported " + inputFile.getName() + " to CC...");
				} else {
					log.info("Importing " + inputFile.getName() + " to CC...");
					saveCardHolders(convertFileToLines(inputFile).stream()
							.map(line -> new CardHolder(DELIMITER, line))
							.collect(Collectors.toList()), inputFile);
				}

				importFileService.moveFile(inputFile, this.properties.getCompletedDir());
			}
		}
	}

	protected void saveCardHolders(List<CardHolder> cardHolders, File inputFile) {
		StringBuilder content = new StringBuilder("Status, " + CardHolder.csvHeader() + "\n");
		
		for (CardHolder cardHolder : cardHolders) {
			if (cardHolder.validate()) {
				content.append(importToCloudCard(cardHolder));
			} else {
				content.append("Failed validation");
			}
			
			content.append(", " + cardHolder + "\n");
		}
		
		try {
			importFileService.writeFile(this.properties.getReportDir(), createReportFileName(inputFile), content.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String importToCloudCard(CardHolder cardHolder) {
		if (cardHolder == null) {
			throw new IllegalArgumentException("cardholder");
		}
		
		String msg = null;
		
		try {
			int responseCode = cloudCardService.request(
					this.properties.getBaseUrl() + "/api/people",
					"POST",
					this.properties.getAccessToken(),
					cardHolder.toJSON());
			
			if (responseCode != HttpURLConnection.HTTP_CREATED) {
				msg = "Failed : HTTP error code : " + responseCode;
			} else {
				msg = "Success";
			}
		} catch (IOException e) {
			msg = "Failed : " + e.getClass().toString() + " : " + e.getMessage();
		}
		
		return msg;
	}

	protected List<String> convertFileToLines(File inputFile) throws IOException {
		List<String> lines = null;
		try {
			lines = importFileService.getLines(inputFile);
			CardHolder.setHeader(lines.get(0).split(DELIMITER));
		} catch (IOException e) {
			importFileService.writeFile(
					this.properties.getReportDir(),
					createReportFileName(inputFile),
					"Failed to read input file \n",
					e.getMessage());
			
			throw new IOException(e);
		}
		return lines.subList(1, lines.size());
	}

	protected static String createReportFileName(File inputFile) {
		return FileUtils.stripFileExtension(inputFile)
				+ "-"
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern(REPORT_FILE_NAME_DATE_FORMAT))
				+ "-Report.csv";
	}
}
