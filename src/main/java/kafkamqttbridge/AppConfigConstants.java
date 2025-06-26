package kafkamqttbridge;

public class AppConfigConstants {


    public static final String DELIMITER = ".";

    public static final String APP = "app";
    public static final String INPUT = APP + DELIMITER + "input";
    public static final String OUTPUT = APP + DELIMITER + "output";

    public static final String MQTT_PREFIX = "mqtt";
    public static final String MQTT_BROKER = MQTT_PREFIX + DELIMITER + "broker";
    public static final String MQTT_TOPIC = MQTT_PREFIX + DELIMITER + "topic";
    public static final String MQTT_USERNAME = MQTT_PREFIX + DELIMITER + "username";
    public static final String MQTT_PASSWORD = MQTT_PREFIX + DELIMITER + "password";

    public static final String KAFKA_PREFIX = "kafka";
    public static final String KAFKA_BROKER = KAFKA_PREFIX + DELIMITER + "broker";
    public static final String KAFKA_TOPIC = KAFKA_PREFIX + DELIMITER + "topic";
}

enum InputOutput {
    MQTT, KAFKA
}
