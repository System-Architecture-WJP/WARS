package com.wars.instruction;

import java.util.List;

public interface InstructionCreator {
    /*
     * Operand order in the list matches the order in the assembly syntax. Examples:
     * addi rt rs imm -> operands[0] = rt, operands[1] = rs, operands[2] = imm
     * lui rt imm -> operands[0] = rt, operands[1] = imm
     */
    Instruction create(List<Integer> operands);
}
