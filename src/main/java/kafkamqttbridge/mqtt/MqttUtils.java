package kafkamqttbridge.mqtt;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import kafkamqttbridge.AppConfig;

import static java.util.UUID.*;
import static kafkamqttbridge.AppConfigConstants.MQTT_BROKER;
import static kafkamqttbridge.AppConfigConstants.MQTT_PORT;

public class MqttUtils {

    private Mqtt3AsyncClient client;

    private void createMqttClient(final AppConfig config) {
        client = MqttClient.builder()
            .useMqttVersion3()
            .identifier(randomUUID().toString())
            .serverHost(config.getString(MQTT_BROKER))
            .serverPort(config.getInt(MQTT_PORT))
            .buildAsync();
    }

    /**
     * Instantiates Mqtt client on first call, returns it if already instantiated
     * @param config
     * @return
     */
    protected Mqtt3AsyncClient getMqttClient(final AppConfig config) {
        if (client == null) {
            createMqttClient(config);
        }
        return client;
    }

}
