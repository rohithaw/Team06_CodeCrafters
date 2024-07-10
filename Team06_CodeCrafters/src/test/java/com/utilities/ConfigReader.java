package com.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;



public class ConfigReader {

	private static Properties properties;
	private static final String propertyFilePath = "src/test/resources/Config/config.properties";

	public static void loadProperty() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(propertyFilePath));
			properties = new Properties();
			try {
				properties.load(reader);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Configuration.properties not found at " + propertyFilePath);
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

	public static long getImplicitWait() {
		String implicitlyWait = properties.getProperty("implicitWait");
		if (implicitlyWait != null)
			return Long.parseLong(implicitlyWait);
		else
			throw new RuntimeException("implicitlyWait not specified in the config.properties file.");
	}

	public static long getPageLoadWait() {
		String pageLoadWait = properties.getProperty("pageLoadWait");
		if (pageLoadWait != null)
			return Long.parseLong(pageLoadWait);
		else
			throw new RuntimeException("pageLoadWait not specified in the config.properties file.");
	}

	public static String getWebUrl() {
		String url = properties.getProperty("weburl");
		if (url != null)
			return url;
		else
			throw new RuntimeException("webpage url is not specified in the config.properties file.");
	}

}
