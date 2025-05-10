package com.wars.instruction;

public abstract class Instruction {
    protected final int opcode;

    protected Instruction(int opcode) {
        this.opcode = opcode;
    }

    public abstract int encode();

    // will be used in the future when simulator is implemented
    // sub classes will need to be abstracted
    // public abstract void execute();

    public String toBinaryString() {
        return String.format("%32s", Integer.toBinaryString(encode())).replace(' ', '0');
    }
}
