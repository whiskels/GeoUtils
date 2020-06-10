package main.java.modules;

import main.java.content.WellHeader;
import main.java.content.WellLog;
import main.java.content.WellParameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;

/** Las Reader class
 * Used to read .las files
 */

public final class LasReader extends FileHandler {
    private BufferedReader bf;
    private LasDataParser parser;
    private String wellName;
    private boolean isRead = false;

    public LasReader(Path input) throws IOException {
        super(input);
        wellName = input.getFileName().toString().replaceFirst("[.][^.]+$", "");
        bf = Files.newBufferedReader(input, StandardCharsets.UTF_8);
    }

    /**
     * Main method - read file and pass it's content to LasDataParser
     */
    public void readFile() throws IOException {
        parser = new LasDataParser(this);
        logger.info("Processing well {}", wellName);
        while (bf.ready()) {
            parser.parseLine(bf.readLine());
        }
        isRead = true;
        bf.close();
    }

    public String readLine() throws IOException {
        return bf.readLine();
    }

    public String getWellName() {
        return this.wellName;
    }

    public LasDataParser getParser() {
        return parser;
    }
}