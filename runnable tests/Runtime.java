import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.util.Properties;

class Runtime {
    static final Path folder = Paths.get("runtime");

    private static final Path settingsFile = folder.resolve("runtime.properties");
    private static final Properties settings = new Properties();

    static {
        try (Reader reader = Files.newBufferedReader(settingsFile)) {
            settings.load(reader);
        } catch (NoSuchFileException e) {
            try {
                Files.createFile(settingsFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void cleanOutput() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.log")) {
            for (Path path : stream) {
                Files.delete(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    enum Setting {
        ROTATION_POLICY("RotatingFileConsumer.rotation-policy", "startup", "temporal");

        private final String key;
        private final String acceptedValues;

        Setting(String key) {
            this(key, "<no values specified>");
        }

        Setting(String key, String... acceptedValues) {
            this.key = key;
            this.acceptedValues = String.join(", ", acceptedValues);
        }

        String get() {
            if (settings.containsKey(key)) {
                return settings.getProperty(key);
            }
            throw new RuntimeException("Please set '" + key + "' in " + settingsFile + " file.");
        }

        RuntimeException newInvalidValueException() {
            return new RuntimeException("Please set '" + key + "' to either: " + acceptedValues + '.');
        }
    }
}
