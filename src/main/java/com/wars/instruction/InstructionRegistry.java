package com.wars.instruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructionRegistry {
    private static final Map<String, InstructionCreator> registry = new HashMap<>();

    static {
        register("addi", InstructionRegistry::addiInstruction);
    }

    private static void register(String label, InstructionCreator creator) {
        registry.put(label, creator);
    }

    public static Instruction create(String label, List<Integer> operands) {
        InstructionCreator creator = registry.get(label);
        if (creator == null) {
            // TODO: add exception here
        }
        return creator.create(operands);
    }

    private static Instruction addiInstruction(List<Integer> operands) {
        return new ITypeInstruction(001000, operands.get(1), operands.get(0),
                operands.get(2));
    }

}
