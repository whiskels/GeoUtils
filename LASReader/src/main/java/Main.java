package main.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import main.java.content.Well;

import main.java.process.FileProcessor;
import main.java.process.LasProcessor;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;



public class Main {

    // Create Logger
    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    // List to store wells
    private static ArrayList<Well> wells = new ArrayList<>();

    // main.java.Main method
    public static void main(String[] args) {
        // Tests (not to run with parameters)
        args = new String[1];
        args[0] = "D:\\utilities\\Java Projects\\data\\test_files_Athy";

        // Process args
        if (args.length != 1) {  // Give info if args.length is illegal
            logger.info("LAS Reader.\nRun with one parameter - path to folder or file");
        }
        else {
            // If 1 argument
            FileProcessor fileProcessor = new FileProcessor(args[0],".las");
            List<Path> lasFiles = fileProcessor.processFiles();

            for (Path path : lasFiles) {
                LasProcessor lasProcessor = new LasProcessor(path);
                wells.add(lasProcessor.process());
            }

            for (Well well : wells) {
                logger.info(well.toString());
            }
        }
    }


    public static ArrayList<Well> getWells() {
        return wells;
    }

    public static void addWell(Well well) { Main.wells.add(well);}
}
