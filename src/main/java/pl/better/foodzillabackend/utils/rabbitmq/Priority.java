package pl.better.foodzillabackend.utils.rabbitmq;

public enum Priority {
    HIGH(2),
    NORMAL(1);

    private final int priorityValue;

    Priority(int priority) {
        this.priorityValue = priority;
    }

    public int getPriorityValue() {
        return priorityValue;
    }
}
