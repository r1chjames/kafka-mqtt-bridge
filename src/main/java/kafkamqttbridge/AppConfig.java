package kafkamqttbridge;
import com.typesafe.config.*;

public class AppConfig {
    private final Config config;

    public AppConfig(Config config) {
        this.config = config;
        config.checkValid(ConfigFactory.defaultReference());
    }

    public AppConfig() {
        this(ConfigFactory.load());
    }

    public String get(final String path) {
        return config.getString(path);
    }
}