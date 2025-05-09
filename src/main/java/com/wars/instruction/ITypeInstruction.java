package com.wars.instruction;

public class ITypeInstruction extends Instruction {
    private final int opcode, rs, rt, immediate;

    public ITypeInstruction(int opcode, int rs, int rt, int immediate) {
        this.opcode = opcode;
        this.rs = rs;
        this.rt = rt;
        this.immediate = immediate;
    }

    @Override
    public int encode() {
        // opcode (6) | rs (5) | rt (5) | immediate (16)
        return (opcode << 26) | (rs << 21) | (rt << 16) | (immediate & 0xFFFF);
    }

    @Override
    public String toString() {
        return String.format("I-Type: opc=%d, rs=%d, rt=%d, imm=%d", opcode, rs, rt, immediate);
    }
}
