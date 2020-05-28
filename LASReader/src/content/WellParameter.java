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
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
