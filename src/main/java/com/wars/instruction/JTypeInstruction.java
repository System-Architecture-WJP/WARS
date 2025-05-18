package com.wars.instruction;

import com.wars.exception.AssemblerException;

public class JTypeInstruction extends Instruction {
    private int iindex = -1;
    private boolean isResolved;
    private final long address;
    
    public JTypeInstruction(int opcode, long address) {
        super(opcode);
        isResolved = false;
        this.address = address;
    }

    public JTypeInstruction(int opcode, int iindex) {
        super(opcode);
        this.iindex = iindex;
        isResolved = true;
        address = -1;
    }

    @Override
    public int encode() {
        if (!isResolved) {
            throw new AssemblerException("Attempt to encode unresolved J type instruction");
        }
        // opcode (6) | iindex (26)
        return (opcode << 26) | (iindex & 0x03FFFFFF);
    }
    
    public boolean isResolved() {
        return isResolved;
    }
        
    public void resolve(long labelAddress) {
        isResolved = true;
        // TODO: check that the difference fits in 26 bits
        this.iindex = (int) (labelAddress - address);
    }
    
    @Override
    public String toString() {
        if (!isResolved) {
            return "J-Type: unresolved";
        }
        return "J-Type: iindex: " + iindex;
    }
}
