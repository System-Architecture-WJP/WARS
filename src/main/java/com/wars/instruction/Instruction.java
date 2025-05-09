package com.wars.instruction;

public abstract class Instruction {
    public abstract int encode();

    public String toBinaryString() {
        return String.format("%32s", Integer.toBinaryString(encode()));
    }
}
