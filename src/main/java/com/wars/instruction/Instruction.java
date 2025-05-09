package com.wars.instruction;

public abstract class Instruction {
    protected final int opcode;

    protected Instruction(int opcode) {
        this.opcode = opcode;
    }

    public abstract int encode();

    public String toBinaryString() {
        return String.format("%32s", Integer.toBinaryString(encode()));
    }
}
