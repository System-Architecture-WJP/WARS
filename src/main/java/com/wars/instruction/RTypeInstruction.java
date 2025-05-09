package com.wars.instruction;

public class RTypeInstruction extends Instruction {
    private final int opcode, rs, rt, rd, sa, fun;

    public RTypeInstruction(int opcode, int rs, int rt, int rd, int sa, int fun) {
        this.opcode = opcode;
        this.rs = rs;
        this.rt = rt;
        this.rd = rd;
        this.sa = sa;
        this.fun = fun;
    }

    @Override
    public int encode() {
        // opcode (6) | rs (5) | rt (5) | rd (5) | sa (5) | fun (6)
        return (opcode << 26) | (rs << 21) | (rt << 16) | (rd << 11) | (sa << 6) | fun;
    }

    @Override
    public String toString() {
        return String.format("R-Type: opc=%d, rs=%d, rt=%d, rd=%d, sa=%d, fun=%d", opcode, rs, rt, rd, sa, fun);
    }
}
