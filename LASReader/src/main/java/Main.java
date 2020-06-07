package main.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import main.java.content.Well;

import main.java.process.FileProcessor;
import main.java.process.LasProcessor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * LAS Reader
 * Finds well log (.las) files in given path
 * Parses found files
 * Creates well objects with the results of parsing
 *
 * @input path to file or directory
 *
 * @author whiskels
 */

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class); // Create instance of logger
    private static ArrayList<Well> wells = new ArrayList<>(); // Create list to store created wells

    /** Main method
     * @param args path to file or directory
     * */
    public static void main(String[] args) {
        // Tests (not to run with parameters)
        args = new String[1];
        args[0] = "D:\\utilities\\Java Projects\\data\\test_files_Athy";

        if (args.length != 1) {  // Sout info if args.length is illegal
            logger.info("LAS Reader.\nRun with one parameter - path to folder or file");
        } else { // If 1 argument
            FileProcessor fileProcessor = new FileProcessor(args[0]); // Create instance file processor
            List<Path> lasFiles = fileProcessor.processFiles(".las"); // Find all las files

            lasFiles.forEach(file -> wells.add(new LasProcessor(file).process())); // For each found file: parse LAS, create well and add it to the list
        }
        wells.forEach(well -> logger.info(well.toString())); // Log results
    }

    public static ArrayList<Well> getWells() {
        return wells;
    }

    public static void addWell(Well well) {
        wells.add(well);
    }
}
