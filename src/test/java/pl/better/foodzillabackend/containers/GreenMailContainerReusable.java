package pl.better.foodzillabackend.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class GreenMailContainerReusable extends GenericContainer<GreenMailContainerReusable> {

    private static GenericContainer<?> container;

    private GreenMailContainerReusable() {
    }

    public static GenericContainer getInstance() {
        if (container == null) {
            container = new GenericContainer<>(DockerImageName.parse("greenmail/standalone:latest"))
                    .waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1))
                    .withEnv("GREENMAIL_OPTS", "-Dgreenmail.setup.test.smtp -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.users=user:admin")
                    .withExposedPorts(3025);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
