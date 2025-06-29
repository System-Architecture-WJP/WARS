package com.wars.instruction;

import com.wars.exception.AssemblerException;
import com.wars.operand.OperandType;

import java.util.List;
import java.util.Map;

public class InstructionRegistry {
    private static final Map<String, InstructionCreator> encoderInstructionMap = Initializer.initializeEncoderMap();
    private static final Map<String, InstructionCreator> executableInstructionMap = Initializer.initializeExecutableMap();
    private static final Map<String, List<OperandType>> operandTypesMap = Initializer.initializeOperandTypesMap();
    private static final Map<Integer, String> opcodeMap = Initializer.initializeOpcodeMap();

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

    public static String getMnemonicByOpcode(int opcode) {
        return opcodeMap.get(opcode);
    }
}
