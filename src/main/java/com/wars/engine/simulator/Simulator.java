package com.wars.engine.simulator;

import com.wars.engine.instruction.InstructionRegistry;
import com.wars.engine.exception.SimulatorException;

public class Simulator {

    public static Configuration simulate(int[] instructions) {
        Configuration c = new Configuration();

        for (int i = 0; i < instructions.length; i++) {
            c.setWord(i * 4, instructions[i]);
        }

        while (c.hasWordAt((int) c.getPC()) && c.isRunning()) {
            int instructionWord = c.getWord((int) c.getPC());
            simulateStep(c, instructionWord);
        }

        return c;
    }

    public static Configuration simulate(Configuration c) {
        int steps = 0;
        int max_steps = (1 << 15);

        while (steps < max_steps &&
                c.hasWordAt((int) c.getPC()) &&
                c.isRunning()) {
            int PC = (int) c.getPC();

            if (PC % 4 != 0) {
                throw new SimulatorException("Fetch address is not word aligned: " + PC);
            }

            int instr = c.getWord(PC);
            simulateStep(c, instr);

            steps++;
        }

        return c;
    }

    private static void simulateStep(Configuration configuration, int instructionWord) {
        int opcode = (instructionWord >>> 26);

        if (opcode == 0b000000 || opcode == 0b010000) {
            simulateRType(configuration, opcode, instructionWord);
        } else if (opcode == 0b000010 || opcode == 0b000011) {
            simulateJType(configuration, opcode, instructionWord);
        } else {
            simulateIType(configuration, opcode, instructionWord);
        }
    }

    private static void simulateIType(Configuration configuration, int opcode, int instructionWord) {
        int rs = (instructionWord >>> 21) & 31;
        int rt = (instructionWord >>> 16) & 31;
        int imm = (instructionWord << 16) >> 16;
        int[] operands = {rs, rt, imm};

        String mnemonic = MnemonicUtils.getMnemonicForIType(opcode, rt);
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void simulateRType(Configuration configuration, int opcode, int instructionWord) {
        int rs = (instructionWord >>> 21) & 31;
        int rt = (instructionWord >>> 16) & 31;
        int rd = (instructionWord >>> 11) & 31;
        int sa = (instructionWord >>> 6) & 31;
        int fun = instructionWord & 0x3F;
        int[] operands = {rs, rt, rd, sa, fun};

        String mnemonic = MnemonicUtils.getMnemonicForRType(opcode, fun, rs);
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void simulateJType(Configuration configuration, int opcode, int instructionWord) {
        int iindex = (instructionWord << 6) >> 6;
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
