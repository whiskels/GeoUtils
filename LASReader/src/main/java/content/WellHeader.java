package main.java.content;

/**
 * Well headers are parsed from the corresponding LAS section
 */

public class WellHeader extends WellParameter {
    public WellHeader(String name) {
        super(name);
    }

    @Override
    public WellObjectType getType() {
        return WellObjectType.HEADER;
    }
}
