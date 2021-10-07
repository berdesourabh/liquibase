package liquibase.util;

import liquibase.Scope;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

public class LiquibaseUtil {

    private static Properties liquibaseBuildProperties;

    /**
     * Return the configured build.version. Will always be "DEV" for non-release builds.
     */
    public static String getBuildVersion() {
        return getBuildInfo("build.version");
    }

    /**
     * Return the build version for release builds and a more descriptive string for snapshot builds.
     */
    public static String getBuildVersionInfo() {
        String version = getBuildInfo("build.version");
        if (version.equals("DEV")) {
            final String buildCommit = getBuildInfo("build.commit");
            if (buildCommit.equals("unknown")) {
                version = "[local build]";
            } else {
                version = "[Core: " + getBuildInfo("build.branch") + "/" + getBuildInfo("build.number") + "/" + buildCommit.substring(0, 6) + "/" + getBuildInfo("build.timestamp");

                if (getBuildInfo("build.pro.number") != null) {
                    version += ", Pro: " + getBuildInfo("build.pro.branch") + "/" + getBuildInfo("build.pro.number") + "/" + getBuildInfo("build.pro.commit").substring(0, 6) + "/" + getBuildInfo("build.pro.timestamp");
                }

                version += "]";
            }
        }

        return version;
    }

    public static String getBuildTime() {
        return getBuildInfo("build.timestamp");
    }

    public static String getBuildNumber() {
        return getBuildInfo("build.number");
    }

    // will extract the information from liquibase.build.properties, which should be a properties file in
    // the jar file.
    private static String getBuildInfo(String propertyId) {
        if (liquibaseBuildProperties == null) {
            try {
                liquibaseBuildProperties = new Properties();
                final Enumeration<URL> propertiesUrls = Scope.getCurrentScope().getClassLoader().getResources("liquibase.build.properties");
                while (propertiesUrls.hasMoreElements()) {
                    final URL url = propertiesUrls.nextElement();
                    try (InputStream buildProperties = url.openStream()) {
                        if (buildProperties != null) {
                            liquibaseBuildProperties.load(buildProperties);
                        }
                    }
                }
            } catch (IOException e) {
                Scope.getCurrentScope().getLog(LiquibaseUtil.class).severe("Cannot read liquibase.build.properties", e);
            }
        }

        String value = "UNKNOWN";
        value = liquibaseBuildProperties.getProperty(propertyId);
        if (value == null) {
            value = "UNKNOWN";
        }
        return value;
    }

}
