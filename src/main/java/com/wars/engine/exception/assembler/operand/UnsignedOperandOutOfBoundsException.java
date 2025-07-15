package com.wars.engine.exception.assembler.operand;

import com.wars.engine.exception.assembler.AssemblerException;

public class UnsignedOperandOutOfBoundsException extends AssemblerException {
    public UnsignedOperandOutOfBoundsException(String value, int bits) {
        super("Unsigned operand out of bounds: " + value + " (must fit in " + bits + " bits)");
    }
}
