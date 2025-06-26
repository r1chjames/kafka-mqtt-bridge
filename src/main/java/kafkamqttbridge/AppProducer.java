package kafkamqttbridge;

public interface AppProducer {
    AppProducer connect();

    boolean publish(String payload);

    void close();
}
