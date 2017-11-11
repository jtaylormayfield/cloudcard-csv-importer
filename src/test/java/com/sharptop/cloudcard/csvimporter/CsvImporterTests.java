package com.sharptop.cloudcard.csvimporter;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import com.sharptop.cloudcard.csvimporter.service.CloudCardService;
import com.sharptop.cloudcard.csvimporter.service.ImportFileService;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
public class CsvImporterTests {
	
	@MockBean
	private CloudCardService cloudCardService;
	
	@MockBean
	private ImportFileService importFileService;

	private CsvImporter csvImporter;
	
	@BeforeClass
	public static void beforeClass() throws IllegalAccessException {
		CardHolder.setOrganizationId(33);
	}
	
	@Before
	public void beforeTest() {
		CardHolder.setHeader(new String[] { "dummy" });
		
		CsvImporterProperties props = new CsvImporterProperties();
		
		props.setAccessToken("something");
		props.setBaseUrl("someurl.com");
		props.setCompletedDir("A:\\dir");
		props.setInputDir("A:\\nother\\dir");
		props.setOrgIdKey(33);
		props.setReportDir("A:\\final\\dir");
		props.setTestMode(true);
		
		csvImporter = new CsvImporter(props, cloudCardService, importFileService);
	}
	
	@Test
	public void testExecute_validRecords() throws IOException {
		final String accessToken = "something";
		final String baseUrl = "someurl.com";
		final String completedDir = "A:\\dir";
		final String inputDir = "A:\\nother\\dir";
		final int orgIdKey = 33;
		final String reportDir = "A:\\final\\dir";
		final boolean testMode = false;
		
		final File inputFile1 = new File("somefile.csv");
		final File inputFile2 = new File("someotherfile.csv");
		
		final String header1 = "Email, PERSON_ID, Name, Type";
		final String header2 = "Email, PERSON_ID, Name";
		
		final String record1 = "email, 1, name, type";
		final String record2 = "email2, 2, \"last, first\"";
		
		CsvImporterProperties props = new CsvImporterProperties();
		
		props.setAccessToken(accessToken);
		props.setBaseUrl(baseUrl);
		props.setCompletedDir(completedDir);
		props.setInputDir(inputDir);
		props.setOrgIdKey(orgIdKey);
		props.setReportDir(reportDir);
		props.setTestMode(testMode);
		
		csvImporter = new CsvImporter(props, cloudCardService, importFileService);
		
		given(importFileService.loadFiles(Mockito.anyString(), Mockito.anyString()))
			.willReturn(new File[] { inputFile1, inputFile2 });
		
		given(importFileService.getLines((File)Mockito.any()))
			.willReturn(Lists.newArrayList(header1, record1))
			.willReturn(Lists.newArrayList(header2, record2));
		
		given(cloudCardService.request(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
			.willReturn(HttpURLConnection.HTTP_CREATED)
			.willReturn(HttpURLConnection.HTTP_CREATED);
		
		csvImporter.execute();
		
		then(importFileService).should(times(1))
			.loadFiles(inputDir, ".csv");
		
		then(importFileService).should(times(1))
			.getLines(inputFile1);

		CardHolder.setHeader(header1.split(CsvImporter.DELIMITER));
		then(cloudCardService).should(times(1))
			.request(baseUrl + "/api/people", "POST", accessToken, new CardHolder(CsvImporter.DELIMITER, record1).toJSON());
		
		then(importFileService).should(times(1))
			.moveFile(inputFile1, completedDir);
		
		then(importFileService).should(times(1))
			.getLines(inputFile2);

		CardHolder.setHeader(header2.split(CsvImporter.DELIMITER));
		then(cloudCardService).should(times(1))
			.request(baseUrl + "/api/people", "POST", accessToken, new CardHolder(CsvImporter.DELIMITER, record2).toJSON());
		
		then(importFileService).should(times(1))
			.moveFile(inputFile2, completedDir);
		
		then(importFileService).should(times(2))
			.writeFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
	}
	
	@Test
	public void testImportToCloudCard_Success() throws IOException {
		given(cloudCardService.request(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
			.willReturn(HttpURLConnection.HTTP_CREATED);
		
		String msg = csvImporter.importToCloudCard(new CardHolder());
		
		then(cloudCardService).should()
			.request(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
		assertThat(msg).isEqualTo("Success");
	}
	
	@Test
	public void testImportToCloudCard_Failure() throws IOException {
		given(cloudCardService.request(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
			.willReturn(HttpURLConnection.HTTP_ACCEPTED);
		
		String msg = csvImporter.importToCloudCard(new CardHolder());
		
		then(cloudCardService).should()
			.request(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		assertThat(msg).isEqualTo("Failed : HTTP error code : " + HttpURLConnection.HTTP_ACCEPTED);
	}
	
	@Test
	public void testImportToCloudCard_Exception() throws IOException {
		given(cloudCardService.request(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
			.willThrow(new IOException("some message"));
		
		String msg = csvImporter.importToCloudCard(new CardHolder());
		
		then(cloudCardService).should()
			.request(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		assertThat(msg).isEqualTo("Failed : " + IOException.class.toString() + " : some message");
	}
	
	@Test
	public void testImportToCloudCard_NullCardHolder() throws IOException {
		assertThatThrownBy(() -> csvImporter.importToCloudCard(null))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
