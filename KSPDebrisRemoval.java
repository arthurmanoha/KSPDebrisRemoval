package kspdebrisremoval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This code reads a KSP quicksave and removes all ships flagged as debris.
 *
 * @author arthu
 */
public class KSPDebrisRemoval {

    public static void main(String[] args) {

        for (String s : args) {

        }

        String folder = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Kerbal Space Program\\saves\\2024\\";
        String inputFile = "quicksave.sfs";
        String outputFile = "quicksaveWithDebrisRemoved.sfs";
        String discardedLinesFile = "quicksaveButOnlyDiscardedLines.sfs";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(folder + inputFile)));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(folder + outputFile)));
            BufferedWriter writerForDebris = new BufferedWriter(new FileWriter(new File(folder + discardedLinesFile)));

            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {

                if (removeLeadingTabs(line).equals("VESSEL")) {
                    writer.write(line + "\n");
                    // This vessel may or may not be a debris.
                    boolean currentVesselIsDebris = false;

                    ArrayList<String> vesselLines = new ArrayList<>();

                    // Read the first opening curly brace:
                    line = reader.readLine();
                    vesselLines.add(line);
                    line = removeLeadingTabs(line);
                    if (!line.equals("{")) {
                        System.out.println("Error: first line after 'VESSEL' is not '{' but should be.");
                    }
                    int curlyBracketLevel = 1; // Will reach zero again at the end ot current vessel.

                    // Read the lines that represent the vessel.
                    while (curlyBracketLevel > 0) {

                        // Add the line to the list.
                        line = reader.readLine();
                        vesselLines.add(line);

                        // Detect the curly braces
                        if (removeLeadingTabs(line).equals("{")) {
                            curlyBracketLevel++;
                        } else if (removeLeadingTabs(line).equals("}")) {
                            curlyBracketLevel--;
                        }

                        // Detect a vessel categorized as debris
                        if (removeLeadingTabs(line).equals("type = Debris")) {
                            currentVesselIsDebris = true;
                        }
                    }

                    // We have now read the whole ship, and we know whether or not it is a debris.
                    if (!currentVesselIsDebris) {
                        // Not a debris, write it to the output file.
                        for (String lineToBeSaved : vesselLines) {
                            writer.write(lineToBeSaved + "\n");
                        }
                    } else {
                        // Debris
                        for (String lineToBeSaved : vesselLines) {
                            writerForDebris.write(lineToBeSaved + "\n");
                        }
                    }

                } else {
                    // Print the line unchanged to the output file.
                    writer.write(line + "\n");
                }

            }
            writer.flush();
        } catch (IOException e) {
            System.out.println("IOException:");
            System.out.println(e);
        }

    }

    /**
     * Compute and return the same string but with any leading tab removed.
     *
     * @param line
     * @return
     */
    private static String removeLeadingTabs(String line) {
        while (line.charAt(0) == '\t') {
            line = line.substring(1);
        }
        return line;
    }

}
