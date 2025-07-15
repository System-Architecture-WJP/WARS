package com.wars.engine.instruction;

import com.wars.engine.simulator.Configuration;

public abstract class Instruction {
    protected final int opcode;

    protected Instruction(int opcode) {
        this.opcode = opcode;
    }

    public abstract int encode();

    public abstract boolean isResolved();

    public void execute(Configuration config) {
        throw new UnsupportedOperationException("Execute not implemented for this instruction");
    }

    public String toBinaryString() {
        return String.format("%32s", Integer.toBinaryString(encode())).replace(' ', '0');
    }
}
