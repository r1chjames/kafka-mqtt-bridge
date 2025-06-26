package kafkamqttbridge;
import com.typesafe.config.*;

public final class AppConfig {
    private final Config parsedConfig;

    public AppConfig(final Config config) {
        this.parsedConfig = config;
        config.checkValid(ConfigFactory.defaultReference());
    }

    public AppConfig() {
        this(ConfigFactory.load());
    }

    public String getString(final String path) {
        return parsedConfig.getString(path);
    }

    public int getInt(final String path) {
        return parsedConfig.getInt(path);
    }
}
