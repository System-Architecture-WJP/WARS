package com.wars.engine.exception.assembler.operand;

import com.wars.engine.exception.assembler.AssemblerException;

public class SignedOperandOutOfBoundsException extends AssemblerException {
    public SignedOperandOutOfBoundsException(String value, int bits) {
        super("Signed operand out of bounds: " + value + " (must fit in " + bits + " bits)");
    }
}
