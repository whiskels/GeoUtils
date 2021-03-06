package content.builder;

import content.Well;
import modules.LasDataParser;
import modules.LasReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class WellBuilderFromLas implements WellBuilder {
    private static final Logger logger = LoggerFactory.getLogger(WellBuilderFromLas.class); // Create instance of logger
    private final Path input;
    private Well well;
    private LasDataParser parser;

    public WellBuilderFromLas(Path input) {
        this.input = input;
    }

    @Override
    public void reset() {
        try {
            LasReader reader = new LasReader(input);
            reader.readFile();
            well = new Well(reader.getWellName());
            parser = reader.getParser();
        } catch (IOException e) {
            logger.error("\t{} {}", e, e.getMessage());
        }
    }

    @Override
    public void addHeaders() {
        well.addHeaders(parser.getHeaders());
    }

    @Override
    public void addLogs() {
        well.addLogs(parser.getLogs());
    }

    @Override
    public void addParameters() {
        well.addParameters(parser.getParameters());
    }

    @Override
    public Well getResult() {
        return well;
    }
}
