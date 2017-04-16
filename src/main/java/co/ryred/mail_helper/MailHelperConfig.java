package co.ryred.mail_helper;

/**
 * Created by Cory Redmond on 13/04/2017.
 * ace@ac3-servers.eu
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by Cory Redmond on 18/01/2017.
 * ace@ac3-servers.eu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailHelperConfig {


    @Getter
    private static Gson gson = getDefaultGsonBuilder().create();

    /**
     * Allows loading the configuration from a remote/static/shared location.
     */
    private String configLocation = null;

    /**
     * Creator mode meaning creations can be made.
     */
    private Properties mailProperties = new Properties();

    /**
     * The username (email address) to log in with.
     */
    private String username = "someone@cory.red";

    /**
     * The password to log in with.
     */
    private String password = "Password123";

    /**
     * Email suffixes.
     */
    private String[] emailSuffixes = new String[]{ "cory.red", "ryred.co" };

    private String enigmaSettings = "* B I IV I AXLE";

    /**
     * Creates a default configuration.
     *
     * @return The default configuration.
     */
    public static MailHelperConfig getDefaultConfiguration() {

        MailHelperConfig configuration = new MailHelperConfig();

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", "mx.cory.red");
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.starttls.required", "true");
        configuration.setMailProperties( properties );

        return configuration;

    }

    /**
     * Load the configuration from a file.
     *
     * @param file The file to load from.
     * @return The configuration loaded from the file.
     */
    public static MailHelperConfig load(@NonNull File file) {

        try {
            return load(IOUtils.readText(file));
        } catch (FileNotFoundException e) {

			/*
             * Mkdirs makes the file a dir as well,
			 * and calling getParentFile sometimes may result in NPE.
			 * Dirty fix.. :/
			 */
            if (!file.exists()) {
                boolean made = file.mkdirs() && file.delete();
                if (!made)
                    System.out.println("Something went wrong when we were creating the file, it may already exist..");
            }


            MailHelperConfig defaultConfig = getDefaultConfiguration();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(gson.toJson(getDefaultConfiguration()));
            } catch (IOException e1) {
                System.out.println("Unable to save default the configuration!");
            }

            System.out.println("Saved the default configuration, please edit this to your liking and restart the server!");

            return defaultConfig;

        } catch (IOException e1) {
            System.out.println("Unable to save/load the configuration!");
            throw new RuntimeException("Error making configuration!", e1);
        }

    }

    /**
     * Load the configuration from a file.
     *
     * @param json The json string.
     * @return A new instance of {@link MailHelperConfig}
     */
    public static MailHelperConfig load(@NonNull String json) {

        MailHelperConfig configuration;

        // Load the configuration.
        System.out.println("Loading configuration.");
        configuration = gson.fromJson(json, MailHelperConfig.class);
        System.out.println("Configuration loaded!");

        return configuration;

    }

    /**
     * The default GSON builder..
     */
    private static GsonBuilder getDefaultGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting();
    }

    /**
     * Apply this instance to the masterConfig.
     *
     * @param masterConfig The upper configuration to be applied to.
     */
    public void applyTo(MailHelperConfig masterConfig) {

        // Is there really not a better way to do this?

        // Apply the env config.
        /*
        if (getEnvironment() != null) {

            EnvironmentConfig environmentConfig = masterConfig.getEnvironment();
            if (environmentConfig == null)
                masterConfig.setEnvironment(environmentConfig = new EnvironmentConfig());

            environmentConfig.setWorldLockTime(getEnvironment().getWorldLockTime());
            environmentConfig.setTimeLockingEnabled(getEnvironment().isTimeLockingEnabled());
            environmentConfig.setWeatherLockingEnabled(getEnvironment().isWeatherLockingEnabled());

        }*/

    }

    /**
     * Save the configuration to a file.
     *
     * @param file The file of which to save to.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save(@NonNull File file) {

        try {

			/*
             * Mkdirs makes the file a dir as well,
			 * and calling getParentFile sometimes may result in NPE.
			 * Dirty fix.. :/
			 */
            file.mkdirs();
            file.delete();
            file.createNewFile();

            try (FileWriter fw = new FileWriter(file)) {
                fw.write(gson.toJson(this));
                fw.flush();
            }

        } catch (IOException e1) {
            System.out.println("Unable to save the default configuration!");
            throw new RuntimeException("Error making default configuration!", e1);
        }

    }

    private static class IOUtils {

        /**
         * Read text from a {@link File}.
         *
         * @param file The file of where to read.
         * @return The string file contents.
         * @throws IOException
         */
        public static String readText(File file) throws IOException {
            return new Scanner(file).useDelimiter("\\A").next();
        }

    }
}
