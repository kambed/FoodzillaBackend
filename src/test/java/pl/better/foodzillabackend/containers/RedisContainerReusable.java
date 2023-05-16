package pl.better.foodzillabackend.containers;

import org.testcontainers.containers.GenericContainer;

import java.util.List;

public class RedisContainerReusable extends GenericContainer<RedisContainerReusable> {
    private static final String IMAGE_VERSION = "redis:7.0.11";
    private static RedisContainerReusable container;

    private RedisContainerReusable() {
        super(IMAGE_VERSION);
        this.setExposedPorts(List.of(6379));
    }

    public static RedisContainerReusable getInstance() {
        if (container == null) {
            container = new RedisContainerReusable();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_REDIS_PORT", container.getMappedPort(6379).toString());
    }
}

