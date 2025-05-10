package com.wars;

import com.wars.instruction.Instruction;
import com.wars.instruction.InstructionDescriptor;
import com.wars.instruction.InstructionRegistry;

import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        String code = """
                # example
                sw 0 1 0
                addi 2 1 4
                addi 4 3 -1
                """;

        Arrays.stream(code.split("\\n")).forEach(App::extract);
    }

    private static void extract(String line) {
        if (line.isBlank() || line.startsWith("#")) return;

        String[] split = line.split("\\s+");
        String mnemonic = split[0];
        String[] operands = Arrays.copyOfRange(split, 1, split.length);

        InstructionDescriptor desc = InstructionRegistry.get(mnemonic);
        int[] parsedOperands = desc.parseOperands(operands);
        Instruction instruction = desc.create(parsedOperands);
        System.out.println(instruction.toBinaryString());
    }
}
