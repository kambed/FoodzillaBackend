package pl.better.foodzillabackend.containers;

import org.testcontainers.containers.GenericContainer;

import java.util.List;

public class RabbitMqContainerReusable extends GenericContainer<RabbitMqContainerReusable> {
    private static final String IMAGE_VERSION = "rabbitmq:management";
    private static RabbitMqContainerReusable container;

    private RabbitMqContainerReusable() {
        super(IMAGE_VERSION);
        this.setExposedPorts(List.of(5672, 15672));
    }

    public static RabbitMqContainerReusable getInstance() {
        if (container == null) {
            container = new RabbitMqContainerReusable();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("RABBIT_MQ_PORT", container.getMappedPort(5672).toString());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}

