package bz.matrix4f.x10.game.networking;

import java.util.Arrays;

public class IntegerByte {

    private byte[] values; //Should be bits, but there is no bit data-type
    private int original; //Original input

    public IntegerByte(int value) {
        this.original = value;
        values = new byte[Integer.SIZE];
        int index = 0;
        int cpow = Integer.SIZE - 1;
        while(value > 0) {
            int cpowVal = (int) Math.pow(2, cpow);
            if(value / cpowVal == 1) {
                values[index] = 1;
                value %= cpowVal;
            }
            index++;
            cpow--;
        }
    }

    public IntegerByte(byte[] values) {
        this.values = values;
        for(int i = 0; i < values.length; i++) {
            original += Math.pow(2, i) * values[values.length - i - 1];
        }
    }

    public byte valueAt(int index) {
        return values[index];
    }

    public int getInteger() {
        return original;
    }

    public byte[] getValues() {
        return Arrays.copyOf(values, values.length);
    }
}
