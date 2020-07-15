package content;

/**
 * Well parameters are parsed from the corresponding LAS section
 * Is parent for WellHeader class
 */

public class WellParameter extends WellObject {
    private String unit;
    private String value;
    private String description;

    public WellParameter(String name) {
        super(name);
    }

    @Override
    public WellObjectType getType() {
        return WellObjectType.PARAMETER;
    }

    /**
     * Sets all well parameter fields
     */
    public final void setNonEmptyFields(String unit, String value, String description) {
        setUnit(unit);
        setValue(value);
        setDescription(description);
    }

    public String getUnit() {
        return unit;
    }

    public final void setUnit(String unit) {
        if (unit != null && unit.length() != 0) {
            this.unit = unit;
        }
    }

    public String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        if (description != null && description.length() != 0) {
            this.description = description;
        }
    }

    public String getValue() {
        return value;
    }

    public final void setValue(String value) {
        if (value != null && value.length() != 0) {
            this.value = value;
        }
    }
}
