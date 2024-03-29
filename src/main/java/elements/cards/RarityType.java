package elements.cards;

public enum RarityType {
    COMMON (1),
    RARE (2),
    EPIC (3),
    LEGENDARY (4);

    private final int value;

    RarityType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
