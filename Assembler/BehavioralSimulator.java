import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BehavioralSimulator {
    private static final int NUM_REGISTERS = 8; // Number of register
    private static final int MEMORY_SIZE = 100; // Size of memory
    private int[] registers; // Register values array
    private int[] memory; // Instruction in memory array
    private int programCounter; //  Current instruction pointer

    private int instructionsExecuteCount = 0; // Count og executed instructions
    private int numMemory = 0;  // Count the number of instructions loaded

    // Constructor to initialize register and memory
    public BehavioralSimulator() {
        registers = new int[NUM_REGISTERS]; // Registers are automatically initialized to 0 in Java
        memory = new int[MEMORY_SIZE]; // init memory
        programCounter = 0; // init program count 0
    }

    // Load instruction from file
    public void loadMachineCode(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int address = 0;

            // Read lines from the file and  load to memory
            while ((line = br.readLine()) != null) {
                if (address < MEMORY_SIZE) {
                    memory[address++] = Integer.parseInt(line.trim()); //convert to interger and store in memory
                    numMemory++;  // Increment for each instruction loaded
                } else {
                    System.out.println("Warning: Memory overflow. Ignoring additional instructions.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print error if have problems
        } catch (NumberFormatException e) { 
            System.out.println("Invalid number format: " + e.getMessage()); //Print error if can't parsed
        }
    }

    // simulate machine's operation
    public void simulate() {
        while (true) {
            printState(); // Display state
            if (programCounter >= MEMORY_SIZE) { // Check if program counter exceed memory size
                System.out.println("Program counter exceeds memory bounds. Exiting simulation.");
                break; // exit the simulation if program counter is out of bounds
            }
            int instruction = memory[programCounter]; // Fetch current 
            int opcode = (instruction >> 22); // Extract opcode
            programCounter++; // plus program counter
            instructionsExecuteCount++; // plus executed instruction

            // executed  the instruction on the opcode
            switch (opcode) {
                case 0: // add
                    executeAdd(instruction);
                    break;

                case 1: // nand
                    executeNand(instruction);
                    break;

                case 2: // lw
                    executeLw(instruction);
                    break;

                case 3: // sw
                    executeSw(instruction);
                    break;

                case 4: // beq
                    executeBeq(instruction);
                    break;

                case 5: // jalr
                    executeJalr(instruction);
                    break;

                case 6: // halt
                    System.out.println("machine halted");
                    printState(); // Call printState before exiting
                    return;
                case 7: // noop
                    // Do nothing
                    break;

                default:
                    System.out.println("Unknown instruction: " + instruction); // if don't know instruction
                    break;
            }
        }
    }

    // Execute add instruction
    private void executeAdd(int instruction) {
        int destReg = (instruction) & 0x07; // Destination register
        int regA = (instruction >> 19) & 0x07; // Source register A
        int regB = (instruction >> 16) & 0x07; // Source register B
        registers[destReg] = registers[regA] + registers[regB];
    }

    // Execute nadd instruction
    private void executeNand(int instruction) {
        int destReg = (instruction) & 0x07; // Destination register
        int regA = (instruction >> 19) & 0x07; // Source register A
        int regB = (instruction >> 16) & 0x07; // Source register B
        registers[destReg] = ~(registers[regA] & registers[regB]);
    }

    // Execute lw instruction
    private void executeLw(int instruction) {
        int regA = (instruction >> 19) & 0x07; // Extract bits 25-23 (3 bits)
        int offset = instruction & 0xFFFF;     // Extract bits 15-0 (16 bits)
        int regB = (instruction >> 16) & 0x07; // Extract bits 22-20 (3 bits)
        // Check for valid memory access
        int effectiveAddress = registers[regA] + offset;
        if (effectiveAddress >= 0 && effectiveAddress < MEMORY_SIZE) {
            registers[regB] = memory[effectiveAddress];
        } else {
            System.out.println("Memory access out of bounds for lw: " + effectiveAddress);
        }
    }

    // Execute sw instruction
    private void executeSw(int instruction) {
        int regA = (instruction >> 19) & 0x07; // Extract bits 25-23 (3 bits)
        int regB = (instruction >> 16) & 0x07; // Extract bits 22-20 (3 bits)
        int offset = instruction & 0xFFFF;     // Extract bits 15-0 (16 bits)

        // Check for valid memory access
        int effectiveAddress = registers[regA] + offset;
        if (effectiveAddress >= 0 && effectiveAddress < MEMORY_SIZE) {
            memory[effectiveAddress] = registers[regB];
        } else {
            System.out.println("Memory access out of bounds for sw: " + effectiveAddress);
        }
    }

    // Execute beq instruction
    private void executeBeq(int instruction) {
        int regA = (instruction >> 19) & 0x07; // First source register
        int regB = (instruction >> 16) & 0x07; // Second source register
        int offset = convertNum(instruction & 0xFFFF); // Offset
        if (registers[regA] == registers[regB]) {
            programCounter += offset; // Branching
        }
    }

    // Execute jalr instruction
    private void executeJalr(int instruction) {
        int regA = (instruction >> 19) & 0x07; // Address to jump to
        int regB = (instruction >> 16) & 0x07; // Register to store return address
        registers[regB] = programCounter; // Save current PC to regB
        programCounter = registers[regA]; // Jump to address in regA
    }

    // Print current state of machine
    public void printState() {
        // Print program counter
        System.out.println("@@@");
        System.out.println("state:");
        System.out.println("\tpc " + programCounter);

        // Print memory
        System.out.println("\tmemory:");
        for (int i = 0; i < numMemory; i++) {  // Only print the number of loaded instructions
            System.out.printf("\tmem[ %2d ] %d%n", i, memory[i]); // Using printf for better formatting
        }

        // Print registers
        System.out.println("\tregisters:");
        for (int i = 0; i < NUM_REGISTERS; i++) {
            System.out.printf("\treg[ %2d ] %d%n", i, registers[i]); // Using printf for better formatting
        }
        
        // Print number of memory instructions loaded
        System.out.println("\tnumMemory: " + numMemory);
        System.out.println("end state");
    }

    // Convert to signed 16-bit integer
    public int convertNum(int num) {
        if ((num & (1 << 15)) != 0) {
            num -= (1 << 16);
        }
        return num;
    }

    // main program execution
    public static void main(String[] args) {
        BehavioralSimulator simulator = new BehavioralSimulator();
        simulator.loadMachineCode("output.txt"); // Load instruction form file
        simulator.simulate(); // start
        System.out.println("final state of machine:"); // show final state
        System.out.println("total of " + simulator.instructionsExecuteCount + " instructions executed"); // display total count
        simulator.printState(); 
    }
}
