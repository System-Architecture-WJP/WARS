package com.wars.instruction;

class JTypeInstruction extends Instruction {
    private final int iindex;

    public JTypeInstruction(int opcode, int iindex) {
        super(opcode);
        this.iindex = iindex;
    }

    @Override
    public int encode() {
        // opcode (6) | iindex (26)
        return (opcode << 26) | (iindex & 0x03FFFFFF);
    }
}
