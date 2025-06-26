package kafkamqttbridge.mqtt;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import static java.util.UUID.*;

public class MqttUtils {

    protected Mqtt3AsyncClient client;

    private void createMqttClient() {
        client = MqttClient.builder()
            .useMqttVersion3()
            .identifier(randomUUID().toString())
            .serverHost("broker.hivemq.com")
            .serverPort(1883)
            .buildAsync();
    }

    protected Mqtt3AsyncClient getMqttClient() {
        if (client == null) {
            createMqttClient();
        }
        return client;
    }

}
