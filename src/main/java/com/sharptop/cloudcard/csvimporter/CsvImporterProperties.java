package com.sharptop.cloudcard.csvimporter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class CsvImporterProperties {

	private String accessToken;
	private String baseUrl;
	private String completedDir;
	private String inputDir;
	private int orgIdKey;
	private String reportDir;
	private boolean testMode;
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getCompletedDir() {
		return completedDir;
	}

	public void setCompletedDir(String completedDir) {
		this.completedDir = completedDir;
	}

	public String getInputDir() {
		return inputDir;
	}

	public void setInputDir(String inputDir) {
		this.inputDir = inputDir;
	}

	public int getOrgIdKey() {
		return orgIdKey;
	}

	public void setOrgIdKey(int orgIdKey) {
		this.orgIdKey = orgIdKey;
	}

	public String getReportDir() {
		return reportDir;
	}

	public void setReportDir(String reportDir) {
		this.reportDir = reportDir;
	}

	public boolean isTestMode() {
		return testMode;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	@Override
	public String toString() {
		return "CsvImporterProperties [accessToken=" + accessToken + ", baseUrl=" + baseUrl + ", completedDir="
				+ completedDir + ", inputDir=" + inputDir + ", orgIdKey=" + orgIdKey + ", reportDir=" + reportDir
				+ ", testMode=" + testMode + "]";
	}
}
