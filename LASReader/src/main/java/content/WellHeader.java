package main.java.content;

public class WellHeader extends WellParameter {
    public WellHeader(String name) {
        super(name);
    }

    @Override
    public WellObjectType getType() {
        return WellObjectType.HEADER;
    }
}
