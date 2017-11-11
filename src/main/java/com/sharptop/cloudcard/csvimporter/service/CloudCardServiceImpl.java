package com.sharptop.cloudcard.csvimporter.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.stereotype.Service;

@Service
public class CloudCardServiceImpl implements CloudCardService {

	@Override
	public int request(String urlStr, String method, String token, String content) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(urlStr).openConnection();
		
		try {
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("X-Auth-Token", token);
			connection.setRequestProperty("Accept", "application/json");

			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(content.getBytes());
			outputStream.flush();
			
			return connection.getResponseCode();
		} finally {
			connection.disconnect();
		}
	}
}
