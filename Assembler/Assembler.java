package Assembler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Assembler {

    // input filePath return Array of Integer(line of code)
    public static int[] assemble(String filePath) throws IOException {

//First - scan label and memAddr and store in labels Map
        Map<String, Integer> labels = new HashMap<>(); // Map [label name : key , memAddr : value]
        int currentAddress = 0; //Track instruction Line

        // Read File
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 0) continue;

                // If first Part is not OPCODE or .fill assume it is labels
                if (!InstructionParser.isOpcode(parts[0]) && !".fill".equals(parts[0])) {
                    if (labels.containsKey(parts[0])) {// Check duplicate labels
                        System.err.println("Error: Duplicate label '" + parts[0] + "' at line " + currentAddress);
                        System.exit(1);
                    }
                    labels.put(parts[0], currentAddress);
                    currentAddress++;
                } else {
                    currentAddress++;
                }
            }
        }
//Second  - Gen Machine Code

        int[] machineCodes = new int[currentAddress]; // Array Store Machine Code
        currentAddress = 0;

        // Read File Again
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 0) continue;

                String instruction;
                String[] fields;

                // Check is Instruction or Label
                if (InstructionParser.isOpcode(parts[0]) || ".fill".equals(parts[0])) {

                    instruction = parts[0];
                    fields = new String[parts.length - 1];
                    System.arraycopy(parts, 1, fields, 0, parts.length - 1);
                } else {

                    instruction = parts[1];
                    fields = new String[parts.length - 2];
                    System.arraycopy(parts, 2, fields, 0, parts.length - 2);
                }

                // Use parseInstruction convert instruction and fields to binary machine code
                int machineCode = InstructionParser.parseInstruction(instruction, fields, labels, currentAddress);
                machineCodes[currentAddress++] = machineCode;
            }
        }

        return machineCodes;
    }
}
