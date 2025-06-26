package kafkamqttbridge;

import java.net.ConnectException;
import java.util.function.Consumer;

public interface AppConsumer {
    AppConsumer connect() throws ConnectException;

    void subscribe(Consumer<String> messageConsumer);
}
