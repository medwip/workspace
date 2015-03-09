package com.guntzergames.medievalwipeout.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class VersionUtils {

	public static synchronized String getVersion(String path) throws IOException {

		File file = new File(path);

		FileReader inputFile = new FileReader(file);
		BufferedReader bufferReader = new BufferedReader(inputFile);

		String line = bufferReader.readLine();

		bufferReader.close();

		return line;

	}

	public static String getVersion(InputStream is) throws IOException {
		
		java.util.Scanner s = new java.util.Scanner(is);
		s.useDelimiter("\\A");
	    String ret = s.hasNext() ? s.next() : "";
	    s.close();
	    return ret;
		
	}
}
