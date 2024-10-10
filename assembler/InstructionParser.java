package Assembler;
import java.util.Map;
import java.util.HashMap;
public class InstructionParser {
    //Create Map that contain OPCODE
    private static final Map<String, Integer> OPCODES = new HashMap<>();
    static {
        OPCODES.put("add", 0b000);
        OPCODES.put("nand", 0b001);
        OPCODES.put("lw", 0b010);
        OPCODES.put("sw", 0b011);
        OPCODES.put("beq", 0b100);
        OPCODES.put("jalr", 0b101);
        OPCODES.put("halt", 0b110);
        OPCODES.put("noop", 0b111);
    }
    //Check String is OPCODE ?
    public static boolean isOpcode(String str) {
        return OPCODES.containsKey(str);
    }
    // Assembly Instruction to Machine Code
    public static int parseInstruction(String instruction, String[] fields, Map<String, Integer> labels, int currentAddress) {
        // If Instruction not .fill or not found in OPCODE MAP
        if (!OPCODES.containsKey(instruction) && !".fill".equals(instruction)) {
            System.err.println("Error: Invalid opcode '" + instruction + "' at line " + currentAddress);
            System.exit(1);
        }
        // If it is .fill then call parseFill()
        if (".fill".equals(instruction)) {
            return parseFill(fields[0], labels);
        }
        // Retrieve Binary of OPCODE from OPCODE Map then Create String start with OPCODE Binary
        int opcode = OPCODES.get(instruction);
        StringBuilder machineCode = new StringBuilder();
        // R-type instructions: add, nand
        if ("add".equals(instruction) || "nand".equals(instruction)) {
            String regA = toBinary.toBin(Integer.parseInt(fields[0]), 3);
            String regB = toBinary.toBin(Integer.parseInt(fields[1]), 3);
            String destReg = toBinary.toBin(Integer.parseInt(fields[2]), 3);
            machineCode.append(toBinary.toBin(opcode, 3)).append(regA).append(regB).append("0000000000000").append(destReg);
        }
        // I-type instructions: lw, sw, beq
        else if ("lw".equals(instruction) || "sw".equals(instruction) || "beq".equals(instruction)) {
            String regA = toBinary.toBin(Integer.parseInt(fields[0]), 3);
            String regB = toBinary.toBin(Integer.parseInt(fields[1]), 3);
            int offset;
            if (labels.containsKey(fields[2])) {
                if ("lw".equals(instruction) || "sw".equals(instruction)) {
                    offset = labels.get(fields[2]);
                } else {
                    offset = labels.get(fields[2]) - (currentAddress + 1);
                }
            } else {
                try {
                    offset = Integer.parseInt(fields[2]);
                } catch (NumberFormatException e) {
                    System.err.println("Error: Undefined label or invalid offset '" + fields[2] + "' at line " + currentAddress);
                    System.exit(1);
                    return -1;
                }
            }
            if (offset < -32768 || offset > 32767) {
                System.err.println("Error: Offset out of range (-32768 to 32767) for instruction at line " + currentAddress);
                System.exit(1);
            }
            machineCode.append(toBinary.toBin(opcode, 3)).append(regA).append(regB).append(toBinary.toBin(offset, 16));
        }
        // J-type instructions: jalr
        else if ("jalr".equals(instruction)) {
            String regA = toBinary.toBin(Integer.parseInt(fields[0]), 3);
            String regB = toBinary.toBin(Integer.parseInt(fields[1]), 3);
            machineCode.append(toBinary.toBin(opcode, 3)).append(regA).append(regB).append("0000000000000000");
        }
        // O-type instructions: halt, noop
        else if ("halt".equals(instruction) || "noop".equals(instruction)) {
            machineCode.append(toBinary.toBin(opcode, 3)).append("0000000000000000000000");
        }
        return Integer.parseInt(machineCode.toString(), 2);
    }
    private static int parseFill(String field, Map<String, Integer> labels) {
        if (labels.containsKey(field)) {
            return labels.get(field); // return label Address
        }
        try {
            return Integer.parseInt(field);
        } catch (NumberFormatException e) {
            System.err.println("Error: Undefined label or invalid .fill value '" + field + "'");
            System.exit(1);
            return -1;
        }
    }
}
