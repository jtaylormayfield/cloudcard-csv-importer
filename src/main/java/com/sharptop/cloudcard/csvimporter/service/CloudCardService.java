package com.sharptop.cloudcard.csvimporter.service;

import java.io.IOException;

public interface CloudCardService {
	
	public int request(String urlStr, String method, String token, String content) throws IOException;
}
