package modules.process;

import java.io.BufferedReader;
import java.io.IOException;

import content.*;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import main.Main;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LasProcessor extends Processor {
    private final String BLOCK_SEPARATOR = "~", INFO_SEPARATOR = "#";
    private enum Section {V, W, C, A, P, O}
    private Section currentSection = null;
    private WellObjectType currentType = null;
    private boolean isWrapped = false;

    public LasProcessor(Path input) {
        super(input);
    }

    public Well process() {
        // Create well (file name without extension)
        Well well = new Well(this.getInput().getFileName().toString().replaceFirst("[.][^.]+$", ""));
        Main.LOGGER.info("Processing well " + well.getName());
        readLasContent(well);
        return well;
    }

    private void readLasContent(Well well) {
        // Create BufferedReader to read file line by line
        try (BufferedReader bf = Files.newBufferedReader(this.getInput(), StandardCharsets.UTF_8)) {

            // Read las
            String line;
            while (bf.ready()) {
                line = bf.readLine();
                if (line.startsWith(BLOCK_SEPARATOR)) { // If line is another block
                    setCurrentSectionAndType(line); // Get current block's section
                } else if (!line.startsWith(INFO_SEPARATOR) && this.currentSection != null) { // If line is not info and currentSection is defined
                    switch (this.currentSection) {
                        case V: // VERSION - get las version and check if LAS is wrapped (Multiple lines per depth step in A section)
                            int pointIndex = line.indexOf(".");
                            int colonIndex = line.indexOf(":");
                            String value = line.substring(pointIndex, colonIndex).replaceAll("\\s+", "");
                            if (line.startsWith("VERS")) {
                                String lasVersion = value;
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
                            // TODO parse this section
                            break;
                        case O:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            Main.LOGGER.severe(e + " " + e.getMessage());
        }
    }

    private void setCurrentSectionAndType(String line) {
        switch (line.charAt(1)) {
            case 'V' :
                this.currentSection = Section.V;
                this.currentType = null;
                break;
            case 'W' :
                this.currentSection = Section.W;
                this.currentType = WellObjectType.HEADER;
                break;
            case 'C' :
                this.currentSection = Section.C;
                this.currentType = WellObjectType.LOG;
                break;
            case 'P' :
                this.currentSection = Section.P;
                this.currentType = WellObjectType.PARAMETER;
                break;
            case 'A' :
                this.currentSection = Section.A;
                this.currentType = null;
                break;
        }
    }

    private WellParameter getWellParameter(String line) {
        String name = null, unit = null, value = null, desc = null;
        WellParameter parameter = null;

        Pattern pattern = Pattern.compile("\\.?([^.]*)\\.([^\\s]*)(.*):(.*)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
             name = matcher.group(1).replaceAll(" ", "");
             unit = matcher.group(2).replaceAll(" ", "");
             value = matcher.group(3).replaceAll(" " ,"");
             desc = matcher.group(4);
        }

        if (!isEmpty(name)) {
            parameter = this.currentType.getInstance(name);
            parameter.setNonEmptyFields(unit,value,desc);
        }

        return parameter;
    }
}
