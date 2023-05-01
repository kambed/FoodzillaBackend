package pl.better.foodzillabackend.utils.rabbitmq;

public enum Priority {
    HIGH(10),
    NORMAL(5),
    LOW(2),
    IDLE(0);

    private final int priorityValue;

    Priority(int priority) {
        this.priorityValue = priority;
    }

    public int getPriorityValue() {
        return priorityValue;
    }
}
