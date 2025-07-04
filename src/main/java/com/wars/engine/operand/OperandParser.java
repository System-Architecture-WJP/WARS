package com.wars.engine.operand;

import com.wars.engine.exception.assembler.operand.OperandCountMismatchException;
import com.wars.engine.exception.assembler.operand.SignedOperandOutOfBoundsException;
import com.wars.engine.exception.assembler.operand.UnsignedOperandOutOfBoundsException;

import java.util.List;

public class OperandParser {
    public static int[] parseAll(String[] operands, List<OperandType> types) {
        if (operands.length != types.size()) {
            throw new OperandCountMismatchException(types.size(), operands.length);
        }

        int[] result = new int[operands.length];
        for (int i = 0; i < types.size(); i++) {
            result[i] = parseSingle(operands[i], types.get(i));
        }

        return result;
    }

    private static int parseSingle(String operand, OperandType type) {
        return switch (type) {
            case REG5 -> parseUnsigned(operand, 5);
            case IMM16 -> parseSigned(operand, 16);
            case IINDEX26 -> parseUnsigned(operand, 26);
        };
    }

    public static int parseUnsigned(String val, int bits) {
        int num = Integer.parseUnsignedInt(val);
        if (num < 0 || num >= (1 << bits)) {
            throw new UnsignedOperandOutOfBoundsException(val, bits);
        }
        return num;
    }

    private static int parseSigned(String val, int bits) {
        int num = Integer.parseInt(val);
        int min = -(1 << (bits - 1));
        int max = (1 << (bits - 1)) - 1;
        if (num < min || num > max) {
            throw new SignedOperandOutOfBoundsException(val, bits);
        }
        return num;
    }
}
