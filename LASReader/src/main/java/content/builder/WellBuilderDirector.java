package content.builder;

public class WellBuilderDirector {
    public void createWell(WellBuilder builder) {
        builder.reset();
        builder.addHeaders();
        builder.addParameters();
        builder.addLogs();
    }
}
