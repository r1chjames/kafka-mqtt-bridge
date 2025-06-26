package kafkamqttbridge;

import java.util.function.Supplier;

public interface AppProducer {
    AppProducer connect();

    boolean publish(final String payload);
}
