package Assembler;

public class toBinary {

    // values to Binary with ...bits
    public static String toBin(int value, int bits) {
                if (value < 0) {
            value = (1 << bits) + value;
        }
        String binaryString = Integer.toBinaryString(value);
        return String.format("%" + bits + "s", binaryString).replace(' ', '0');
    }
}
