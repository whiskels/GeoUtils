package content;

/**
 * Basic class for Well objects
 */

public abstract class WellObject {
    private final String name;

    public WellObject(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public WellObjectType getType() {
        return null;
    }
}
