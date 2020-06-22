package content;

/**
 * Well headers are parsed from the corresponding LAS section
 */

public final class WellHeader extends WellParameter {
    public WellHeader(String name) {
        super(name);
    }

    @Override
    public WellObjectType getType() {
        return WellObjectType.HEADER;
    }
}
