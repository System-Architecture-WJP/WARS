package com.wars.instruction;

@FunctionalInterface
interface InstructionExecutor {
    Instruction execute(int[] operands);
}
