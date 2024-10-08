package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Main {
    public static void main(String[] args) {
        String filePath = "assembler/sample.txt";
        String outputFilePath = "assembler/output.txt";
        try {
            int[] machineCodes = Assembler.assemble(filePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
                for (int code : machineCodes) {
                    writer.write(String.valueOf(code));
                    writer.newLine();
                }
            }
            System.out.println("Machine code has been written to " + outputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
