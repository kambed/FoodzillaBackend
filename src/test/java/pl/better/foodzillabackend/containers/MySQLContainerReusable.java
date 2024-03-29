package pl.better.foodzillabackend.containers;

import org.testcontainers.containers.MySQLContainer;

public class MySQLContainerReusable extends MySQLContainer<MySQLContainerReusable> {
    private static final String IMAGE_VERSION = "mysql:5.7";
    private static MySQLContainerReusable container;

    private MySQLContainerReusable() {
        super(IMAGE_VERSION);
        this.withInitScript("init.sql");
    }

    public static MySQLContainerReusable getInstance() {
        if (container == null) {
            container = new MySQLContainerReusable();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("MYSQL_USER", container.getUsername());
        System.setProperty("MYSQL_PASSWORD", container.getPassword());
        System.setProperty("MYSQL_URL", container.getJdbcUrl());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
