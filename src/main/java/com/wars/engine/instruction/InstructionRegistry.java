package com.wars.engine.instruction;

import com.wars.engine.exception.AssemblerException;
import com.wars.engine.operand.OperandType;

import java.util.List;
import java.util.Map;

public class InstructionRegistry {
    private static final Map<String, InstructionCreator> encoderInstructionMap = Initializer.initializeEncoderMap();
    private static final Map<String, InstructionCreator> executableInstructionMap = Initializer.initializeExecutableMap();
    private static final Map<String, List<OperandType>> operandTypesMap = Initializer.initializeOperandTypesMap();

    public static Instruction create(String mnemonic, int[] operands) {
        if (!encoderInstructionMap.containsKey(mnemonic)) {
            throw new AssemblerException("Unknown instruction: " + mnemonic + " Possible instructions: " + encoderInstructionMap.keySet());
        }

        return encoderInstructionMap.get(mnemonic).create(operands);
    }

    public static Instruction getExecutableInstruction(String mnemonic, int[] operands) {
        return executableInstructionMap.get(mnemonic).create(operands);
    }

    public static List<OperandType> getOperandTypes(String mnemonic) {
        return operandTypesMap.get(mnemonic);
    }
}
