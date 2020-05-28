package main;

import content.Well;

import modules.process.ProcessFiles;


import java.util.ArrayList;
import java.util.logging.Logger;


public class Main {
    // Create Logger
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    // List to store wells
    private static ArrayList<Well> wells = new ArrayList<Well>();

    // main.Main method
    public static void main(String[] args) {
        // Tests (not to run with parameters)
        args = new String[1];
        args[0] = "D:\\utilities\\Java Projects\\data\\test_files_Athy";

        // Process args
        if (args.length != 1) {  // Give info if args.length is illegal
            LOGGER.info("LAS Reader.\n" +
                    "Run with one parameter - path to folder or file");
        }
        else {
            // If 1 argument
            ProcessFiles.go(args[0]);
            for (Well well : wells) {
                System.out.println(well.toString());
            }
        }
    }


    public static ArrayList<Well> getWells() {
        return wells;
    }

    public static void addWell(Well well) { Main.wells.add(well);}
}
