package com.documentmigrationencryption;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class ApplicationPropertiesReader {
    private static Properties properties;

    public ApplicationPropertiesReader() {
        properties = new Properties();
        // Load the application.properties file from the classpath
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new IOException("application.properties file not found in the classpath");
            }
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, String> getAppDetails() {
        HashMap<String, String> KeyHash = new HashMap<>();

        KeyHash.put("ENCRYPTION_KEY_FILE", properties.getProperty("ENCRYPTION_KEY_FILE"));
        KeyHash.put("ENCRYPTION_KEY_FILE_EXTENSION", properties.getProperty("ENCRYPTION_KEY_FILE_EXTENSION"));
        KeyHash.put("ENCRYPTION_FILE", properties.getProperty("ENCRYPTION_FILE"));
        KeyHash.put("ENCRYPTION_FILE_EXTENSION", properties.getProperty("ENCRYPTION_FILE_EXTENSION"));
        KeyHash.put("Product.PDF_PATH_WORKSPACE", properties.getProperty("Product.PDF_PATH_WORKSPACE"));
        KeyHash.put("Product.PDF_PATH_BASE", properties.getProperty("Product.PDF_PATH_BASE"));
        KeyHash.put("Product.PDF_PATH_WAR", properties.getProperty("Product.PDF_PATH_WAR"));

        return KeyHash;
    }

    public static HashMap<String, String> get_details(){
        ApplicationPropertiesReader obj = new ApplicationPropertiesReader();
        return obj.getAppDetails();
    }
}

