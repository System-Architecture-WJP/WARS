package com.wars.simulator;

import java.nio.channels.Pipe.SourceChannel;

import com.wars.exception.SimulatorException;
import com.wars.instruction.InstructionRegistry;

public class Simulator {

    public static boolean SIMULATION = false;

    public static Configuration simulate(int[] ints) {
        var configuration = new Configuration(1024); // TODO how much memory???

        for (int bin : ints) {
            simulateStep(configuration, bin);
        }
        return configuration;
    }

    public static Configuration simulate(Configuration configuration) {

        SIMULATION = true;
        int steps = 0;
        int N = (1 << 15);
        while (SIMULATION){
            int PC = (int) configuration.getPC();
            if (PC + 3 >= configuration.getMemory().length){
                throw new SimulatorException("PC refers instruction outside of Memory: " + configuration.getPC());
            }
            if (PC % 4 != 0){
                throw new SimulatorException("fetch address is not word aligned: " + PC);
            }
            int instr = configuration.getWord(PC);
            simulateStep(configuration, instr);
            
            if (steps > N){
                SIMULATION = false;
            }
            steps ++;
        }

        return configuration;
    }

    private static void simulateStep(Configuration configuration, int bin) {
        int opcode = (bin >>> 26);

        if (opcode == 0b000000 || opcode == 0b010000) {
            simulateRType(configuration, bin);
        } else if (opcode == 0b000010 || opcode == 0b000011) {
            simulateJType(configuration, bin);
        } else {
            simulateIType(configuration, bin);
        }
    }

    private static void simulateIType(Configuration configuration, int bin) {
        int opcode = (bin >>> 26);
        int rs = (bin >>> 21) & 31;
        int rt = (bin >>> 16) & 31;
        int imm = (bin << 16) >> 16;

        
        String mnemonic = InstructionRegistry.getInstruction(opcode, rt);
        int[] operands = {rs, rt, imm};
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void simulateRType(Configuration configuration, int bin) {
        int opcode = (bin >>> 26);
        int rs = (bin >>> 21) & 31;
        int rt = (bin >>> 16) & 31;
        int rd = (bin >>> 11) & 31;
        int sa = (bin >>> 6) & 31;
        int fun = bin & 0x3F;
        String mnemonic = InstructionRegistry.getInstruction(opcode, fun);
        int[] operands = {rd, rs, rt, sa, fun};
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void simulateJType(Configuration configuration, int bin) {
        int opcode = (bin >>> 26);
        int iindex = (bin << 6) >> 6;
        String mnemonic = InstructionRegistry.getByOpcode(opcode);
        int[] operands = {iindex};
        executeInstruction(configuration, mnemonic, operands);
    }

    private static void executeInstruction(Configuration configuration, String mnemonic, int[] operands) {
        var instruction = InstructionRegistry.getExecutableInstruction(mnemonic, operands);
        instruction.execute(configuration);
    }

}
