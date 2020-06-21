package main.java;

import main.java.content.builder.WellBuilder;
import main.java.content.builder.WellBuilderDirector;
import main.java.content.builder.WellBuilderFromLas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import main.java.content.Well;

import main.java.modules.FileFinder;
import main.java.modules.LasReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * LAS Reader
 * Finds well log (.las) files in given path
 * Parses found files
 * Creates well objects with the results of parsing
 *
 * @args path to file or directory
 *
 * @author whiskels
 */

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class); // Create instance of logger
    private static List<Well> wells = new ArrayList<>(); // Create list to store created wells

    /** Main method
     * @param args path to file or directory
     */
    public static void main(String[] args) {
        // Tests (not to run with parameters)
        // args = new String[1];
        // args[0] = "D:\\utilities\\Java Projects\\data\\test_files_Athy";

        if (args.length != 1) {  // Sout info if args.length is illegal
            logger.info("LAS Reader.\nRun with one parameter - path to folder or file");
        } else { // If 1 argument
            final FileFinder fileFinder = new FileFinder(args[0]); // Create instance file processor
            final List<Path> lasFiles = fileFinder.findFiles(".las"); // Find all las files

            final WellBuilderDirector wbd = new WellBuilderDirector();

            lasFiles.forEach(file -> {
                    final WellBuilder builder = new WellBuilderFromLas(file);
                    wbd.createWell(builder);
                    wells.add(builder.getResult());
            });
        }

        wells.forEach(well -> logger.info(well.toString())); // Log results
    }

    public static List<Well> getWells() {
        return wells;
    }

    public static void addWell(Well well) {
        wells.add(well);
    }
}
