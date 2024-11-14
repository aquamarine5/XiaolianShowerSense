package org.aquarngd.xiaolianshowersense.data;

public enum WasherStatus {
    NOT_USING(1),
    USING(2),
    FAULT(0);

    private final int id;

    WasherStatus(int id) {
        this.id = id;
    }

    public int value() {
        return id;
    }

    public static WasherStatus valueOf(int value) {
        return switch (value) {
            case 1 -> NOT_USING;
            case 2 -> USING;
            default -> FAULT;
        };
    }
}
