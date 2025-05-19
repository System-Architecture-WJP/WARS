package com.wars.simulator;

import com.wars.instruction.InstructionRegistry;

public class Simulator {
    public static Configuration simulate(int[] ints) {
        var configuration = new Configuration(1024); // TODO how much memory???

        for (int bin : ints) {
            simulateStep(configuration, bin);
        }
        return configuration;
    }

    private static void simulateStep(Configuration configuration, int bin) {
        int opcode = (bin >> 26);

        if (opcode == 0b000000 || opcode == 0b010000) {
            simulateRType(configuration, bin);
        } else if (opcode == 0b000010 || opcode == 0b000011) {
            simulateJType(configuration, bin);
        } else {
            simulateIType(configuration, bin);
        }
    }

    private static void simulateIType(Configuration configuration, int bin) {
        int opcode = (bin >> 26);
        int rs = (bin >>> 21);
        int rt = (bin >>> 16);
        int imm = bin & 0xFFFF;

        String mnemonic = InstructionRegistry.getByOpcode(opcode);
        int[] operands = {rs, rt, imm};
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void simulateRType(Configuration configuration, int bin) {
        int opcode = (bin >> 26);
        int rs = (bin >>> 21);
        int rt = (bin >>> 16);
        int rd = (bin >>> 11);
        int sa = (bin >>> 6);
        int fun = bin & 0x3F;

        String mnemonic = InstructionRegistry.getByOpcode(opcode);
        int[] operands = {rs, rt, rd, sa, fun};
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void simulateJType(Configuration configuration, int bin) {
        int opcode = (bin >> 26);
        int iindex = bin & 0x03FFFFFF;

        String mnemonic = InstructionRegistry.getByOpcode(opcode);
        int[] operands = {iindex};
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void executeInstruction(Configuration configuration, String mnemonic, int[] operands) {
        var instruction = InstructionRegistry.getExecutableInstruction(mnemonic, operands);
        instruction.execute(configuration);
    }

}
