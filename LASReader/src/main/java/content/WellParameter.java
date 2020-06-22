package content;

/**
 * Well parameters are parsed from the corresponding LAS section
 * Is parent for WellHeader class
 */

public class WellParameter extends WellObject {
    private String unit, value, description;

    public WellParameter(String name) {
        super(name);
    }

    @Override
    public WellObjectType getType() {
        return WellObjectType.PARAMETER;
    }

    /** Sets all well parameter fields */
    public final void setNonEmptyFields(String unit, String value, String description) {
        setUnit(unit);
        setValue(value);
        setDescription(description);
    }

    public final void setUnit(String unit) {
        if (unit != null && unit.length() != 0) {
            this.unit = unit;
        }
    }

    public final void setDescription(String description) {
        if (description != null && description.length() != 0) {
            this.description = description;
        }
    }

    public final void setValue(String value) {
        if (value != null && value.length() != 0) {
            this.value = value;
        }
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }
}
