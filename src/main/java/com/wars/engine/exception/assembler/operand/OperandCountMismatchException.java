package com.wars.engine.exception.assembler.operand;

import com.wars.engine.exception.assembler.AssemblerException;

public class OperandCountMismatchException extends AssemblerException {
    public OperandCountMismatchException(int expected, int actual) {
        super("Expected " + expected + " operands but got " + actual);
    }
}
