package com.wars.instruction;

public class JTypeInstruction extends Instruction {
    private final int opcode, iindex;

    public JTypeInstruction(int opcode, int iindex) {
        this.opcode = opcode;
        this.iindex = iindex;
    }

    @Override
    public int encode() {
        // opcode (6) | iindex (26)
        return (opcode << 26) | (iindex & 0x03FFFFFF);
    }
}
