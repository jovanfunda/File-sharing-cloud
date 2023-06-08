package app;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Constants {

    public static String working_root;

    public static int lower_limit;
    public static int upper_limit;

    public static String bootstrapIP;
    public static int bootstrapPort;

    public static void appConfig() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("resources/app.properties")) {
            properties.load(fileInputStream);

            working_root = properties.getProperty("working_root");

            lower_limit = Integer.parseInt(properties.getProperty("lower_limit"));
            upper_limit = Integer.parseInt(properties.getProperty("upper_limit"));

            bootstrapIP = properties.getProperty("bootstrapIP");
            bootstrapPort = Integer.parseInt(properties.getProperty("bootstrapPort"));

            } catch (IOException e) {
            e.printStackTrace();
        }
    }
}