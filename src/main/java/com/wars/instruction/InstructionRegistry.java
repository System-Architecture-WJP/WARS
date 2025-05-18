package com.wars.instruction;

import com.wars.exception.AssemblerException;
import com.wars.operand.OperandType;

import java.util.List;
import java.util.Map;

public class InstructionRegistry {

    private static final Map<String, InstructionCreator> instructionCreatorMap = Initializer.initializeCreatorMap();
    private static final Map<String, List<OperandType>> operandTypesMap = Initializer.initializeOperandTypesMap();
    private static final Map<Integer, String> opcodeMap = Initializer.initializeOpcodeMap();

    public static Instruction create(String mnemonic, int[] operands) {
        if (!instructionCreatorMap.containsKey(mnemonic)) {
            throw new AssemblerException("Unknown instruction: " + mnemonic + " Possible instructions: " + instructionCreatorMap.keySet());
        }

        return instructionCreatorMap.get(mnemonic).create(operands);
    }

    public static List<OperandType> getOperandTypes(String mnemonic) {
        return operandTypesMap.get(mnemonic);
    }

    public static String getByOpcode(int opcode) {
        return opcodeMap.get(opcode);
    }
}
