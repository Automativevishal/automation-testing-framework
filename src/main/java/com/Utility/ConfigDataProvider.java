package com.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigDataProvider {

    private static final String DEFAULT_CONFIG_PATH = "Config/Config.properties";
    private final Properties properties = new Properties();

    public ConfigDataProvider() throws IOException {
        String configPath = System.getProperty("config.file", DEFAULT_CONFIG_PATH);
        Path path = Paths.get(configPath);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir")).resolve(path).normalize();
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            properties.load(inputStream);

            System.out.println("Config File = " + path);
            System.out.println("Headless from file = " + properties.getProperty("headless"));
        }
    }

    public String getProperty(String key) {
        return getProperty(key, "");
    }

    public String getProperty(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.trim().isEmpty()) {
            return systemValue.trim();
        }

        String value = properties.getProperty(key);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
    }

    public String getEnvironment() {
        return getProperty("environment", "qa");
    }

    public String getBrowser() {
        return getProperty("browser", "chrome");
    }

    public String getFacebook() {
        return getProperty("Facebook");
    }

    public String getQAENV_2() {
        return getProperty("QAENV_2");
    }

    public String getQAENV_3() {
        return getProperty("QAENV_3");
    }

    public String getAutomation_Testing() {
        return getProperty("Automation_Testing");
    }

    public String getGoogleSearch() {
        return getProperty("googlrSearch");
    }

    public String getOrangeHRM() {
        return getProperty("orangeHRM");
    }

    public String getTestPage() {
        return getProperty("testPage");
    }
    
    public String getEcartExercise() {
        return getProperty("ecart_Exercise");
    }
}
