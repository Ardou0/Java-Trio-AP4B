package fr.utbm.ap4b.model;

public enum Trios {
    LO21(1),
    LC(2),
    MH40(3),
    WE4(4),
    LR(5),
    LP2(6),
    DEUTEC(7),
    AP4(8),
    LK(9),
    SI40(10),
    SY34(11),
    LG(12);

    private final int value;

    Trios(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
