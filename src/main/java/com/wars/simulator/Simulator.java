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
        int opcode = (instructionWord >> 26);

        if (opcode == 0b000000 || opcode == 0b010000) {
            simulateRType(configuration, instructionWord);
        } else if (opcode == 0b000010 || opcode == 0b000011) {
            simulateJType(configuration, instructionWord);
        } else {
            simulateIType(configuration, instructionWord);
        }
    }

    private static void simulateIType(Configuration configuration, int instructionWord) {
        int opcode = (instructionWord >> 26);
        int rs = (instructionWord >>> 21);
        int rt = (instructionWord >>> 16);
        int imm = instructionWord & 0xFFFF;
        int[] operands = {rs, rt, imm};

        String mnemonic = InstructionRegistry.getMnemonicByOpcode(opcode);
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void simulateRType(Configuration configuration, int instructionWord) {
        int opcode = (instructionWord >> 26);
        int rs = (instructionWord >>> 21);
        int rt = (instructionWord >>> 16);
        int rd = (instructionWord >>> 11);
        int sa = (instructionWord >>> 6);
        int fun = instructionWord & 0x3F;
        int[] operands = {rs, rt, rd, sa, fun};

        String mnemonic = InstructionRegistry.getMnemonicByOpcode(opcode);
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void simulateJType(Configuration configuration, int instructionWord) {
        int opcode = (instructionWord >> 26);
        int iindex = instructionWord & 0x03FFFFFF;
        int[] operands = {iindex};

        String mnemonic = InstructionRegistry.getMnemonicByOpcode(opcode);
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void executeInstruction(Configuration configuration, String mnemonic, int[] operands) {
        var instruction = InstructionRegistry.create(mnemonic, operands);
        instruction.execute(configuration);
    }

}
