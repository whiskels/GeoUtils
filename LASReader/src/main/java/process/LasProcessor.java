package main.java.process;

import main.java.content.Well;
import main.java.content.WellObjectType;
import main.java.content.WellParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Las Processor class
 * Used to parse .las files and create Well objects
 */

public final class LasProcessor extends Processor {
    private final String BLOCK_SEPARATOR = "~", INFO_SEPARATOR = "#"; // Separators are constant
    private Section currentSection = null; // Current section of .las file
    private WellObjectType currentType = null; // Current type of created parameter
    private int currentLine = -1; // Current file line
    private boolean isWrapped = false; // Is values section wrapped
    private enum Section {V, W, C, A, P, O} // .las file sections as enum

    public LasProcessor(Path input) {
        super(input);
    }

    /**
     * Main method - processes .las file
     *
     * @return well object
     */
    public Well process() {
        // Create well (file name without extension)
        Well well = new Well(this.getInput().getFileName().toString().replaceFirst("[.][^.]+$", ""));
        logger.info("Processing well {}", well.getName());
        readLasContent(well);
        return well;
    }

    /**
     * Reads file content and fills fields in created well
     * @param well Well instance created in process() method is given as input
     */
    private void readLasContent(Well well) {
        // Try-with-resources initialization of buffered reader
        try (BufferedReader bf = Files.newBufferedReader(this.getInput(), StandardCharsets.UTF_8)) {
            String line;
            currentLine = 0;

            while (bf.ready()) {
                line = bf.readLine();
                currentLine++;
                if (line.startsWith(BLOCK_SEPARATOR)) { // If line is another block
                    setCurrentSectionAndType(line); // Get current block's section
                    logger.debug("\tSwitched section to: {}", this.currentSection);
                } else if (!line.startsWith(INFO_SEPARATOR) && this.currentSection != null) { // If line is not info and currentSection is defined
                    switch (this.currentSection) {
                        case V: // VERSION - get las version and check if LAS is wrapped (Multiple lines per depth step in A section)
                            int pointIndex = line.indexOf(".");
                            int colonIndex = line.indexOf(":");
                            String value = line.substring(pointIndex, colonIndex).replaceAll("\\s+", "");
                            if (line.startsWith("VERS")) {
                                String lasVersion = value;
                                logger.debug("\tLAS Version: {}", lasVersion);
                            } else if (line.startsWith("WRAP") && value.equals("YES")) {
                                this.isWrapped = true;
                            }
                            break;
                        case W: // WELL - merged with P
                        case C: // CURVE - merged with P
                        case P:
                            if (this.currentType != null) {
                                well.addParameter(getWellParameter(line));
                            }
                            break;
                        case A:
                            List<String> values = new ArrayList<>();
                            while (values.size() != well.getLogs().size()) { // Accumulate data until values size equals log curves size
                                try {
                                    if (Character.isWhitespace(line.charAt(0))) {
                                        line = line.replaceFirst("\\s+", "");
                                    }
                                    line = line.replaceAll("\\s+", " ");
                                    values.addAll(Arrays.asList(line.split(" ")));
                                    if (values.size() != well.getLogs().size()) {
                                        line = bf.readLine();
                                        currentLine++;
                                    }
                                } catch (Exception e) {
                                    logger.error("\t{} while trying to parse line {}\n\tline is: {}\n\t{}", e, currentLine, line, e.getMessage());
                                    break;
                                }
                            }
                            well.addLogValues(values);
                            break;
                        case O:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("\t{} {}", e, e.getMessage());
        }
        logger.debug(well.toString());
    }

    /** Sets current section and type from line that starts with BLOCK_SEPARATOR */
    private void setCurrentSectionAndType(String line) {
        switch (line.charAt(1)) {
            case 'V':
                currentSection = Section.V;
                currentType = null;
                break;
            case 'W':
                currentSection = Section.W;
                currentType = WellObjectType.HEADER;
                break;
            case 'C':
                currentSection = Section.C;
                currentType = WellObjectType.LOG;
                break;
            case 'P':
                currentSection = Section.P;
                currentType = WellObjectType.PARAMETER;
                break;
            case 'O':
                currentSection = Section.O;
                currentType = null;
                break;
            case 'A':
                currentSection = Section.A;
                currentType = null;
                break;
        }
    }

    /** General method to get Well parameters from line*/
    private WellParameter getWellParameter(String line) {
        String name = null, unit = null, value = null, desc = null;
        WellParameter parameter = null;
        Pattern pattern = Pattern.compile("\\.?([^.]*)\\.([^\\s]*)(.*):(.*)");
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            name = matcher.group(1).replaceAll(" ", "");
            unit = matcher.group(2).replaceAll(" ", "");
            value = matcher.group(3).replaceAll(" ", "");
            desc = matcher.group(4);
        }

        if (!isEmpty(name)) {
            parameter = this.currentType.getInstance(name);
            parameter.setNonEmptyFields(unit, value, desc);
        }

        return parameter;
    }
}
