package com.wars.instruction;

import com.wars.constant.OperandType;
import com.wars.exception.AssemblerException;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class InstructionDescriptor {
    private final List<OperandType> operandTypes;
    private final InstructionCreator creator;

    public Instruction create(int[] operands) {
        return creator.create(operands);
    }

    public int[] parseOperands(String[] operands) {
        if (operands.length != operandTypes.size()) {
            throw new AssemblerException("Expected " + operandTypes.size() + " operands but got " + operands.length);
        }

        int[] parsedOperands = new int[operands.length];
        for (int i = 0; i < operandTypes.size(); i++) {
            parsedOperands[i] = validateOperand(operands[i], operandTypes.get(i));
        }

        return parsedOperands;
    }

    private int validateOperand(String operand, OperandType type) {
        try {
            return switch (type) {
                case REG5 -> parseReg(operand);
                case IMM16 -> parseImm(operand);
            };
        } catch (NumberFormatException e) {
            throw new AssemblerException("Invalid number format for operand: " + operand);
        }
    }

    private int parseReg(String val) {
        int bits = 5;
        int num = Integer.parseUnsignedInt(val);
        if (num < 0 || num >= (1 << bits)) {
            throw new AssemblerException("Register out of bounds: " + val);
        }
        return num;
    }

    private int parseImm(String val) {
        int bits = 16;
        int num = Integer.parseInt(val);
        int min = -(1 << (bits - 1));
        int max = (1 << (bits - 1)) - 1;
        if (num < min || num > max) {
            throw new AssemblerException("Immediate out of bounds: " + val);
        }
        return num;
    }

}
