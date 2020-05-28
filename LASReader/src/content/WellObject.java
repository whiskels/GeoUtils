package content;

public abstract class WellObject {
    private String name;

    public WellObject(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public WellObjectType getType() {
        return null;
    }
}
