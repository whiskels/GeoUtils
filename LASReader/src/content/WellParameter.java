package content;

public class WellParameter extends WellObject {
    private String unit, value, description;

    public WellParameter(String name) {
        super(name);
    }

    @Override
    public WellObjectType getType() {
        return WellObjectType.PARAMETER;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        if (unit != null || unit.length() != 0)
            this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null || description.length() != 0)
            this.description = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value != null || value.length() != 0)
            this.value = value;
    }

    public void setNonEmptyFields(String unit, String value, String description) {
        setUnit(unit);
        setValue(value);
        setDescription(description);
    }
}
