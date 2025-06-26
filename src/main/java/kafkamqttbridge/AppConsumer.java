package kafkamqttbridge;

import java.util.function.Consumer;

public interface AppConsumer {
    AppConsumer connect();

    void subscribe(final Consumer<String> messageConsumer);
}
