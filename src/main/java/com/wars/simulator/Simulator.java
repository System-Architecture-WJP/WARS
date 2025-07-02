package com.wars.simulator;

import com.wars.instruction.InstructionRegistry;

import java.util.HashMap;
import java.util.Map;

public class Simulator {

    public static Configuration simulate(int[] instructions) {
        var configuration = new Configuration();

        // Mimic having the program in memory.
        Map<Long, Integer> programMemory = new HashMap<>();
        for (int i = 0; i < instructions.length; i++) {
            programMemory.put((long) i * 4, instructions[i]);
        }

        while (programMemory.containsKey(configuration.getPC())) {
            int instructionWord = programMemory.get(configuration.getPC());
            simulateStep(configuration, instructionWord);
        }

        return configuration;
    }

    private static void simulateStep(Configuration configuration, int instructionWord) {
        int opcode = (instructionWord >> 26) & 0x3F;

        if (opcode == 0b000000 || opcode == 0b010000) {
            simulateRType(configuration, opcode, instructionWord);
        } else if (opcode == 0b000010 || opcode == 0b000011) {
            simulateJType(configuration, opcode, instructionWord);
        } else {
            simulateIType(configuration, opcode, instructionWord);
        }
    }

    private static void simulateIType(Configuration configuration, int opcode, int instructionWord) {
        int rs = (instructionWord >> 21) & 0x1F;
        int rt = (instructionWord >> 16) & 0x1F;
        int imm = instructionWord & 0xFFFF;
        int[] operands = {rs, rt, imm};

        String mnemonic = MnemonicUtils.getMnemonicForIType(opcode, rt);
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void simulateRType(Configuration configuration, int opcode, int instructionWord) {
        int rs = (instructionWord >> 21) & 0x1F;
        int rt = (instructionWord >> 16) & 0x1F;
        int rd = (instructionWord >> 11) & 0x1F;
        int sa = (instructionWord >> 6) & 0x1F;
        int fun = instructionWord & 0x3F;
        int[] operands = {rs, rt, rd, sa, fun};

        String mnemonic = MnemonicUtils.getMnemonicForRType(opcode, fun, rs);
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void simulateJType(Configuration configuration, int opcode, int instructionWord) {
        int iindex = instructionWord & 0x03FFFFFF;
        int[] operands = {iindex};

        String mnemonic = MnemonicUtils.getMnemonicForJType(opcode);
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void executeInstruction(Configuration configuration, String mnemonic, int[] operands) {
        System.out.println("Executing instruction: " + mnemonic + " with operands: " + java.util.Arrays.toString(operands));
        var instruction = InstructionRegistry.getExecutableInstruction(mnemonic, operands);
        instruction.execute(configuration);
    }
}
