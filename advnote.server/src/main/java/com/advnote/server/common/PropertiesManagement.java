package com.advnote.server.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.hibernate.internal.util.ConfigHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.advnote.server.util.Util;

public class PropertiesManagement {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesManagement.class);

//	private static final String SINGLE_QUOTE = "'";
//	private static final String PRODUCTION_MODE_ON_VALUE = "true";

	private static String appContext;
//	private static String serviceUrlPrefix;
//	private static String serverUrl;
//	private static String serverFrontEndUrl;
//	private static Boolean isProductionMode;

	static {
		try {
			appContext = PropertiesManagement.loadPropertyFromFile(Constants.APP_CONTEXT_PROPERTY, Constants.SERVER_FILE_PROPERTY);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
//
//	public static String getSysAdminEmail() throws IOException {
//
//		String email = ManagementProperties.loadPropertyFromFile(Constants.SYS_ADMIN_EMAIL_PROPERTY, Constants.SERVER_FILE_PROPERTY);
//		return email.replace(SINGLE_QUOTE, Constants.EMPTY_STRING).trim(); // The current  is used on a SQL File, it requires SINGLE_QUOTES. It's necessary remove before use. 
//
//	}

	public static Properties loadPropertiesFromFile(String fileName) throws IOException {
		Properties prop = new Properties();
		InputStream inputStream = null;

		try {
			inputStream = ConfigHelper.getResourceAsStream(fileName);

			if (inputStream != null) {
				prop.load(inputStream);
			}

		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		return prop;

	}

	public static String loadPropertyFromFile(String property, String fileName) throws IOException {

		Properties prop = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = Util.class.getClassLoader().getResourceAsStream(fileName);

			if (inputStream != null) {
				prop.load(inputStream);
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}

		}

		return prop.getProperty(property);
	}

	public static String getAppContext() throws IOException {
		if (appContext == null) {
			appContext = PropertiesManagement.loadPropertyFromFile(Constants.APP_CONTEXT_PROPERTY, Constants.SERVER_FILE_PROPERTY);
		}
		return appContext;
	}

}
